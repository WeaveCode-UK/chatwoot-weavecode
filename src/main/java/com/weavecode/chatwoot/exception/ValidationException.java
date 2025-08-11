package com.weavecode.chatwoot.exception;

/**
 * Exception thrown when entity validation fails
 * Implements enterprise-grade error handling patterns
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
