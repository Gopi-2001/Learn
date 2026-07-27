# Rate Limiting — Notes with Java Implementations

> Sources: [awesome-system-design-resources (Java implementations)](https://github.com/ashishps1/awesome-system-design-resources/tree/main/implementations/java/rate_limiting) and [AlgoMaster — Rate Limiting Algorithms Explained with Code](https://blog.algomaster.io/p/rate-limiting-algorithms-explained-with-code)

## What is Rate Limiting?

Rate limiting controls how many requests a client (user, IP, API key) can make to a service within a given time period. It protects backend systems from being overwhelmed, prevents abuse (brute force, scraping, DDoS), ensures fair usage across clients, and helps control infrastructure cost.

When a client exceeds the allowed limit, the server typically rejects the request with an **HTTP 429 (Too Many Requests)** response, often along with headers like `Retry-After`, `X-RateLimit-Limit`, `X-RateLimit-Remaining`.

There are 5 classic algorithms used to implement rate limiting:

| # | Algorithm | Core Idea |
|---|-----------|-----------|
| 1 | Token Bucket | Tokens refill at a fixed rate; a request consumes a token |
| 2 | Leaky Bucket | Requests queue up and "leak" out at a constant rate |
| 3 | Fixed Window Counter | Count requests in fixed, non-overlapping time windows |
| 4 | Sliding Window Log | Keep a timestamp log and count entries within the rolling window |
| 5 | Sliding Window Counter | Weighted blend of current + previous fixed window counts |

---

## 1. Token Bucket

**How it works:**
- A bucket holds a maximum number of tokens (`capacity`).
- Tokens are added to the bucket at a fixed rate (`fillRate` tokens/second).
- Every incoming request must consume one (or more) tokens to proceed.
- If enough tokens are available, the request is allowed and tokens are deducted.
- If not enough tokens are available, the request is rejected.

**Pros**
- Simple to implement and reason about.
- Allows short bursts of traffic up to the bucket's capacity.

**Cons**
- Memory scales with number of users if you keep one bucket per client.
- Doesn't guarantee a perfectly smooth/uniform rate of requests.

```java
package implementations.java.rate_limiting;

import java.time.Instant;

public class TokenBucket {
    private final long capacity;         // Maximum number of tokens the bucket can hold
    private final double fillRate;       // Rate at which tokens are added (tokens per second)
    private double tokens;               // Current number of tokens in the bucket
    private Instant lastRefillTimestamp; // Last time we refilled the bucket

    public TokenBucket(long capacity, double fillRate) {
        this.capacity = capacity;
        this.fillRate = fillRate;
        this.tokens = capacity;          // Start with a full bucket
        this.lastRefillTimestamp = Instant.now();
    }

    public synchronized boolean allowRequest(int tokens) {
        refill(); // First, add any new tokens based on elapsed time

        if (this.tokens < tokens) {
            return false; // Not enough tokens, deny the request
        }

        this.tokens -= tokens; // Consume the tokens
        return true;           // Allow the request
    }

    private void refill() {
        Instant now = Instant.now();
        // Calculate how many tokens to add based on the time elapsed
        double tokensToAdd = (now.toEpochMilli() - lastRefillTimestamp.toEpochMilli()) * fillRate / 1000.0;
        this.tokens = Math.min(capacity, this.tokens + tokensToAdd); // Don't exceed capacity
        this.lastRefillTimestamp = now;
    }
}
```

**Quick usage:**
```java
TokenBucket limiter = new TokenBucket(10, 2); // capacity=10 tokens, refill 2 tokens/sec
if (limiter.allowRequest(1)) {
    // process request
} else {
    // reject with HTTP 429
}
```

---

## 2. Leaky Bucket

**How it works:**
- Think of a bucket with a small hole at the bottom.
- Requests enter the bucket from the top (queued as timestamps).
- The bucket "leaks" (processes) requests at a constant rate.
- If the bucket is already full, new requests are dropped.

**Pros**
- Smooths out bursty traffic into a steady, predictable output rate.
- Good for protecting downstream systems that need a constant processing rate.

**Cons**
- Doesn't handle sudden bursts well — excess requests are dropped immediately rather than allowed through.
- Slightly more complex than Token Bucket.

```java
package implementations.java.rate_limiting;

import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;

public class LeakyBucket {
    private final long capacity;          // Maximum number of requests the bucket can hold
    private final double leakRate;        // Rate at which requests leak out (requests per second)
    private final Queue<Instant> bucket;  // Queue holding timestamps of requests
    private Instant lastLeakTimestamp;    // Last time we leaked from the bucket

    public LeakyBucket(long capacity, double leakRate) {
        this.capacity = capacity;
        this.leakRate = leakRate;
        this.bucket = new LinkedList<>();
        this.lastLeakTimestamp = Instant.now();
    }

    public synchronized boolean allowRequest() {
        leak(); // First, leak out any requests based on elapsed time

        if (bucket.size() < capacity) {
            bucket.offer(Instant.now()); // Add the new request to the bucket
            return true;                 // Allow the request
        }
        return false; // Bucket is full, deny the request
    }

    private void leak() {
        Instant now = Instant.now();
        long elapsedMillis = now.toEpochMilli() - lastLeakTimestamp.toEpochMilli();
        int leakedItems = (int) (elapsedMillis * leakRate / 1000.0); // How many should have leaked

        for (int i = 0; i < leakedItems && !bucket.isEmpty(); i++) {
            bucket.poll();
        }
        lastLeakTimestamp = now;
    }
}
```

**Quick usage:**
```java
LeakyBucket limiter = new LeakyBucket(5, 1); // capacity=5, leaks 1 request/sec
if (limiter.allowRequest()) {
    // process request
} else {
    // reject with HTTP 429
}
```

---

## 3. Fixed Window Counter

**How it works:**
1. Time is divided into fixed windows (e.g., 1-minute intervals).
2. Each window has a counter starting at zero.
3. Every new request increments the counter for the current window.
4. If the counter exceeds the limit, requests are denied until the next window starts.

**Pros**
- Easiest algorithm to implement and understand.
- Gives clear, predictable limits per time window.

**Cons**
- **Boundary burst problem**: doesn't handle bursts at window edges well — up to 2x the intended rate can slip through right around a window boundary (e.g., a burst at the very end of one window plus a burst at the very start of the next).

```java
package implementations.java.rate_limiting;

import java.time.Instant;

public class FixedWindowCounter {
    private final long windowSizeInSeconds;  // Size of each fixed window
    private final long maxRequestsPerWindow; // Max requests allowed per window

    private long currentWindowStart;         // Epoch second when current window started
    private long requestCount;               // Requests seen in the current window

    public FixedWindowCounter(long windowSizeInSeconds, long maxRequestsPerWindow) {
        this.windowSizeInSeconds = windowSizeInSeconds;
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.currentWindowStart = Instant.now().getEpochSecond();
        this.requestCount = 0;
    }

    public synchronized boolean allowRequest() {
        long now = Instant.now().getEpochSecond();

        // Roll over to a new window if the current one has expired
        if (now - currentWindowStart >= windowSizeInSeconds) {
            currentWindowStart = now;
            requestCount = 0;
        }

        if (requestCount < maxRequestsPerWindow) {
            requestCount++;
            return true; // Allow the request
        }

        return false; // Limit exceeded for this window
    }
}
```

**Quick usage:**
```java
FixedWindowCounter limiter = new FixedWindowCounter(60, 100); // 100 requests / 60s window
if (limiter.allowRequest()) {
    // process request
} else {
    // reject with HTTP 429
}
```

---

## 4. Sliding Window Log

**How it works:**
1. Keep a log (list/queue) of timestamps for every request.
2. When a new request arrives, remove all entries older than the window size.
3. Count the remaining entries.
4. If the count is below the limit, allow the request and record its timestamp.
5. Otherwise, deny the request.

**Pros**
- Very accurate — no rough edges/boundary issues between windows.
- Works well for low-volume APIs.

**Cons**
- Memory-intensive at high volume since every request timestamp must be stored.
- Requires scanning/pruning the timestamp log on every request.

```java
package implementations.java.rate_limiting;

import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;

public class SlidingWindowLog {
    private final long windowSizeInMillis; // Size of the sliding window
    private final long maxRequests;        // Max requests allowed within the window
    private final Queue<Long> requestLog;  // Timestamps (epoch millis) of allowed requests

    public SlidingWindowLog(long windowSizeInMillis, long maxRequests) {
        this.windowSizeInMillis = windowSizeInMillis;
        this.maxRequests = maxRequests;
        this.requestLog = new LinkedList<>();
    }

    public synchronized boolean allowRequest() {
        long now = Instant.now().toEpochMilli();
        long windowStart = now - windowSizeInMillis;

        // Drop timestamps that have fallen outside the current window
        while (!requestLog.isEmpty() && requestLog.peek() <= windowStart) {
            requestLog.poll();
        }

        if (requestLog.size() < maxRequests) {
            requestLog.offer(now); // Record this request
            return true;          // Allow the request
        }

        return false; // Too many requests within the window
    }
}
```

**Quick usage:**
```java
SlidingWindowLog limiter = new SlidingWindowLog(60_000, 100); // 100 requests / rolling 60s
if (limiter.allowRequest()) {
    // process request
} else {
    // reject with HTTP 429
}
```

---

## 5. Sliding Window Counter

**How it works:**
This combines the Fixed Window Counter and Sliding Window Log approaches — accurate like the log, but memory-efficient like the fixed window (it only stores two counters instead of every timestamp).

1. Track request counts for the current window and the previous window.
2. Calculate a weighted sum based on how much the sliding window overlaps with the previous window:

```
weight = ((windowSize - elapsedTimeInCurrentWindow) / windowSize) * previousWindowCount + currentWindowCount
```

3. If `weight` (plus the incoming request) is below the limit, allow the request.

**Pros**
- More accurate than plain Fixed Window Counter (smooths out the boundary-burst problem).
- More memory-efficient than Sliding Window Log (constant space — just two counters).

**Cons**
- Slightly more complex to implement than Fixed Window.
- The "weighted" estimate is an approximation, not an exact count like Sliding Window Log.

```java
package implementations.java.rate_limiting;

import java.time.Instant;

public class SlidingWindowCounter {
    private final long windowSizeInSeconds;  // Size of each window
    private final long maxRequestsPerWindow; // Max requests allowed per window

    private long currentWindowStart;
    private long currentWindowCount;
    private long previousWindowCount;

    public SlidingWindowCounter(long windowSizeInSeconds, long maxRequestsPerWindow) {
        this.windowSizeInSeconds = windowSizeInSeconds;
        this.maxRequestsPerWindow = maxRequestsPerWindow;
        this.currentWindowStart = Instant.now().getEpochSecond();
        this.currentWindowCount = 0;
        this.previousWindowCount = 0;
    }

    public synchronized boolean allowRequest() {
        long now = Instant.now().getEpochSecond();
        long elapsedSinceWindowStart = now - currentWindowStart;

        // Roll the window forward if it has expired
        if (elapsedSinceWindowStart >= windowSizeInSeconds) {
            long windowsPassed = elapsedSinceWindowStart / windowSizeInSeconds;
            // If exactly one window has passed, current becomes previous.
            // If more than one window has passed, both counts reset to zero (no overlap left).
            previousWindowCount = (windowsPassed == 1) ? currentWindowCount : 0;
            currentWindowCount = 0;
            currentWindowStart += windowsPassed * windowSizeInSeconds;
            elapsedSinceWindowStart = now - currentWindowStart;
        }

        double overlapRatio = 1.0 - ((double) elapsedSinceWindowStart / windowSizeInSeconds);
        double weightedCount = (previousWindowCount * overlapRatio) + currentWindowCount;

        if (weightedCount + 1 > maxRequestsPerWindow) {
            return false; // Would exceed the limit
        }

        currentWindowCount++;
        return true; // Allow the request
    }
}
```

**Quick usage:**
```java
SlidingWindowCounter limiter = new SlidingWindowCounter(60, 100); // 100 requests / 60s
if (limiter.allowRequest()) {
    // process request
} else {
    // reject with HTTP 429
}
```

---

## Comparison Table

| Algorithm | Accuracy | Memory Usage | Burst Handling | Implementation Complexity |
|---|---|---|---|---|
| Token Bucket | Good | Low (per-client state) | Allows bursts up to capacity | Low |
| Leaky Bucket | Good | Medium (queue of requests) | Smooths bursts, drops overflow | Medium |
| Fixed Window Counter | Weak at edges | Low (one counter) | Boundary burst problem (up to 2x) | Lowest |
| Sliding Window Log | Very accurate | High (stores every timestamp) | Handles bursts precisely | Medium |
| Sliding Window Counter | Very accurate (approx.) | Low (two counters) | Smooths boundary bursts | Medium |

## Choosing an Algorithm

- **Need simplicity + burst tolerance** → Token Bucket (most widely used in practice, e.g., AWS API Gateway, Stripe).
- **Need a strictly smoothed, constant output rate** → Leaky Bucket.
- **Need the simplest possible implementation and can tolerate boundary bursts** → Fixed Window Counter.
- **Need perfect accuracy and traffic is low-volume** → Sliding Window Log.
- **Need near-perfect accuracy at scale with low memory overhead** → Sliding Window Counter.

## Practical Considerations

- **Thread-safety**: all implementations above use `synchronized` methods for simplicity; in high-throughput systems, consider `AtomicLong`/`AtomicReference`, striped locks, or lock-free structures instead.
- **Distributed systems**: in-memory state (as shown above) only works for a single instance. For multi-node deployments, back the limiter with a shared store like **Redis** (e.g., `INCR` + `EXPIRE` for Fixed Window, sorted sets `ZADD`/`ZREMRANGEBYSCORE` for Sliding Window Log, or Lua scripts for atomic Token Bucket refills).
- **Per-client granularity**: rate limiters are usually keyed by user ID, API key, or IP address — maintain one limiter instance per key (e.g., in a `ConcurrentHashMap<String, TokenBucket>`).
- **Communicate limits clearly**: return `429 Too Many Requests` with headers such as `Retry-After`, `X-RateLimit-Limit`, `X-RateLimit-Remaining`, and `X-RateLimit-Reset` so clients can implement proper retry/backoff.

## References

- Ashish Pratap Singh, [Rate Limiting Algorithms Explained with Code](https://blog.algomaster.io/p/rate-limiting-algorithms-explained-with-code) — AlgoMaster Newsletter
- [awesome-system-design-resources — Java rate limiting implementations](https://github.com/ashishps1/awesome-system-design-resources/tree/main/implementations/java/rate_limiting)