package com.weavecode.chatwoot.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
public class SecurityHeadersFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityHeadersFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Add security headers
            addSecurityHeaders(response);
            
            // Continue with the filter chain
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            logger.error("Error in security headers filter: {}", e.getMessage(), e);
            // Continue with the filter chain even if headers fail
            filterChain.doFilter(request, response);
        }
    }

    private void addSecurityHeaders(HttpServletResponse response) {
        // Security Headers
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Content Security Policy
        response.setHeader("Content-Security-Policy", 
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
            "style-src 'self' 'unsafe-inline'; " +
            "img-src 'self' data: https:; " +
            "font-src 'self' data:; " +
            "connect-src 'self' ws: wss:; " +
            "frame-ancestors 'none'; " +
            "base-uri 'self'; " +
            "form-action 'self'");
        
        // HSTS (HTTP Strict Transport Security)
        response.setHeader("Strict-Transport-Security", 
            "max-age=31536000; includeSubDomains; preload");
        
        // Permissions Policy (formerly Feature Policy)
        response.setHeader("Permissions-Policy", 
            "geolocation=(), microphone=(), camera=(), payment=(), usb=(), magnetometer=(), " +
            "gyroscope=(), accelerometer=(), ambient-light-sensor=(), autoplay=(), " +
            "encrypted-media=(), midi=(), sync-xhr=(), picture-in-picture=(), " +
            "publickey-credentials-get=(), screen-wake-lock=(), web-share=()");
        
        // Cache Control for sensitive endpoints
        if (isSensitiveEndpoint(request)) {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, private");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }
        
        // Remove server information
        response.setHeader("Server", "Chatwoot");
        
        logger.debug("Security headers added to response");
    }

    private boolean isSensitiveEndpoint(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.contains("/api/auth/") || 
               path.contains("/api/users/") || 
               path.contains("/actuator/") ||
               path.contains("/swagger-ui/") ||
               path.contains("/v3/api-docs/");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip filtering for static resources and health checks
        String path = request.getRequestURI();
        return path.startsWith("/static/") || 
               path.startsWith("/public/") || 
               path.equals("/actuator/health") ||
               path.equals("/favicon.ico");
    }
}
