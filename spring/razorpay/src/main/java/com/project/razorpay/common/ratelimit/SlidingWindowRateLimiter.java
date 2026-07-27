package com.project.razorpay.common.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Distributed Sliding Window Log rate limiter backed by Redis.
 *
 * Algorithm:
 *  - For each rate-limited key, we maintain a Redis Sorted Set (ZSET) where:
 *      - score  = request timestamp (epoch millis)
 *      - member = a random UUID (unique per request, so simultaneous
 *                 requests in the same millisecond don't collide/overwrite
 *                 each other, which would happen if the timestamp itself
 *                 were used as the member)
 *  - On every check:
 *      1. Drop all entries older than the sliding window (score <= now - window).
 *      2. Count what's left -> that's the true request count in the last
 *         `windowSeconds`, giving exact sliding-window accuracy (no fixed-window
 *         boundary burst problem).
 *      3. If under the limit, record this request and allow it.
 *      4. If at/over the limit, deny it and compute how many seconds until
 *         the oldest request ages out of the window (Retry-After hint).
 *
 * Activated only when `app.rate-limit.method=sliding` is set.
 *
 * NOTE (not handled here): the read (removeRangeByScore + zCard) and the
 * later write (add) are two separate round-trips, not one atomic operation.
 * Under high concurrency, multiple requests can each read the same "under
 * limit" count before any of them writes, allowing a small overshoot past
 * maxRequestAllowed. For strict correctness under load, wrap this in a
 * Redis Lua script (EVAL) so prune+count+add happen atomically.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "sliding")
public class SlidingWindowRateLimiter implements RateLimiter {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public RateLimitResult check(String key, int maxRequestAllowed, long windowSeconds) {

        long nowMs = System.currentTimeMillis();

        // Lower bound of the sliding window: anything older than this is stale
        // and no longer counts toward the limit.
        long floorMs = nowMs - windowSeconds * 1000;

        String redisKey = "ratelimit:sliding:" + key;

        var zset = stringRedisTemplate.opsForZSet();

        // Step 1: Evict all timestamps that have fallen outside the window.
        // This keeps the ZSET size bounded to only "live" requests.
        zset.removeRangeByScore(redisKey, Double.NEGATIVE_INFINITY, floorMs);

        // Step 2: Remaining ZSET size = number of requests still inside the window.
        Long count = zset.zCard(redisKey);

        // Fail-Open Principle: if Redis returns null (unexpected/transient issue),
        // default to 0 rather than blocking the request — we prefer to risk
        // slightly over-allowing traffic over taking the whole system down
        // because the rate-limit store had a hiccup.
        long current = count != null ? count : 0;

        // Step 3: Limit already reached/exceeded -> deny.
        if (current >= maxRequestAllowed) {

            // Find the single oldest entry still in the window; once it ages
            // out, a slot frees up and the client can retry.
            var oldest = zset.rangeWithScores(redisKey, 0, 0);

            int retryAfter = 1; // sane fallback if we can't compute an exact value

            if (oldest != null && !oldest.isEmpty()) {
                Double oldestScore = oldest.iterator().next().getScore();

                if (oldestScore != null) {
                    // The moment the oldest request will fall outside the window.
                    long windowExpiresMs = oldestScore.longValue() + windowSeconds * 1000;
                    // Convert remaining time into whole seconds (rounded up so we
                    // never tell the client to retry too early).
                    retryAfter = (int) Math.ceil((windowExpiresMs - nowMs) / 1000.0);
                }
            }

            return RateLimitResult.denied(retryAfter);
        }

        // Step 4: Under the limit -> record this request in the ZSET.
        // Score = timestamp (used for pruning later), member = unique UUID
        // (so it never collides with another request at the same millisecond).
        zset.add(redisKey, UUID.randomUUID().toString(), nowMs);

        // Refresh TTL so the key auto-expires if this client goes idle,
        // preventing unbounded key growth in Redis. The +1s buffer avoids
        // the key expiring exactly at the window boundary due to timing jitter.
        stringRedisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds + 1));

        return RateLimitResult.allowed((int) (maxRequestAllowed - current - 1));
    }
}