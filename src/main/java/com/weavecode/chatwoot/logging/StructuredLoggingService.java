package com.weavecode.chatwoot.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class StructuredLoggingService {

    private static final Logger logger = LoggerFactory.getLogger(StructuredLoggingService.class);
    private static final String CORRELATION_ID_KEY = "correlationId";
    private static final String TENANT_ID_KEY = "tenantId";
    private static final String USER_ID_KEY = "userId";
    private static final String REQUEST_ID_KEY = "requestId";

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Start a new request context with correlation ID
     */
    public void startRequestContext(String tenantId, String userId) {
        String correlationId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        
        MDC.put(CORRELATION_ID_KEY, correlationId);
        MDC.put(TENANT_ID_KEY, tenantId);
        MDC.put(USER_ID_KEY, userId);
        MDC.put(REQUEST_ID_KEY, requestId);
        
        logger.info("Request context started", Map.of(
            "correlationId", correlationId,
            "requestId", requestId,
            "tenantId", tenantId,
            "userId", userId
        ));
    }

    /**
     * Clear the current request context
     */
    public void clearRequestContext() {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        String requestId = MDC.get(REQUEST_ID_KEY);
        
        if (correlationId != null) {
            logger.info("Request context cleared", Map.of(
                "correlationId", correlationId,
                "requestId", requestId
            ));
        }
        
        MDC.clear();
    }

    /**
     * Log business event with structured data
     */
    public void logBusinessEvent(String event, String category, Map<String, Object> data) {
        Map<String, Object> logData = new HashMap<>(data);
        logData.put("event", event);
        logData.put("category", category);
        logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        logger.info("Business event: {}", logData);
    }

    /**
     * Log security event with structured data
     */
    public void logSecurityEvent(String event, String severity, String source, Map<String, Object> data) {
        Map<String, Object> logData = new HashMap<>(data);
        logData.put("event", event);
        logData.put("severity", severity);
        logData.put("source", source);
        logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        switch (severity.toUpperCase()) {
            case "HIGH":
                logger.error("Security event: {}", logData);
                break;
            case "MEDIUM":
                logger.warn("Security event: {}", logData);
                break;
            default:
                logger.info("Security event: {}", logData);
        }
    }

    /**
     * Log performance metric with structured data
     */
    public void logPerformanceMetric(String metric, String operation, long duration, Map<String, Object> data) {
        Map<String, Object> logData = new HashMap<>(data);
        logData.put("metric", metric);
        logData.put("operation", operation);
        logData.put("duration", duration);
        logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        if (duration > 1000) { // Log slow operations as warnings
            logger.warn("Performance metric: {}", logData);
        } else {
            logger.info("Performance metric: {}", logData);
        }
    }

    /**
     * Log error with structured data and context
     */
    public void logError(String error, String context, Exception exception, Map<String, Object> data) {
        Map<String, Object> logData = new HashMap<>(data);
        logData.put("error", error);
        logData.put("context", context);
        logData.put("exception", exception.getClass().getSimpleName());
        logData.put("message", exception.getMessage());
        logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        logger.error("Error occurred: {}", logData, exception);
    }

    /**
     * Log API request with structured data
     */
    public void logApiRequest(String method, String endpoint, String clientIp, Map<String, Object> data) {
        Map<String, Object> logData = new HashMap<>(data);
        logData.put("method", method);
        logData.put("endpoint", endpoint);
        logData.put("clientIp", clientIp);
        logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        logger.info("API request: {}", logData);
    }

    /**
     * Log API response with structured data
     */
    public void logApiResponse(String method, String endpoint, int statusCode, long responseTime, Map<String, Object> data) {
        Map<String, Object> logData = new HashMap<>(data);
        logData.put("method", method);
        logData.put("endpoint", endpoint);
        logData.put("statusCode", statusCode);
        logData.put("responseTime", responseTime);
        logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        if (statusCode >= 400) {
            logger.warn("API response: {}", logData);
        } else {
            logger.info("API response: {}", logData);
        }
    }

    /**
     * Log database operation with structured data
     */
    public void logDatabaseOperation(String operation, String table, long duration, Map<String, Object> data) {
        Map<String, Object> logData = new HashMap<>(data);
        logData.put("operation", operation);
        logData.put("table", table);
        logData.put("duration", duration);
        logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        if (duration > 500) { // Log slow queries as warnings
            logger.warn("Database operation: {}", logData);
        } else {
            logger.debug("Database operation: {}", logData);
        }
    }

    /**
     * Log cache operation with structured data
     */
    public void logCacheOperation(String operation, String key, boolean hit, long duration, Map<String, Object> data) {
        Map<String, Object> logData = new HashMap<>(data);
        logData.put("operation", operation);
        logData.put("key", key);
        logData.put("hit", hit);
        logData.put("duration", duration);
        logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        logger.debug("Cache operation: {}", logData);
    }

    /**
     * Get current correlation ID
     */
    public String getCurrentCorrelationId() {
        return MDC.get(CORRELATION_ID_KEY);
    }

    /**
     * Get current tenant ID
     */
    public String getCurrentTenantId() {
        return MDC.get(TENANT_ID_KEY);
    }

    /**
     * Get current user ID
     */
    public String getCurrentUserId() {
        return MDC.get(USER_ID_KEY);
    }

    /**
     * Get current request ID
     */
    public String getCurrentRequestId() {
        return MDC.get(REQUEST_ID_KEY);
    }

    /**
     * Set additional context in MDC
     */
    public void setContext(String key, String value) {
        MDC.put(key, value);
    }

    /**
     * Remove context from MDC
     */
    public void removeContext(String key) {
        MDC.remove(key);
    }
}
