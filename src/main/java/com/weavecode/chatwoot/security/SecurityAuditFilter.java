package com.weavecode.chatwoot.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(4)
public class SecurityAuditFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityAuditFilter.class);
    private static final Logger securityLogger = LoggerFactory.getLogger("SECURITY_AUDIT");

    @Autowired
    private ChatwootMetricsCollector metricsCollector;

    // Track suspicious IPs and their activities
    private final Map<String, SuspiciousActivity> suspiciousIps = new ConcurrentHashMap<>();
    
    // Thresholds for suspicious activity
    private static final int MAX_FAILED_AUTH_ATTEMPTS = 10;
    private static final int MAX_4XX_RESPONSES = 20;
    private static final int MAX_5XX_RESPONSES = 5;
    private static final int SUSPICIOUS_ACTIVITY_WINDOW_MINUTES = 15;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        
        try {
            // Log request start
            logRequestStart(clientIp, userAgent, endpoint, method);
            
            // Continue with the filter chain
            filterChain.doFilter(request, response);
            
            // Log response
            int statusCode = response.getStatus();
            long responseTime = System.currentTimeMillis() - startTime;
            
            logResponse(clientIp, endpoint, method, statusCode, responseTime);
            
            // Check for suspicious activity
            checkSuspiciousActivity(clientIp, endpoint, statusCode, responseTime);
            
        } catch (Exception e) {
            // Log security exception
            logSecurityException(clientIp, endpoint, method, e);
            throw e;
        }
    }

    private void logRequestStart(String clientIp, String userAgent, String endpoint, String method) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logData.put("event", "REQUEST_START");
        logData.put("clientIp", clientIp);
        logData.put("userAgent", userAgent);
        logData.put("endpoint", endpoint);
        logData.put("method", method);
        
        securityLogger.info("Security audit: {}", logData);
    }

    private void logResponse(String clientIp, String endpoint, String method, int statusCode, long responseTime) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logData.put("event", "RESPONSE");
        logData.put("clientIp", clientIp);
        logData.put("endpoint", endpoint);
        logData.put("method", method);
        logData.put("statusCode", statusCode);
        logData.put("responseTime", responseTime);
        
        // Log suspicious responses
        if (statusCode >= 400) {
            logData.put("severity", statusCode >= 500 ? "HIGH" : "MEDIUM");
            securityLogger.warn("Suspicious response: {}", logData);
        } else {
            securityLogger.info("Security audit: {}", logData);
        }
    }

    private void logSecurityException(String clientIp, String endpoint, String method, Exception e) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logData.put("event", "SECURITY_EXCEPTION");
        logData.put("clientIp", clientIp);
        logData.put("endpoint", endpoint);
        logData.put("method", method);
        logData.put("exception", e.getClass().getSimpleName());
        logData.put("message", e.getMessage());
        logData.put("severity", "HIGH");
        
        securityLogger.error("Security exception: {}", logData);
        
        // Increment security metrics
        metricsCollector.incrementApiErrors();
    }

    private void checkSuspiciousActivity(String clientIp, String endpoint, int statusCode, long responseTime) {
        SuspiciousActivity activity = suspiciousIps.computeIfAbsent(clientIp, k -> new SuspiciousActivity());
        
        // Update activity counters
        if (statusCode >= 400 && statusCode < 500) {
            activity.increment4xxResponses();
        } else if (statusCode >= 500) {
            activity.increment5xxResponses();
        }
        
        // Check for failed authentication attempts
        if (isAuthEndpoint(endpoint) && statusCode >= 400) {
            activity.incrementFailedAuthAttempts();
        }
        
        // Check for suspicious patterns
        if (isSuspiciousPattern(endpoint, userAgent)) {
            activity.incrementSuspiciousPatterns();
        }
        
        // Log suspicious activity if thresholds are exceeded
        if (activity.isSuspicious()) {
            logSuspiciousActivity(clientIp, activity);
            
            // Increment security metrics
            metricsCollector.incrementApiErrors();
        }
        
        // Clean up old activity records
        cleanupOldActivity();
    }

    private boolean isAuthEndpoint(String endpoint) {
        return endpoint.contains("/api/auth/") || 
               endpoint.contains("/api/login") || 
               endpoint.contains("/api/register");
    }

    private boolean isSuspiciousPattern(String endpoint, String userAgent) {
        // Check for common attack patterns
        return (endpoint.contains("admin") || endpoint.contains("config")) ||
               (userAgent != null && (
                   userAgent.toLowerCase().contains("bot") ||
                   userAgent.toLowerCase().contains("crawler") ||
                   userAgent.toLowerCase().contains("scanner")
               ));
    }

    private void logSuspiciousActivity(String clientIp, SuspiciousActivity activity) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logData.put("event", "SUSPICIOUS_ACTIVITY");
        logData.put("clientIp", clientIp);
        logData.put("failedAuthAttempts", activity.getFailedAuthAttempts());
        logData.put("4xxResponses", activity.get4xxResponses());
        logData.put("5xxResponses", activity.get5xxResponses());
        logData.put("suspiciousPatterns", activity.getSuspiciousPatterns());
        logData.put("severity", "HIGH");
        
        securityLogger.warn("Suspicious activity detected: {}", logData);
    }

    private void cleanupOldActivity() {
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(SUSPICIOUS_ACTIVITY_WINDOW_MINUTES);
        
        suspiciousIps.entrySet().removeIf(entry -> 
            entry.getValue().getLastActivity().isBefore(cutoff)
        );
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

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip audit for health checks and static resources
        String path = request.getRequestURI();
        return path.equals("/actuator/health") ||
               path.equals("/actuator/info") ||
               path.startsWith("/static/") ||
               path.startsWith("/public/") ||
               path.equals("/favicon.ico");
    }

    private static class SuspiciousActivity {
        private int failedAuthAttempts = 0;
        private int responses4xx = 0;
        private int responses5xx = 0;
        private int suspiciousPatterns = 0;
        private LocalDateTime lastActivity = LocalDateTime.now();

        public void incrementFailedAuthAttempts() {
            failedAuthAttempts++;
            lastActivity = LocalDateTime.now();
        }

        public void increment4xxResponses() {
            responses4xx++;
            lastActivity = LocalDateTime.now();
        }

        public void increment5xxResponses() {
            responses5xx++;
            lastActivity = LocalDateTime.now();
        }

        public void incrementSuspiciousPatterns() {
            suspiciousPatterns++;
            lastActivity = LocalDateTime.now();
        }

        public boolean isSuspicious() {
            return failedAuthAttempts >= MAX_FAILED_AUTH_ATTEMPTS ||
                   responses4xx >= MAX_4XX_RESPONSES ||
                   responses5xx >= MAX_5XX_RESPONSES ||
                   suspiciousPatterns >= 5;
        }

        // Getters
        public int getFailedAuthAttempts() { return failedAuthAttempts; }
        public int get4xxResponses() { return responses4xx; }
        public int get5xxResponses() { return responses5xx; }
        public int getSuspiciousPatterns() { return suspiciousPatterns; }
        public LocalDateTime getLastActivity() { return lastActivity; }
    }
}
