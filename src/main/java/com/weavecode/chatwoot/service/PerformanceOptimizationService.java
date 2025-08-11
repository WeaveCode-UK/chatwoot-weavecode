package com.weavecode.chatwoot.service;

import com.weavecode.chatwoot.dto.PerformanceMetrics;
import com.weavecode.chatwoot.dto.CacheMetrics;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PerformanceOptimizationService {
    
    /**
     * Get system performance metrics
     */
    PerformanceMetrics getSystemPerformanceMetrics(UUID tenantId);
    
    /**
     * Get cache performance metrics
     */
    CacheMetrics getCachePerformanceMetrics(UUID tenantId);
    
    /**
     * Optimize database queries
     */
    void optimizeDatabaseQueries(UUID tenantId);
    
    /**
     * Warm up cache for frequently accessed data
     */
    void warmUpCache(UUID tenantId);
    
    /**
     * Clear expired cache entries
     */
    void clearExpiredCache(UUID tenantId);
    
    /**
     * Optimize Redis memory usage
     */
    void optimizeRedisMemory(UUID tenantId);
    
    /**
     * Get slow query analysis
     */
    List<Map<String, Object>> getSlowQueryAnalysis(UUID tenantId);
    
    /**
     * Optimize JPA entity relationships
     */
    void optimizeEntityRelationships(UUID tenantId);
    
    /**
     * Get connection pool metrics
     */
    Map<String, Object> getConnectionPoolMetrics(UUID tenantId);
    
    /**
     * Optimize connection pool settings
     */
    void optimizeConnectionPool(UUID tenantId);
    
    /**
     * Get garbage collection metrics
     */
    Map<String, Object> getGarbageCollectionMetrics(UUID tenantId);
    
    /**
     * Trigger garbage collection optimization
     */
    void optimizeGarbageCollection(UUID tenantId);
    
    /**
     * Get thread pool metrics
     */
    Map<String, Object> getThreadPoolMetrics(UUID tenantId);
    
    /**
     * Optimize thread pool settings
     */
    void optimizeThreadPool(UUID tenantId);
    
    /**
     * Generate performance optimization report
     */
    Map<String, Object> generateOptimizationReport(UUID tenantId);
    
    /**
     * Apply performance optimizations automatically
     */
    void applyAutomaticOptimizations(UUID tenantId);
}
