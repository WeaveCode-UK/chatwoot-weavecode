package com.weavecode.chatwoot.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

@Component
@Order(3)
public class InputSanitizationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(InputSanitizationFilter.class);

    // Patterns for detecting potentially malicious input
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|UNION|SCRIPT|<SCRIPT|JAVASCRIPT|ONLOAD|ONERROR|ONCLICK)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)(<SCRIPT|JAVASCRIPT|ONLOAD|ONERROR|ONCLICK|ONMOUSEOVER|ONFOCUS|ONBLUR|ONCHANGE|ONSUBMIT|ONRESET|ONSELECT|ONUNLOAD)",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
        "(?i)(\\.\\./|\\.\\.\\\\)",
        Pattern.CASE_INSENSITIVE
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // Create a sanitized request wrapper
            SanitizedRequestWrapper sanitizedRequest = new SanitizedRequestWrapper(request);
            
            // Continue with the filter chain using sanitized request
            filterChain.doFilter(sanitizedRequest, response);
            
        } catch (Exception e) {
            logger.error("Error in input sanitization filter: {}", e.getMessage(), e);
            // Continue with the filter chain even if sanitization fails
            filterChain.doFilter(request, response);
        }
    }

    private static class SanitizedRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {
        
        public SanitizedRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return sanitizeInput(value);
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return null;
            }
            
            String[] sanitizedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                sanitizedValues[i] = sanitizeInput(values[i]);
            }
            return sanitizedValues;
        }

        @Override
        public String getHeader(String name) {
            String value = super.getHeader(name);
            return sanitizeInput(value);
        }

        @Override
        public String getQueryString() {
            String queryString = super.getQueryString();
            return sanitizeInput(queryString);
        }

        @Override
        public String getRequestURI() {
            String uri = super.getRequestURI();
            return sanitizePath(uri);
        }

        @Override
        public String getRequestURL() {
            StringBuffer url = super.getRequestURL();
            return sanitizePath(url.toString());
        }
    }

    private static String sanitizeInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        try {
            // Check for SQL injection patterns
            if (SQL_INJECTION_PATTERN.matcher(input).find()) {
                logger.warn("Potential SQL injection attempt detected: {}", input);
                return "";
            }

            // Check for XSS patterns
            if (XSS_PATTERN.matcher(input).find()) {
                logger.warn("Potential XSS attempt detected: {}", input);
                return "";
            }

            // HTML escape the input
            String sanitized = HtmlUtils.htmlEscape(input.trim());
            
            // Remove any remaining potentially dangerous characters
            sanitized = sanitized.replaceAll("[<>\"']", "");
            
            return sanitized;
            
        } catch (Exception e) {
            logger.error("Error sanitizing input: {}", e.getMessage(), e);
            return "";
        }
    }

    private static String sanitizePath(String path) {
        if (path == null || path.trim().isEmpty()) {
            return path;
        }

        try {
            // Check for path traversal attempts
            if (PATH_TRAVERSAL_PATTERN.matcher(path).find()) {
                logger.warn("Potential path traversal attempt detected: {}", path);
                return "/";
            }

            // Remove any null bytes
            path = path.replaceAll("\0", "");
            
            // Normalize the path
            path = path.replaceAll("//+", "/");
            
            return path;
            
        } catch (Exception e) {
            logger.error("Error sanitizing path: {}", e.getMessage(), e);
            return "/";
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip sanitization for health checks and static resources
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
