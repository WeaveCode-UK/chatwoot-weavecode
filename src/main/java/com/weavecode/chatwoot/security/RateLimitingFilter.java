package com.weavecode.chatwoot.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@Order(2)
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Rate limiting configuration
    private static final int MAX_REQUESTS_PER_MINUTE = 100;
    private static final int MAX_REQUESTS_PER_HOUR = 1000;
    private static final int MAX_AUTH_ATTEMPTS_PER_MINUTE = 5;
    private static final int MAX_AUTH_ATTEMPTS_PER_HOUR = 20;
    private static final int MAX_API_REQUESTS_PER_MINUTE = 200;
    private static final int MAX_API_REQUESTS_PER_HOUR = 2000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String clientIp = getClientIpAddress(request);
            String endpoint = request.getRequestURI();
            
            // Check rate limits
            if (isRateLimited(clientIp, endpoint)) {
                logger.warn("Rate limit exceeded for IP: {} on endpoint: {}", clientIp, endpoint);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setHeader("Retry-After", "60");
                response.getWriter().write("Rate limit exceeded. Please try again later.");
                return;
            }
            
            // Continue with the filter chain
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            logger.error("Error in rate limiting filter: {}", e.getMessage(), e);
            // Continue with the filter chain even if rate limiting fails
            filterChain.doFilter(request, response);
        }
    }

    private boolean isRateLimited(String clientIp, String endpoint) {
        try {
            // Different rate limits for different endpoint types
            if (isAuthEndpoint(endpoint)) {
                return checkAuthRateLimit(clientIp);
            } else if (isApiEndpoint(endpoint)) {
                return checkApiRateLimit(clientIp);
            } else {
                return checkGeneralRateLimit(clientIp);
            }
        } catch (Exception e) {
            logger.error("Error checking rate limit: {}", e.getMessage(), e);
            // Allow request if rate limiting fails
            return false;
        }
    }

    private boolean checkAuthRateLimit(String clientIp) {
        String minuteKey = "rate_limit:auth:minute:" + clientIp;
        String hourKey = "rate_limit:auth:hour:" + clientIp;
        
        // Check minute limit
        Long minuteCount = getCurrentCount(minuteKey);
        if (minuteCount != null && minuteCount >= MAX_AUTH_ATTEMPTS_PER_MINUTE) {
            return true;
        }
        
        // Check hour limit
        Long hourCount = getCurrentCount(hourKey);
        if (hourCount != null && hourCount >= MAX_AUTH_ATTEMPTS_PER_HOUR) {
            return true;
        }
        
        // Increment counters
        incrementCounter(minuteKey, Duration.ofMinutes(1));
        incrementCounter(hourKey, Duration.ofHours(1));
        
        return false;
    }

    private boolean checkApiRateLimit(String clientIp) {
        String minuteKey = "rate_limit:api:minute:" + clientIp;
        String hourKey = "rate_limit:api:hour:" + clientIp;
        
        // Check minute limit
        Long minuteCount = getCurrentCount(minuteKey);
        if (minuteCount != null && minuteCount >= MAX_API_REQUESTS_PER_MINUTE) {
            return true;
        }
        
        // Check hour limit
        Long hourCount = getCurrentCount(hourKey);
        if (hourCount != null && hourCount >= MAX_API_REQUESTS_PER_HOUR) {
            return true;
        }
        
        // Increment counters
        incrementCounter(minuteKey, Duration.ofMinutes(1));
        incrementCounter(hourKey, Duration.ofHours(1));
        
        return false;
    }

    private boolean checkGeneralRateLimit(String clientIp) {
        String minuteKey = "rate_limit:general:minute:" + clientIp;
        String hourKey = "rate_limit:general:hour:" + clientIp;
        
        // Check minute limit
        Long minuteCount = getCurrentCount(minuteKey);
        if (minuteCount != null && minuteCount >= MAX_REQUESTS_PER_MINUTE) {
            return true;
        }
        
        // Check hour limit
        Long hourCount = getCurrentCount(hourKey);
        if (hourCount != null && hourCount >= MAX_REQUESTS_PER_HOUR) {
            return true;
        }
        
        // Increment counters
        incrementCounter(minuteKey, Duration.ofMinutes(1));
        incrementCounter(hourKey, Duration.ofHours(1));
        
        return false;
    }

    private Long getCurrentCount(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value != null ? Long.valueOf(value.toString()) : 0L;
        } catch (Exception e) {
            logger.warn("Error getting rate limit count for key {}: {}", key, e.getMessage());
            return 0L;
        }
    }

    private void incrementCounter(String key, Duration expiration) {
        try {
            redisTemplate.opsForValue().increment(key);
            redisTemplate.expire(key, expiration);
        } catch (Exception e) {
            logger.warn("Error incrementing rate limit counter for key {}: {}", key, e.getMessage());
        }
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

    private boolean isAuthEndpoint(String endpoint) {
        return endpoint.contains("/api/auth/") || 
               endpoint.contains("/api/login") || 
               endpoint.contains("/api/register");
    }

    private boolean isApiEndpoint(String endpoint) {
        return endpoint.startsWith("/api/") && !isAuthEndpoint(endpoint);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip rate limiting for health checks and static resources
        String path = request.getRequestURI();
        return path.equals("/actuator/health") ||
               path.equals("/actuator/info") ||
               path.startsWith("/static/") ||
               path.startsWith("/public/") ||
               path.equals("/favicon.ico");
    }
}
