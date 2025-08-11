package com.weavecode.chatwoot.exception;

/**
 * Exception thrown when an entity is not found
 * Implements enterprise-grade error handling patterns
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
