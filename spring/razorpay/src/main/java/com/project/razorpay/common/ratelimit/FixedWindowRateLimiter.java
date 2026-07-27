package com.project.razorpay.common.ratelimit;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "fixed")
public class FixedWindowRateLimiter implements RateLimiter {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public RateLimitResult check(String key, int maxRequestAllowed, long windowSeconds) {

        String redisKey = "ratelimit:fixed" + key;

        Long count = stringRedisTemplate.opsForValue().increment(redisKey);

        if(count == null) return RateLimitResult.allowed(maxRequestAllowed);

        if(count == 1)  {
            stringRedisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds));
        }

        if(count > maxRequestAllowed) {
            Long ttl = stringRedisTemplate.getExpire(redisKey, TimeUnit.SECONDS);

            int retryAfter = (ttl != null & ttl>0) ? ttl.intValue() : (int) windowSeconds;

            return RateLimitResult.denied(retryAfter);
        }

        return RateLimitResult.allowed((int)(maxRequestAllowed - count));
    }

}

/*
* Here is the detailed breakdown of the entire method, focusing heavily on the null check and the count == 1 logic to clear up your confusion.
*
## The Full Code Flow Walkthrough
```
@Overridepublic RateLimitResult check(String key, int maxRequestAllowed, long windowSeconds) {
    String redisKey = "ratelimit:fixed" + key;

    // 1. Increment the counter
    Long count = stringRedisTemplate.opsForValue().increment(redisKey);
```
*
## Step 1: The Counter Increment

* increment(redisKey) acts as a "create or update" command in Redis.
* If the key does not exist: Redis automatically creates it with a value of 0 and then increments it to 1.
* If the key already exists: Redis simply adds 1 to the existing number.

------------------------------
## Part 1: Explaining the null Part
```
    // 2. The Null Check
    if(count == null) return RateLimitResult.allowed(maxRequestAllowed);
```
## Why could count be null?
In standard standalone Redis operations, increment will never return null if it successfully hits the database. However, Spring Data Redis's stringRedisTemplate.opsForValue().increment() returns a Java Long object wrapper (not a primitive long).
It can return null in a few specific scenarios: [1]

   1. Connection Timeout/Failure: If the Redis server goes down or a network glitch occurs during the command execution.
   2. Mocking/Testing: If you are running unit tests and your mock Redis template is not configured properly to return a value.

## Why does it allow the request if it is null?
This is a design pattern called Fail-Open.

* If your Redis server crashes, you do not want your entire application to crash or block your users.
* By returning RateLimitResult.allowed(...), the code safely says: "We cannot reach the rate limiter right now, so let's trust the user and allow the traffic through rather than breaking the application."

------------------------------
## Part 2: Explaining the count == 1 Part
```
    // 3. The Window Initialization
    if(count == 1) {
        stringRedisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds));
    }
```
## Why check for exactly 1?

* When count == 1, it proves that this execution was the exact moment the key was born in Redis.
* This is your only chance to set the lifespan (Time To Live / TTL) of this rate limit window.

## What happens if we don't do this?
If you forget to set the expire when count == 1, the key will stay in Redis forever. The counter will keep climbing indefinitely (2, 3, 4... 10000), and the user will be blocked permanently after hitting the limit once.
## Why not set the expiration on every single request?
If you updated the expiration on every single request (e.g., without the if(count == 1) check), you would accidentally create a Sliding Window instead of a Fixed Window.

* Every time the user sends a request, the timer would reset back to windowSeconds.
* A fast user would keep pushing the expiration forward, trapping themselves in a blocked state forever. Checking count == 1 ensures the window timer is set once and only once at the very beginning.

------------------------------
## Part 3: The Rest of the Code
```
    // 4. Checking the limit
    if(count > maxRequestAllowed) {
        Long ttl = stringRedisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        int retryAfter = (ttl != null && ttl > 0) ? ttl.intValue() : (int) windowSeconds;
        return RateLimitResult.denied(retryAfter);
    }

    // 5. Request allowed
    return RateLimitResult.allowed((int)(maxRequestAllowed - count));
}
```

* Step 4 (Blocked): If count is higher than allowed, it calculates how many seconds are left until the key self-destructs (ttl). It tells the client: "You are blocked. Try again in retryAfter seconds."
* Step 5 (Allowed): If the count is within limits, it allows the request and returns how many remaining requests the user can make in this window.

------------------------------

* */
