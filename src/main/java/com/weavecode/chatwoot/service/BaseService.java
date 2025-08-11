package com.weavecode.chatwoot.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base service interface providing common business logic operations
 * Implements enterprise-grade service patterns with comprehensive error handling
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
public interface BaseService<T, ID extends UUID> {

    /**
     * Create a new entity with validation and business rules
     */
    T create(T entity);

    /**
     * Find entity by ID with proper error handling
     */
    Optional<T> findById(ID id);

    /**
     * Find entity by ID or throw exception if not found
     */
    T findByIdOrThrow(ID id);

    /**
     * Find all entities with pagination
     */
    Page<T> findAll(Pageable pageable);

    /**
     * Find all entities matching specification with pagination
     */
    Page<T> findAll(Specification<T> spec, Pageable pageable);

    /**
     * Find all entities matching specification
     */
    List<T> findAll(Specification<T> spec);

    /**
     * Update existing entity with validation
     */
    T update(ID id, T entity);

    /**
     * Partial update of entity fields
     */
    T partialUpdate(ID id, T entity);

    /**
     * Delete entity by ID with business rule validation
     */
    void deleteById(ID id);

    /**
     * Check if entity exists by ID
     */
    boolean existsById(ID id);

    /**
     * Count total entities
     */
    long count();

    /**
     * Count entities matching specification
     */
    long count(Specification<T> spec);

    /**
     * Validate entity before operations
     */
    void validateEntity(T entity);

    /**
     * Perform business rule validation
     */
    void validateBusinessRules(T entity);

    /**
     * Handle entity lifecycle events
     */
    void handleEntityLifecycle(T entity, String operation);
}
