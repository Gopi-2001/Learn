package com.project.razorpay.common.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Token Bucket rate limiter backed by Redis, made atomic via a Lua script.
 *
 * Unlike the sliding-window limiters (which count exact requests in a
 * rolling time window), Token Bucket models a bucket that:
 *   - holds up to `capacity` tokens (here, capacity = maxRequestAllowed)
 *   - refills continuously at `refillPerSec` tokens/second
 *   - requires 1 token per request; if none available, the request is denied
 *
 * This allows short bursts up to the full bucket capacity, then throttles
 * down to the steady refill rate — different trade-off than the sliding
 * window limiters, which enforce a hard cap with no burst allowance beyond
 * the configured limit.
 *
 * State per client is just 2 fields (tokens, last-refill-timestamp) stored
 * in a Redis Hash, refilled lazily (computed on each check) rather than via
 * a background job — so no scheduler is needed.
 *
 * Activated only when app.rate-limit.method=bucket is set.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "bucket")
public class TokenBucketRateLimiter implements RateLimiter {

    /*
     * Atomic token bucket check-and-consume via Lua.
     *
     * KEYS[1] = redis key (per rate-limited entity)
     * ARGV[1] = bucket capacity (max tokens the bucket can hold)
     * ARGV[2] = refill rate, in tokens per second
     * ARGV[3] = now, in milliseconds
     * ARGV[4] = TTL in seconds to set on the key, so idle clients' state
     *           is reclaimed by Redis instead of persisting forever
     *
     * Returns a 3-element array (a Lua/Redis script returns one value, so
     * everything the caller needs is packed into this array):
     *   [0] = 1 (allowed) or 0 (denied)
     *   [1] = tokens remaining in the bucket after this check (floored)
     *   [2] = suggested Retry-After in seconds (0 if allowed)
     */
    private static final RedisScript<List> SCRIPT = new DefaultRedisScript<>("""
            local key = KEYS[1]
            local capacity = tonumber(ARGV[1])
            local refillPerSec = tonumber(ARGV[2])
            local nowMs = tonumber(ARGV[3])
            local ttlSeconds = tonumber(ARGV[4])

            -- Read current bucket state: tokens available and when we last refilled. 
            -- HMGET reads multiple fields from a Redis Hash in one round-trip.
            -- Here `key` is a Hash with two fields: 'tokens' (current token
            -- count) and 'ts' (last refill timestamp, ms). Grouping them under
            -- one key means one EXPIRE covers both, and they're always read
            -- together consistently instead of via two separate string keys.
            -- If this key has never been written before (first request ever
            -- for this client), Redis returns nil for each field, which is
            -- why we check `tokens == nil` below and initialize a full bucket.
            local data = redis.call('HMGET', key, 'tokens', 'ts')
            local tokens = tonumber(data[1])
            local lastTs = tonumber(data[2])

            -- First-ever request for this key: start with a full bucket.
            if tokens == nil then
                tokens = capacity
                lastTs = nowMs
            end

            -- Lazy refill: top up tokens based on time elapsed since last check,
            -- capped so the bucket never exceeds its capacity.
            local elapsedSec = math.max(0, (nowMs - lastTs) / 1000)
            tokens = math.min(capacity, tokens + elapsedSec * refillPerSec)

            local allowed = 0

            -- Consume 1 token if available; this check + the refill above happen
            -- atomically within this script, so concurrent requests can't both
            -- see the same "1 token available" state and both get allowed.
            if tokens >= 1 then
                tokens = tokens - 1
                allowed = 1
            end

            -- Persist updated bucket state and refresh TTL.            
            -- HMSET writes multiple fields into the Hash in one atomic call,
            -- so 'tokens' and 'ts' are updated together and never fall out of
            -- sync with each other. Note: HMSET is deprecated since Redis 4.0
            -- in favor of HSET (which now also accepts multiple field-value
            -- pairs), but HMSET is kept for backward compatibility and still
            -- works fine here.
            redis.call('HMSET', key, 'tokens', tokens, 'ts', nowMs)
            redis.call('EXPIRE', key, ttlSeconds)

            local retryAfter = 0

            -- If denied, estimate how many seconds until at least 1 token
            -- will be available again at the current refill rate.
            if allowed == 0 then
                retryAfter = math.ceil((1 - tokens) / refillPerSec)
            end

            return {allowed, math.floor(tokens), retryAfter};
            """, List.class);

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public RateLimitResult check(String key, int maxRequestAllowed, long windowSeconds) {

        try {
            String redisKey = "ratelimit:tokenBucket:" + key;

            // Reuse the existing (limit, window) config to derive a steady
            // refill rate: e.g. 100 requests / 60s => ~1.67 tokens/sec,
            // while still allowing a burst of up to 100 requests at once
            // if the bucket is full (that's the defining trait of Token
            // Bucket vs. a hard sliding-window cap).
            double refillPerSec = (double) maxRequestAllowed / windowSeconds;

            // Keep bucket state around for two windows of inactivity before
            // Redis reclaims it, so a client who pauses briefly doesn't lose
            // their accumulated tokens or reset unexpectedly.
            long ttlSeconds = windowSeconds * 2;

            List<Long> result = stringRedisTemplate.execute(SCRIPT,
                    List.of(redisKey),
                    String.valueOf(maxRequestAllowed),
                    String.valueOf(refillPerSec),
                    String.valueOf(System.currentTimeMillis()),
                    String.valueOf(ttlSeconds)
            );

            boolean allowed = result.get(0) == 1L;
            int remaining = result.get(1).intValue();
            int retryAfter = result.get(2).intValue();

            return allowed ? RateLimitResult.allowed(remaining) : RateLimitResult.denied(retryAfter);

        } catch (Exception e) {
            // Redis unavailable / script failure -> fail open. Same trade-off
            // as the other limiters: prefer letting traffic through over
            // taking the whole API down when the rate-limit store itself
            // is having problems.
            log.warn("Rate limiter unavailable, failing open for key={}", key, e);

            return RateLimitResult.allowed(maxRequestAllowed);
        }
    }
}