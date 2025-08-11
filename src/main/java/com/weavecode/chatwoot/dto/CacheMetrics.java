package com.weavecode.chatwoot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CacheMetrics {
    private UUID tenantId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    // Cache performance metrics
    private double cacheHitRate;
    private double cacheMissRate;
    private long cacheSize;
    private long cacheEvictions;
    private double cacheMemoryUsage;
    
    // Redis specific metrics
    private long cacheKeyspaceHits;
    private long cacheKeyspaceMisses;
    private double averageGetTime;
    private double averageSetTime;
    private double cacheThroughput;
    
    // Cache distribution by type
    private Map<String, Long> cacheDistribution;
    
    // Cache efficiency metrics
    private double cacheEfficiency;
    private double cacheUtilisation;
    private long cacheExpirations;
    private long cacheCompactions;
    
    // Constructors
    public CacheMetrics() {}
    
    public CacheMetrics(UUID tenantId) {
        this.tenantId = tenantId;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters and Setters
    public UUID getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public double getCacheHitRate() {
        return cacheHitRate;
    }
    
    public void setCacheHitRate(double cacheHitRate) {
        this.cacheHitRate = cacheHitRate;
    }
    
    public double getCacheMissRate() {
        return cacheMissRate;
    }
    
    public void setCacheMissRate(double cacheMissRate) {
        this.cacheMissRate = cacheMissRate;
    }
    
    public long getCacheSize() {
        return cacheSize;
    }
    
    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }
    
    public long getCacheEvictions() {
        return cacheEvictions;
    }
    
    public void setCacheEvictions(long cacheEvictions) {
        this.cacheEvictions = cacheEvictions;
    }
    
    public double getCacheMemoryUsage() {
        return cacheMemoryUsage;
    }
    
    public void setCacheMemoryUsage(double cacheMemoryUsage) {
        this.cacheMemoryUsage = cacheMemoryUsage;
    }
    
    public long getCacheKeyspaceHits() {
        return cacheKeyspaceHits;
    }
    
    public void setCacheKeyspaceHits(long cacheKeyspaceHits) {
        this.cacheKeyspaceHits = cacheKeyspaceHits;
    }
    
    public long getCacheKeyspaceMisses() {
        return cacheKeyspaceMisses;
    }
    
    public void setCacheKeyspaceMisses(long cacheKeyspaceMisses) {
        this.cacheKeyspaceMisses = cacheKeyspaceMisses;
    }
    
    public double getAverageGetTime() {
        return averageGetTime;
    }
    
    public void setAverageGetTime(double averageGetTime) {
        this.averageGetTime = averageGetTime;
    }
    
    public double getAverageSetTime() {
        return averageSetTime;
    }
    
    public void setAverageSetTime(double averageSetTime) {
        this.averageSetTime = averageSetTime;
    }
    
    public double getCacheThroughput() {
        return cacheThroughput;
    }
    
    public void setCacheThroughput(double cacheThroughput) {
        this.cacheThroughput = cacheThroughput;
    }
    
    public Map<String, Long> getCacheDistribution() {
        return cacheDistribution;
    }
    
    public void setCacheDistribution(Map<String, Long> cacheDistribution) {
        this.cacheDistribution = cacheDistribution;
    }
    
    public double getCacheEfficiency() {
        return cacheEfficiency;
    }
    
    public void setCacheEfficiency(double cacheEfficiency) {
        this.cacheEfficiency = cacheEfficiency;
    }
    
    public double getCacheUtilisation() {
        return cacheUtilisation;
    }
    
    public void setCacheUtilisation(double cacheUtilisation) {
        this.cacheUtilisation = cacheUtilisation;
    }
    
    public long getCacheExpirations() {
        return cacheExpirations;
    }
    
    public void setCacheExpirations(long cacheExpirations) {
        this.cacheExpirations = cacheExpirations;
    }
    
    public long getCacheCompactions() {
        return cacheCompactions;
    }
    
    public void setCacheCompactions(long cacheCompactions) {
        this.cacheCompactions = cacheCompactions;
    }
    
    // Utility methods
    public double getTotalCacheOperations() {
        return cacheKeyspaceHits + cacheKeyspaceMisses;
    }
    
    public double getCacheHitRatio() {
        long total = cacheKeyspaceHits + cacheKeyspaceMisses;
        return total > 0 ? (double) cacheKeyspaceHits / total : 0.0;
    }
    
    public boolean isCachePerformanceGood() {
        return cacheHitRate > 80.0 && averageGetTime < 10.0;
    }
    
    public boolean isCacheMemoryUsageHigh() {
        return cacheMemoryUsage > 80.0;
    }
    
    public boolean isCacheEfficient() {
        return cacheEfficiency > 70.0;
    }
    
    public String getCacheHealthStatus() {
        if (isCachePerformanceGood() && !isCacheMemoryUsageHigh() && isCacheEfficient()) {
            return "HEALTHY";
        } else if (cacheHitRate < 50.0 || cacheMemoryUsage > 90.0) {
            return "CRITICAL";
        } else {
            return "WARNING";
        }
    }
    
    public double getCacheMissRatio() {
        return 100.0 - cacheHitRate;
    }
    
    public long getCacheKeyspaceTotal() {
        return cacheKeyspaceHits + cacheKeyspaceMisses;
    }
    
    @Override
    public String toString() {
        return "CacheMetrics{" +
                "tenantId=" + tenantId +
                ", timestamp=" + timestamp +
                ", cacheHitRate=" + cacheHitRate +
                ", cacheSize=" + cacheSize +
                ", cacheMemoryUsage=" + cacheMemoryUsage +
                ", averageGetTime=" + averageGetTime +
                '}';
    }
}
