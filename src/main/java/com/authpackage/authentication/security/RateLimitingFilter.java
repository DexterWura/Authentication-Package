package com.authpackage.authentication.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RateLimitingFilter extends OncePerRequestFilter {
    
    @Value("${auth.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;
    
    @Value("${auth.rate-limit.requests-per-minute:60}")
    private int requestsPerMinute;
    
    @Value("${auth.rate-limit.login-requests-per-minute:5}")
    private int loginRequestsPerMinute;
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        
        if (!rateLimitEnabled) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String clientIp = getClientIpAddress(request);
        String path = request.getRequestURI();
        
        // Different rate limits for different endpoints
        int limit = path.contains("/login") || path.contains("/register") 
            ? loginRequestsPerMinute 
            : requestsPerMinute;
        
        Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createBucket(limit));
        
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Too many requests. Please try again later.\"}");
        }
    }
    
    private Bucket createBucket(int requestsPerMinute) {
        Refill refill = Refill.intervally(requestsPerMinute, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(requestsPerMinute, refill);
        return Bucket.builder().addLimit(limit).build();
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}

