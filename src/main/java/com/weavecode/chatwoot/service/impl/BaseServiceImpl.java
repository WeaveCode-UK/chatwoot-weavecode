package com.weavecode.chatwoot.service.impl;

import com.weavecode.chatwoot.exception.EntityNotFoundException;
import com.weavecode.chatwoot.exception.ValidationException;
import com.weavecode.chatwoot.repository.BaseRepository;
import com.weavecode.chatwoot.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Abstract base service implementation providing common business logic
 * Implements enterprise-grade service patterns with comprehensive error handling
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Transactional
public abstract class BaseServiceImpl<T, ID extends UUID> implements BaseService<T, ID> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final BaseRepository<T, ID> repository;

    protected BaseServiceImpl(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public T create(T entity) {
        logger.debug("Creating entity: {}", entity);
        
        try {
            validateEntity(entity);
            validateBusinessRules(entity);
            
            T savedEntity = repository.save(entity);
            handleEntityLifecycle(savedEntity, "CREATE");
            
            logger.info("Entity created successfully with ID: {}", getEntityId(savedEntity));
            return savedEntity;
        } catch (Exception e) {
            logger.error("Error creating entity: {}", e.getMessage(), e);
            throw new ValidationException("Failed to create entity: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<T> findById(ID id) {
        logger.debug("Finding entity by ID: {}", id);
        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public T findByIdOrThrow(ID id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAll(Pageable pageable) {
        logger.debug("Finding all entities with pagination: {}", pageable);
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<T> findAll(Specification<T> spec, Pageable pageable) {
        logger.debug("Finding entities with specification and pagination: {}", pageable);
        return repository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll(Specification<T> spec) {
        logger.debug("Finding entities with specification");
        return repository.findAll(spec);
    }

    @Override
    @Transactional
    public T update(ID id, T entity) {
        logger.debug("Updating entity with ID: {}", id);
        
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Entity not found with ID: " + id);
        }
        
        try {
            validateEntity(entity);
            validateBusinessRules(entity);
            
            T updatedEntity = repository.save(entity);
            handleEntityLifecycle(updatedEntity, "UPDATE");
            
            logger.info("Entity updated successfully with ID: {}", id);
            return updatedEntity;
        } catch (Exception e) {
            logger.error("Error updating entity with ID {}: {}", id, e.getMessage(), e);
            throw new ValidationException("Failed to update entity: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public T partialUpdate(ID id, T entity) {
        logger.debug("Partially updating entity with ID: {}", id);
        
        T existingEntity = findByIdOrThrow(id);
        T mergedEntity = mergeEntities(existingEntity, entity);
        
        return update(id, mergedEntity);
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        logger.debug("Deleting entity with ID: {}", id);
        
        T entity = findByIdOrThrow(id);
        
        try {
            validateBusinessRules(entity);
            repository.deleteById(id);
            handleEntityLifecycle(entity, "DELETE");
            
            logger.info("Entity deleted successfully with ID: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting entity with ID {}: {}", id, e.getMessage(), e);
            throw new ValidationException("Failed to delete entity: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return repository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long count(Specification<T> spec) {
        return repository.count(spec);
    }

    @Override
    public void validateEntity(T entity) {
        // Default validation - can be overridden by subclasses
        if (entity == null) {
            throw new ValidationException("Entity cannot be null");
        }
    }

    @Override
    public void validateBusinessRules(T entity) {
        // Default business rule validation - can be overridden by subclasses
        // Implement specific business logic validation here
    }

    @Override
    public void handleEntityLifecycle(T entity, String operation) {
        // Default lifecycle handling - can be overridden by subclasses
        logger.debug("Handling entity lifecycle: {} for operation: {}", 
                    getEntityId(entity), operation);
    }

    /**
     * Get entity ID for logging purposes
     */
    protected abstract ID getEntityId(T entity);

    /**
     * Merge entities for partial updates
     */
    protected abstract T mergeEntities(T existing, T updated);
}
