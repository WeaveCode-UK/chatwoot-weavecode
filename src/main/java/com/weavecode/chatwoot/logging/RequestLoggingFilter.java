package com.weavecode.chatwoot.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Order(5)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Autowired
    private StructuredLoggingService loggingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        
        // Wrap request and response for content caching
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        
        try {
            // Start request context
            startRequestContext(wrappedRequest, requestId);
            
            // Log request start
            logRequestStart(wrappedRequest, requestId);
            
            // Continue with the filter chain
            filterChain.doFilter(wrappedRequest, wrappedResponse);
            
            // Log response
            long responseTime = System.currentTimeMillis() - startTime;
            logResponse(wrappedRequest, wrappedResponse, responseTime, requestId);
            
            // Copy response content back to original response
            wrappedResponse.copyBodyToResponse();
            
        } catch (Exception e) {
            // Log error
            long responseTime = System.currentTimeMillis() - startTime;
            logError(wrappedRequest, e, responseTime, requestId);
            throw e;
        } finally {
            // Clear request context
            loggingService.clearRequestContext();
        }
    }

    private void startRequestContext(HttpServletRequest request, String requestId) {
        // Extract tenant and user info from request
        String tenantId = extractTenantId(request);
        String userId = extractUserId(request);
        
        // Start logging context
        loggingService.startRequestContext(tenantId, userId);
        loggingService.setContext("requestId", requestId);
    }

    private String extractTenantId(HttpServletRequest request) {
        // Try to get tenant ID from various sources
        String tenantId = request.getHeader("X-Tenant-ID");
        if (tenantId == null) {
            tenantId = request.getParameter("tenantId");
        }
        if (tenantId == null) {
            tenantId = "unknown";
        }
        return tenantId;
    }

    private String extractUserId(HttpServletRequest request) {
        // Try to get user ID from various sources
        String userId = request.getHeader("X-User-ID");
        if (userId == null) {
            userId = request.getParameter("userId");
        }
        if (userId == null) {
            userId = "anonymous";
        }
        return userId;
    }

    private void logRequestStart(HttpServletRequest request, String requestId) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("requestId", requestId);
        requestData.put("method", request.getMethod());
        requestData.put("uri", request.getRequestURI());
        requestData.put("queryString", request.getQueryString());
        requestData.put("clientIp", getClientIpAddress(request));
        requestData.put("userAgent", request.getHeader("User-Agent"));
        requestData.put("contentType", request.getContentType());
        requestData.put("contentLength", request.getContentLength());
        
        // Log headers (excluding sensitive ones)
        Map<String, String> headers = new HashMap<>();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            if (!isSensitiveHeader(headerName)) {
                headers.put(headerName, request.getHeader(headerName));
            }
        });
        requestData.put("headers", headers);
        
        loggingService.logApiRequest(
            request.getMethod(),
            request.getRequestURI(),
            getClientIpAddress(request),
            requestData
        );
    }

    private void logResponse(HttpServletRequest request, HttpServletResponse response, 
                           long responseTime, String requestId) {
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("requestId", requestId);
        responseData.put("statusCode", response.getStatus());
        responseData.put("responseTime", responseTime);
        responseData.put("contentType", response.getContentType());
        responseData.put("contentLength", response.getContentLength());
        
        // Log response headers (excluding sensitive ones)
        Map<String, String> headers = new HashMap<>();
        response.getHeaderNames().forEach(headerName -> {
            if (!isSensitiveHeader(headerName)) {
                headers.put(headerName, response.getHeader(headerName));
            }
        });
        responseData.put("headers", headers);
        
        loggingService.logApiResponse(
            request.getMethod(),
            request.getRequestURI(),
            response.getStatus(),
            responseTime,
            responseData
        );
    }

    private void logError(HttpServletRequest request, Exception exception, 
                         long responseTime, String requestId) {
        Map<String, Object> errorData = new HashMap<>();
        errorData.put("requestId", requestId);
        errorData.put("method", request.getMethod());
        errorData.put("uri", request.getRequestURI());
        errorData.put("responseTime", responseTime);
        errorData.put("clientIp", getClientIpAddress(request));
        
        loggingService.logError(
            "Request processing failed",
            "API_REQUEST",
            exception,
            errorData
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

    private boolean isSensitiveHeader(String headerName) {
        String lowerHeader = headerName.toLowerCase();
        return lowerHeader.contains("authorization") ||
               lowerHeader.contains("cookie") ||
               lowerHeader.contains("x-api-key") ||
               lowerHeader.contains("x-auth-token") ||
               lowerHeader.contains("password") ||
               lowerHeader.contains("secret");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip logging for health checks and static resources
        String path = request.getRequestURI();
        return path.equals("/actuator/health") ||
               path.equals("/actuator/info") ||
               path.startsWith("/static/") ||
               path.startsWith("/public/") ||
               path.equals("/favicon.ico") ||
               path.startsWith("/actuator/metrics") ||
               path.startsWith("/actuator/prometheus");
    }
}
