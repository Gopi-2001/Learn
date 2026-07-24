package com.project.razorpay.merchant.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;


@Component
@Slf4j
@RequiredArgsConstructor
public class RedisApiKeyCache implements ApiKeyCache {

    private static final String REDIS_KEY_PREFIX = "apikey:";
    private static final Duration REDIS_TTL = Duration.ofMinutes(5);

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public Optional<ApiKeyCacheEntry> get(String keyId) {

        try {
            String json = stringRedisTemplate.opsForValue().get(REDIS_KEY_PREFIX + keyId);

            if(json == null) {
                return Optional.empty();
            }

            return Optional.of(objectMapper.readValue(json, ApiKeyCacheEntry.class));

        } catch (Exception e) {
            log.warn("ApiKey cache read filed, keyId: {}", keyId);
            return Optional.empty();
        }
    }

    @Override
    public void put(String keyId, ApiKeyCacheEntry entry) {
            try {

                stringRedisTemplate.opsForValue().set(REDIS_KEY_PREFIX + keyId,
                        objectMapper.writeValueAsString(entry),
                        REDIS_TTL);

            } catch (Exception e){
                log.warn("ApiKey cache put filed, keyId: {}", keyId);
            }
    }

    @Override
    public void evict(String keyId) {

        stringRedisTemplate.delete(REDIS_KEY_PREFIX + keyId);

    }
}
