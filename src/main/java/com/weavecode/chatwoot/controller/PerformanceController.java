package com.weavecode.chatwoot.controller;

import com.weavecode.chatwoot.dto.PerformanceMetrics;
import com.weavecode.chatwoot.dto.CacheMetrics;
import com.weavecode.chatwoot.service.PerformanceOptimizationService;
import com.weavecode.chatwoot.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = "*")
public class PerformanceController {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceController.class);

    @Autowired
    private PerformanceOptimizationService performanceService;

    @Autowired
    private SecurityService securityService;

    /**
     * Get system performance metrics for current tenant
     */
    @GetMapping("/system")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PerformanceMetrics> getSystemPerformanceMetrics() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            PerformanceMetrics metrics = performanceService.getSystemPerformanceMetrics(tenantId);
            
            logger.info("Retrieved system performance metrics for tenant {}", tenantId);
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            logger.error("Error retrieving system performance metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get cache performance metrics for current tenant
     */
    @GetMapping("/cache")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CacheMetrics> getCachePerformanceMetrics() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            CacheMetrics metrics = performanceService.getCachePerformanceMetrics(tenantId);
            
            logger.info("Retrieved cache performance metrics for tenant {}", tenantId);
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            logger.error("Error retrieving cache performance metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Optimize database queries for current tenant
     */
    @PostMapping("/optimize/database")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> optimizeDatabaseQueries() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            performanceService.optimizeDatabaseQueries(tenantId);
            
            logger.info("Database query optimization completed for tenant {}", tenantId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error optimizing database queries: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Warm up cache for current tenant
     */
    @PostMapping("/optimize/cache-warmup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> warmUpCache() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            performanceService.warmUpCache(tenantId);
            
            logger.info("Cache warm-up completed for tenant {}", tenantId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error warming up cache: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Clear expired cache entries for current tenant
     */
    @PostMapping("/optimize/cache-clear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> clearExpiredCache() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            performanceService.clearExpiredCache(tenantId);
            
            logger.info("Expired cache cleanup completed for tenant {}", tenantId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error clearing expired cache: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Optimize Redis memory usage for current tenant
     */
    @PostMapping("/optimize/redis-memory")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> optimizeRedisMemory() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            performanceService.optimizeRedisMemory(tenantId);
            
            logger.info("Redis memory optimization completed for tenant {}", tenantId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error optimizing Redis memory: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get slow query analysis for current tenant
     */
    @GetMapping("/analysis/slow-queries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getSlowQueryAnalysis() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            List<Map<String, Object>> analysis = performanceService.getSlowQueryAnalysis(tenantId);
            
            logger.info("Retrieved slow query analysis for tenant {}", tenantId);
            return ResponseEntity.ok(analysis);
            
        } catch (Exception e) {
            logger.error("Error retrieving slow query analysis: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Optimize JPA entity relationships for current tenant
     */
    @PostMapping("/optimize/entity-relationships")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> optimizeEntityRelationships() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            performanceService.optimizeEntityRelationships(tenantId);
            
            logger.info("Entity relationship optimization completed for tenant {}", tenantId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error optimizing entity relationships: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get connection pool metrics for current tenant
     */
    @GetMapping("/metrics/connection-pool")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getConnectionPoolMetrics() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            Map<String, Object> metrics = performanceService.getConnectionPoolMetrics(tenantId);
            
            logger.info("Retrieved connection pool metrics for tenant {}", tenantId);
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            logger.error("Error retrieving connection pool metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Optimize connection pool settings for current tenant
     */
    @PostMapping("/optimize/connection-pool")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> optimizeConnectionPool() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            performanceService.optimizeConnectionPool(tenantId);
            
            logger.info("Connection pool optimization completed for tenant {}", tenantId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error optimizing connection pool: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get garbage collection metrics for current tenant
     */
    @GetMapping("/metrics/garbage-collection")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getGarbageCollectionMetrics() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            Map<String, Object> metrics = performanceService.getGarbageCollectionMetrics(tenantId);
            
            logger.info("Retrieved garbage collection metrics for tenant {}", tenantId);
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            logger.error("Error retrieving garbage collection metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Trigger garbage collection optimization for current tenant
     */
    @PostMapping("/optimize/garbage-collection")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> optimizeGarbageCollection() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            performanceService.optimizeGarbageCollection(tenantId);
            
            logger.info("Garbage collection optimization completed for tenant {}", tenantId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error optimizing garbage collection: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get thread pool metrics for current tenant
     */
    @GetMapping("/metrics/thread-pool")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getThreadPoolMetrics() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            Map<String, Object> metrics = performanceService.getThreadPoolMetrics(tenantId);
            
            logger.info("Retrieved thread pool metrics for tenant {}", tenantId);
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            logger.error("Error retrieving thread pool metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Optimize thread pool settings for current tenant
     */
    @PostMapping("/optimize/thread-pool")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> optimizeThreadPool() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            performanceService.optimizeThreadPool(tenantId);
            
            logger.info("Thread pool optimization completed for tenant {}", tenantId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error optimizing thread pool: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generate performance optimization report for current tenant
     */
    @GetMapping("/report/optimization")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> generateOptimizationReport() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            Map<String, Object> report = performanceService.generateOptimizationReport(tenantId);
            
            logger.info("Generated performance optimization report for tenant {}", tenantId);
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            logger.error("Error generating performance optimization report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Apply automatic performance optimizations for current tenant
     */
    @PostMapping("/optimize/automatic")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> applyAutomaticOptimizations() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            performanceService.applyAutomaticOptimizations(tenantId);
            
            logger.info("Automatic performance optimizations completed for tenant {}", tenantId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error applying automatic optimizations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get comprehensive performance dashboard data for current tenant
     */
    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getPerformanceDashboard() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            // Get all performance metrics
            PerformanceMetrics systemMetrics = performanceService.getSystemPerformanceMetrics(tenantId);
            CacheMetrics cacheMetrics = performanceService.getCachePerformanceMetrics(tenantId);
            Map<String, Object> connectionPoolMetrics = performanceService.getConnectionPoolMetrics(tenantId);
            Map<String, Object> gcMetrics = performanceService.getGarbageCollectionMetrics(tenantId);
            Map<String, Object> threadPoolMetrics = performanceService.getThreadPoolMetrics(tenantId);
            
            // Combine all metrics
            Map<String, Object> dashboard = Map.of(
                "system", systemMetrics,
                "cache", cacheMetrics,
                "connectionPool", connectionPoolMetrics,
                "garbageCollection", gcMetrics,
                "threadPool", threadPoolMetrics,
                "timestamp", java.time.LocalDateTime.now()
            );
            
            logger.info("Generated performance dashboard for tenant {}", tenantId);
            return ResponseEntity.ok(dashboard);
            
        } catch (Exception e) {
            logger.error("Error generating performance dashboard: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
