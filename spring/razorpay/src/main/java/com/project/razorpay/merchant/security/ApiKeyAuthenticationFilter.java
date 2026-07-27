package com.project.razorpay.merchant.security;

import com.project.razorpay.common.exception.RateLimitException;
import com.project.razorpay.common.ratelimit.RateLimitResult;
import com.project.razorpay.common.ratelimit.RateLimiter;
import com.project.razorpay.merchant.cache.ApiKeyCache;
import com.project.razorpay.merchant.cache.ApiKeyCacheEntry;
import com.project.razorpay.merchant.entity.ApiKey;
import com.project.razorpay.merchant.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String BASIC_PREFIX = "Basic ";
    private final ApiKeyRepository apiKeyRepository;
    private final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();
    private final MerchantContext merchantContext;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final ApiKeyCache apiKeyCache;
    private final RateLimiter rateLimiter;

    @Value("${app.rate-limit.use-case.api-key.request-per-minute:60}")
    private Integer requestPerMinute;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Incoming request: {}", request.getRequestURI());

        try {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith(BASIC_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String[] credentials = decode(header);

        if (credentials == null) {
            throw new BadCredentialsException("Malformed API Key Header");
        }

        String keyId = credentials[0];
        String rawSecret = credentials[1];

        ApiKeyCacheEntry apiKeyEntry = apiKeyCache.get(keyId)
                .orElseGet(() -> loadAndCache(keyId));

        // Previously, we were making database call and now we first check it in the cache and if found in cache that's great
        //  if not found in cache then we call loadAndCache method which will first load the data and then cache it.


//        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId)
//                .orElseThrow(() -> new BadCredentialsException("Invalid or missing API Key"));

        if (apiKeyEntry==null || !apiKeyEntry.enabled() || !secretMatches(rawSecret, apiKeyEntry)) {
            throw new BadCredentialsException("Invalid or missing API Key");
        }

        RateLimitResult rateLimitResult = rateLimiter.check("apikey:" + keyId,requestPerMinute,60);

        if(!rateLimitResult.isAllowed()) {
            log.warn("Too many request keyId = {}", keyId);

            throw new RateLimitException("Too many Requests", rateLimitResult.retryAfterSeconds());
        }

        response.setHeader("X-RateLimit-Limit",String.valueOf(requestPerMinute));
        response.setHeader("X-RateLimit-Remaining",String.valueOf(rateLimitResult.remaining()));

        var auth = new UsernamePasswordAuthenticationToken(keyId, null,
                List.of(new SimpleGrantedAuthority("API_KEY_ROLE")));

        SecurityContextHolder.getContext().setAuthentication(auth);

        merchantContext.setMerchantId(apiKeyEntry.merchantId());

        merchantContext.setKeyId(apiKeyEntry.keyId());

        filterChain.doFilter(request, response);

    } catch(Exception e) {
            log.error(e.getMessage(), e);
            handlerExceptionResolver.resolveException(request,response,null,e);
        }



    }

    private ApiKeyCacheEntry loadAndCache(String keyId) {

        ApiKey apiKey = apiKeyRepository.findByKeyId(keyId).orElse(null);

        if(apiKey == null) return null;

        ApiKeyCacheEntry apiKeyCacheEntry = new ApiKeyCacheEntry(
                apiKey.getKeyId(),
                apiKey.getKeySecretHash(),
                apiKey.getPreviousKeySecretHash(),
                apiKey.getGracePeriodExpiresAt(),
                apiKey.getMerchant().getId(),
                apiKey.getEnvironment(),
                apiKey.isEnabled()
        );

        apiKeyCache.put(keyId, apiKeyCacheEntry);

        return apiKeyCacheEntry;
    }

    private boolean secretMatches(String rawSecret, ApiKeyCacheEntry apiKeyCacheEntry) {
        if(BCRYPT.matches(rawSecret, apiKeyCacheEntry.keySecretHash())){
            return true;
        }

        boolean isInGracePeriod = apiKeyCacheEntry.isInGracePeriod();

        return isInGracePeriod
                && apiKeyCacheEntry.previousKeySecretHash() != null
                && BCRYPT.matches(rawSecret, apiKeyCacheEntry.previousKeySecretHash());

    }

    private String[] decode(String header) {
        String encoded = header.substring(BASIC_PREFIX.length()).trim();

        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);

        int colon = decoded.indexOf(':');

        if(colon < 1) return null;

        return new String[]{decoded.substring(0, colon), decoded.substring(colon+1)};

    }
}
