package com.weavecode.chatwoot.util;

import java.util.regex.Pattern;

/**
 * Utility class for common validation operations
 * 
 * Provides reusable validation methods for:
 * - Email validation
 * - Password strength validation
 * - Domain validation
 * - UUID validation
 * - General input sanitization
 * 
 * @author WeaveCode Team
 * @version 1.0.0
 */
public class ValidationUtils {

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // Domain validation pattern
    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$"
    );
    
    // UUID validation pattern
    private static final Pattern UUID_PATTERN = Pattern.compile(
        "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );
    
    // Password strength pattern (at least 8 chars, 1 uppercase, 1 lowercase, 1 digit)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$"
    );

    /**
     * Validate email format
     * 
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate domain format
     * 
     * @param domain Domain to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidDomain(String domain) {
        if (domain == null || domain.trim().isEmpty()) {
            return false;
        }
        return DOMAIN_PATTERN.matcher(domain.trim()).matches();
    }

    /**
     * Validate UUID format
     * 
     * @param uuid UUID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUuid(String uuid) {
        if (uuid == null || uuid.trim().isEmpty()) {
            return false;
        }
        return UUID_PATTERN.matcher(uuid.trim()).matches();
    }

    /**
     * Validate password strength
     * 
     * @param password Password to validate
     * @return true if strong enough, false otherwise
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Sanitize input string
     * 
     * @param input Input to sanitize
     * @return Sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim()
            .replaceAll("<script[^>]*>.*?</script>", "") // Remove script tags
            .replaceAll("<[^>]*>", "") // Remove HTML tags
            .replaceAll("javascript:", "") // Remove javascript: protocol
            .replaceAll("on\\w+\\s*=", "") // Remove event handlers
            .replaceAll("\\s+", " "); // Normalize whitespace
    }

    /**
     * Check if string is not null or empty
     * 
     * @param str String to check
     * @return true if not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    /**
     * Check if string is null or empty
     * 
     * @param str String to check
     * @return true if null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validate tenant ID format
     * 
     * @param tenantId Tenant ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidTenantId(String tenantId) {
        return isNotEmpty(tenantId) && tenantId.length() <= 50;
    }

    /**
     * Validate user ID format
     * 
     * @param userId User ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUserId(String userId) {
        return isValidUuid(userId);
    }

    /**
     * Validate conversation ID format
     * 
     * @param conversationId Conversation ID to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidConversationId(String conversationId) {
        return isValidUuid(conversationId);
    }

    /**
     * Validate message content
     * 
     * @param content Message content to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidMessageContent(String content) {
        return isNotEmpty(content) && content.length() <= 10000; // Max 10KB message
    }

    /**
     * Validate plan type
     * 
     * @param plan Plan to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPlan(String plan) {
        if (isEmpty(plan)) {
            return false;
        }
        
        String upperPlan = plan.toUpperCase();
        return upperPlan.equals("FREE") || 
               upperPlan.equals("STARTER") || 
               upperPlan.equals("PROFESSIONAL") || 
               upperPlan.equals("ENTERPRISE");
    }

    /**
     * Validate role type
     * 
     * @param role Role to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidRole(String role) {
        if (isEmpty(role)) {
            return false;
        }
        
        String upperRole = role.toUpperCase();
        return upperRole.equals("USER") || 
               upperRole.equals("AGENT") || 
               upperRole.equals("ADMIN") || 
               upperRole.equals("SUPER_ADMIN");
    }
}
