package com.project.razorpay.common.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Distributed Sliding Window Log rate limiter backed by Redis, made ATOMIC
 * via a Lua script (unlike the plain Java+Redis version, which does the
 * prune/count/add as separate round-trips and can slightly overshoot the
 * limit under concurrent load).
 *
 * Why Lua: Redis executes a script as a single, uninterruptible operation.
 * By moving "prune -> count -> decide -> add" entirely into the script, no
 * other client's request can be interleaved between the count and the add,
 * closing the race condition present in the non-Lua implementation.
 *
 * Data model (same sliding-window-log approach as before):
 *   - ZSET per rate-limited key: score = request timestamp (ms),
 *     member = random UUID (guarantees uniqueness even if two requests
 *     land in the same millisecond, which a timestamp-as-member scheme
 *     would silently collapse into one entry).
 *
 * Activated only when app.rate-limit.method=sliding-lua is set.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.rate-limit.method", havingValue = "sliding-lua")
public class SlidingWindowLuaLimiter implements RateLimiter {

    private final StringRedisTemplate stringRedisTemplate;

    /*
     * Atomic sliding window via Lua.
     *
     * KEYS[1] = redis key (per rate-limited entity)
     * ARGV[1] = now in milliseconds
     * ARGV[2] = window floor in milliseconds (now - windowMs) — anything
     *           scored at or before this is stale and gets pruned
     * ARGV[3] = max requests allowed in the window
     * ARGV[4] = TTL in seconds to set on the key (window + buffer), so idle
     *           clients' keys self-expire instead of growing Redis forever
     * ARGV[5] = unique member id (UUID) representing this specific request
     *
     * Returns a 3-element array, since a Lua/Redis script can only return
     * one value — packing everything the caller needs into one array avoids
     * extra round-trips:
     *   [0] = 1 (allowed) or 0 (denied)
     *   [1] = remaining request quota after this one (0 if denied)
     *   [2] = score (timestamp) of the oldest member still in the window —
     *         used by the caller to compute an accurate Retry-After;
     *         0 if the request was allowed (not needed in that case)
     */
    private static final String SLIDING_WINDOW_LUA = """
            local key = KEYS[1]
            local now = tonumber(ARGV[1])
            local floor = tonumber(ARGV[2])
            local limit = tonumber(ARGV[3])
            local ttl  = tonumber(ARGV[4])
            local member = ARGV[5]

            -- Step 1: prune every entry that has aged out of the sliding window.
            redis.call('ZREMRANGEBYSCORE', key,'-inf',floor)

            -- Step 2: count what's left -> true request count in the current window.
            local count = redis.call('ZCARD',key)

            if count >= limit then
                -- Step 3a: over the limit -> deny WITHOUT adding this request.
                -- Fetch the single oldest surviving member so the caller can
                -- work out exactly when a slot frees up (Retry-After).
                local oldest = redis.call('ZRANGE',key,0,0,'WITHSCORES')
                local oldestScore = 0

                if #oldest > 0 then
                    oldestScore = tonumber(oldest[2])
                end

                return {0,0,oldestScore}
            end

            -- Step 3b: under the limit -> record this request and refresh TTL.
            -- Because this all runs inside the same script invocation, the
            -- "count was under limit" check and this "add" are atomic w.r.t.
            -- every other client hitting this key.
            redis.call('ZADD', key, now, member)
            redis.call('EXPIRE',key,ttl)

            local remaining  = limit - count - 1

            return {1, remaining, 0 }
            """;

    // Pre-compiled/registered script handle; Spring Data Redis will send the
    // script's SHA to Redis (EVALSHA) rather than the full source on every
    // call once Redis has cached it, so this is cheap to reuse.
    private final RedisScript<List<Long>> script =
            RedisScript.of(SLIDING_WINDOW_LUA, (Class<List<Long>>) (Class<?>) List.class);

    @Override
    public RateLimitResult check(String key, int maxRequests, long windowSeconds) {

        try {
            long nowMs = System.currentTimeMillis();
            long floorMs = nowMs - windowSeconds * 1000;
            // Unique per-request identifier, used as the ZSET member so
            // simultaneous requests never overwrite each other.
            String member = UUID.randomUUID().toString();

            // Single atomic round-trip: prune + count + decide + (maybe) add,
            // all executed server-side as one unit of work.
            List<Long> result = stringRedisTemplate.execute(
                    script,
                    List.of("ratelimit:sliding:" + key),
                    String.valueOf(nowMs),
                    String.valueOf(floorMs),
                    String.valueOf(maxRequests),
                    String.valueOf(windowSeconds + 1), // +1s buffer before key expiry
                    member
            );

            if (result == null || result.isEmpty()) {
                // Redis unavailable / unexpected empty reply -> fail open.
                // We choose availability over strict enforcement: better to
                // let traffic through than to take the whole API down because
                // the rate-limit store hiccuped.
                return RateLimitResult.allowed(maxRequests);
            }

            boolean allowed = result.get(0) == 1L;
            int remaining = result.get(1).intValue();
            long oldestScoreMs = result.get(2);

            if (!allowed) {
                // Compute seconds until the oldest request ages out of the
                // window (i.e., when a slot will free up). Falls back to the
                // full window size if we somehow got no oldest score.
                int retryAfter = oldestScoreMs > 0
                        ? (int) Math.max(1, (oldestScoreMs + windowSeconds * 1000 - nowMs) / 1000)
                        : (int) windowSeconds;

                return RateLimitResult.denied(retryAfter);
            }

            return RateLimitResult.allowed(remaining);

        } catch (DataAccessException e) {
            // Redis connectivity/command failure -> fail open (same rationale
            // as the null-result branch above), but log it so ops can see the
            // rate limiter is degraded rather than failing silently.
            log.warn("Rate limiter unavailable, failing open for key={}", key, e);

            return RateLimitResult.allowed(maxRequests);
        }
    }
}