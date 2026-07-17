package com.project.razorpay.merchant.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    private final JwtUtil jwtUtil;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final MerchantContext merchantContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("Incoming request: {}", request.getRequestURI());

        try {
            final String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwtToken = authorizationHeader.split(BEARER_PREFIX)[1];

            // This will also work
            //  String jwtToken = authorizationHeader.substring("Bearer ".length());

            Claims claims = jwtUtil.verifyAccessToken(jwtToken);

            if (claims != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                var auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + jwtUtil.extractRole(claims))));

                SecurityContextHolder.getContext().setAuthentication(auth);

                merchantContext.setMerchantId(jwtUtil.extractMerchantId(claims));
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            handlerExceptionResolver.resolveException(request,response,null,e);
        }


    }
}
