package com.weavecode.chatwoot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

/**
 * Base repository interface providing common functionality for all entities
 * Implements enterprise-grade data access patterns with specification support
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends UUID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    
    /**
     * Check if entity exists by ID
     */
    boolean existsById(ID id);
    
    /**
     * Count total entities
     */
    long count();
    
    /**
     * Delete entity by ID
     */
    void deleteById(ID id);
    
    /**
     * Delete multiple entities by IDs
     */
    void deleteAllById(Iterable<? extends ID> ids);
}
