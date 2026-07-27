# Cache Stampede & Cache Penetration — Notes

Two of the most common failure modes in any cache-in-front-of-a-database design. Both can take down your backend even though "the cache is supposed to protect it."

---

## 1. Cache Stampede (a.k.a. Thundering Herd / Dog-Piling)

### What it is

A **cache stampede** happens when a cached key **expires (or is evicted)**, and at that exact moment, a large number of concurrent requests for that same key all miss the cache simultaneously. Every one of those requests then goes to the database at once to recompute/refetch the same value — hammering the DB with duplicate, redundant load for something that's about to be cached again anyway.

```
Cache TTL expires
        │
        ▼
1000 concurrent requests for the same key arrive
        │
        ▼
All 1000 see a cache MISS
        │
        ▼
All 1000 hit the database at the same time
        │
        ▼
DB gets a sudden spike of duplicate queries → possible overload/timeout
```

This is especially dangerous for **hot keys** — e.g., a trending product page, a popular user's profile, a homepage banner config — where thousands of requests per second are reading the *exact same key*.

### Why it happens

- A popular key's TTL expires and many requests arrive in the same instant.
- A cache node restarts/crashes, wiping a large chunk of the cache at once.
- A deploy triggers a cold cache (nothing warmed up yet) right as traffic ramps.

### How to handle it

#### a) Mutex / Locking (a.k.a. "Cache Lock" / "Single Flight")

Only **one** request is allowed to recompute the value on a miss; everyone else either waits briefly or gets a stale value, instead of all of them hitting the DB.

```java
public String getWithLock(String key) {
    String value = redisTemplate.opsForValue().get(key);
    if (value != null) {
        return value; // cache hit
    }

    String lockKey = "lock:" + key;
    // Try to acquire a short-lived lock; only one caller succeeds (SETNX semantics)
    Boolean acquired = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, "1", Duration.ofSeconds(5));

    if (Boolean.TRUE.equals(acquired)) {
        try {
            // We got the lock: recompute from the DB and repopulate the cache
            value = database.fetch(key);
            redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10));
            return value;
        } finally {
            redisTemplate.delete(lockKey);
        }
    } else {
        // Someone else is already recomputing — briefly wait and retry the cache,
        // or return a slightly stale/fallback value instead of also hitting the DB
        sleep(50);
        return getWithLock(key); // retry (bound this with a max attempt count in practice)
    }
}
```

**Key idea:** `setIfAbsent` (Redis `SET key val NX`) is atomic — only one concurrent caller can win the lock, so only one goes to the DB while the rest wait for the cache to be repopulated.

#### b) Probabilistic Early Expiration (a.k.a. "XFetch")

Instead of waiting for a hard TTL expiry, let requests **probabilistically recompute the value slightly before it actually expires** — spreading recomputation out over time instead of all at once at the exact expiry moment.

```java
public String getWithEarlyRecompute(String key) {
    CachedValue cached = getFromCacheWithMeta(key); // value + computedAt + ttl + computeDurationEstimate

    if (cached == null) {
        return recomputeAndCache(key);
    }

    double delta = cached.computeDurationEstimate;
    double beta = 1.0; // tuning factor — higher = recompute earlier/more often
    long now = System.currentTimeMillis();

    // The closer we are to expiry, the higher the probability of recomputing NOW
    boolean shouldRecompute = (now - delta * beta * Math.log(Math.random())) >= cached.expiresAt;

    if (shouldRecompute) {
        return recomputeAndCache(key); // this request "volunteers" to refresh early
    }
    return cached.value; // everyone else just gets the still-valid cached value
}
```

This avoids the all-at-once cliff-edge of a hard TTL: a few requests recompute early (staggered probabilistically), so by the time the "real" expiry hits, the value has usually already been refreshed by someone.

#### c) Logical/Soft TTL + Background Refresh (a.k.a. "Stale-While-Revalidate")

Store the value with **no hard Redis TTL** (or a much longer one), but track a separate "logical expiry" timestamp in the cached payload itself.

- If `now < logicalExpiry` → serve the cached value, done.
- If `now >= logicalExpiry` → serve the (slightly stale) cached value **immediately**, but kick off an async background refresh so the *next* request gets a fresh value. Nobody waits, nobody blocks, and the DB only gets hit once per stale key.

```java
public String getStaleWhileRevalidate(String key) {
    CachedValue cached = getFromCache(key);

    if (cached == null) {
        return recomputeAndCache(key); // true cold cache — must block here
    }

    if (System.currentTimeMillis() >= cached.logicalExpiry) {
        // Serve stale immediately, refresh in the background (don't block the caller)
        executor.submit(() -> recomputeAndCache(key));
    }

    return cached.value;
}
```

This trades strict freshness for zero-latency-impact recomputation, and is a very common pattern for high-traffic read paths (CDNs use this exact idea via the `stale-while-revalidate` HTTP `Cache-Control` directive).

#### d) Jittered TTLs

Simplest mitigation, though not a complete fix on its own: instead of every related key expiring at exactly the same TTL, add a small random jitter so they expire at slightly different times, spreading out recompute load.

```java
long baseTtlSeconds = 600;
long jitter = ThreadLocalRandom.current().nextLong(0, 60); // 0–60s of random jitter
redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(baseTtlSeconds + jitter));
```

Good as a cheap first line of defense, but doesn't help a single very-hot key expiring once and getting slammed by thousands of simultaneous readers — combine with (a) or (c) for that.

---

## 2. Cache Penetration

### What it is

**Cache penetration** happens when requests query for keys that **don't exist in the database at all** (e.g., an invalid ID, a non-existent user, a made-up SKU). Since the value doesn't exist, it's never cached — so **every single request for that key bypasses the cache entirely and hits the database**, every time.

```
Request for nonExistentId=99999
        │
        ▼
Cache MISS (nothing was ever cached, because DB has no such record)
        │
        ▼
DB query → "not found"
        │
        ▼
Nothing gets cached (there's no value to cache)
        │
        ▼
Next identical request repeats the exact same DB round-trip, forever
```

This is especially dangerous when it's **exploited deliberately** — an attacker deliberately floods your API with requests for random/non-existent IDs specifically to bypass your cache and hammer the database directly (a cache-aware form of DoS).

### How to handle it

#### a) Cache the "Not Found" result too (Negative Caching)

Instead of only caching real values, also cache a sentinel/marker for "this key doesn't exist," with a **short TTL** (so a legitimately-created record later isn't hidden by a stale "not found" entry for too long).

```java
private static final String NULL_MARKER = "__NULL__";

public String getWithNegativeCache(String key) {
    String cached = redisTemplate.opsForValue().get(key);

    if (cached != null) {
        return NULL_MARKER.equals(cached) ? null : cached;
    }

    String dbValue = database.fetch(key); // may be null if not found

    if (dbValue == null) {
        // Cache the "not found" result too, with a short TTL
        redisTemplate.opsForValue().set(key, NULL_MARKER, Duration.ofMinutes(2));
        return null;
    }

    redisTemplate.opsForValue().set(key, dbValue, Duration.ofMinutes(10));
    return dbValue;
}
```

Now repeated lookups for the same non-existent key hit Redis (cheap) instead of the DB (expensive) — but a flood of requests for many *different* non-existent keys (e.g., random IDs) would still fill up Redis with junk `__NULL__` entries and still touch the DB once per unique bad key. That's where a Bloom filter helps.

#### b) Bloom Filter (pre-check before touching cache or DB at all)

A **Bloom filter** is a space-efficient probabilistic data structure that can tell you, extremely cheaply, "this key is **definitely not** present" or "this key **might be** present" (never a false negative, occasionally a false positive). Load it once with the full set of *valid* keys/IDs from your DB (e.g., all real user IDs), and check it before even touching the cache:

```java
public String getWithBloomFilter(String key) {
    if (!bloomFilter.mightContain(key)) {
        // Definitely does not exist — short-circuit here.
        // No cache lookup, no DB call, no wasted round-trip at all.
        return null;
    }

    // Might exist — proceed with normal cache-then-DB flow.
    return getWithNegativeCache(key);
}
```

Using Guava's Bloom filter:
```java
BloomFilter<String> bloomFilter = BloomFilter.create(
        Funnels.stringFunnel(Charset.defaultCharset()),
        10_000_000,  // expected number of valid keys
        0.01         // acceptable false-positive rate (1%)
);

// Populate once at startup (or refresh periodically) from your source of truth
allValidUserIds.forEach(bloomFilter::put);
```

Also available as a native Redis data structure via the **RedisBloom** module (`BF.ADD`, `BF.EXISTS`), which is preferable in a distributed system so every app instance shares the same filter instead of keeping separate in-memory copies that can drift out of sync.

This is the strongest defense against a deliberate attack flooding random/non-existent keys — the Bloom filter check is essentially free (microseconds, no network call) and rejects the vast majority of garbage requests before they ever reach Redis or the database.

#### c) Input validation / request-level guards

Cheap, complementary first line of defense — reject obviously malformed requests before they even reach your caching layer:
- ID format/range checks (e.g., reject non-numeric IDs for a numeric-ID system).
- Rate-limit per client/IP specifically on cache-miss-heavy endpoints (ties back into the rate limiter work already in this project — a spike in "not found" responses from one client is a good signal to throttle harder).

---

## Quick Comparison

| | Cache Stampede | Cache Penetration |
|---|---|---|
| **Trigger** | A *valid, hot* key expires/evicts | Requests for keys that **never existed** |
| **Symptom** | Sudden burst of duplicate DB load at expiry | Steady/attacker-driven DB load bypassing cache entirely |
| **Primary fixes** | Mutex/lock on recompute, probabilistic early expiry, stale-while-revalidate, jittered TTLs | Negative caching (cache the "not found"), Bloom filter pre-check |
| **Best combo** | Stale-while-revalidate + jitter for most systems; add locking for extremely hot single keys | Bloom filter (cheap, strong) + negative caching (covers the small false-positive-rate gap) |

## General Principle

Both problems come from the same root cause: **a cache-miss doesn't just mean "go fetch it" — it means "something unusual might be happening," and naive cache-aside logic treats every miss the same way.** The fixes above all boil down to distinguishing:
1. A miss because the value is legitimately absent and rare (→ negative cache / Bloom filter).
2. A miss because many callers raced the same expiry at once (→ locking / stale-while-revalidate / jitter).

Handling both well means your cache stays a genuine shock-absorber for your database instead of becoming a pass-through under exactly the conditions (hot keys, malicious traffic) when you need it most.