package com.weavecode.chatwoot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PerformanceMetrics {
    private UUID tenantId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    // System metrics
    private double cpuUsage;
    private double memoryUsage;
    private long heapMemoryUsed;
    private long heapMemoryTotal;
    private long heapMemoryMax;
    private long nonHeapMemoryUsed;
    
    // Application metrics
    private double responseTime;
    private double throughput;
    private int activeConnections;
    private double errorRate;
    
    // Database metrics
    private double databaseResponseTime;
    private int databaseConnections;
    private long databaseQueryCount;
    
    // Cache metrics
    private double cacheHitRate;
    private long cacheSize;
    private long cacheEvictions;
    
    // JVM metrics
    private long gcCount;
    private long gcTime;
    
    // Thread metrics
    private int threadCount;
    private int peakThreadCount;
    private int daemonThreadCount;
    
    // Custom metrics
    private Map<String, Object> customMetrics;
    
    // Constructors
    public PerformanceMetrics() {}
    
    public PerformanceMetrics(UUID tenantId) {
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
    
    public double getCpuUsage() {
        return cpuUsage;
    }
    
    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }
    
    public double getMemoryUsage() {
        return memoryUsage;
    }
    
    public void setMemoryUsage(double memoryUsage) {
        this.memoryUsage = memoryUsage;
    }
    
    public long getHeapMemoryUsed() {
        return heapMemoryUsed;
    }
    
    public void setHeapMemoryUsed(long heapMemoryUsed) {
        this.heapMemoryUsed = heapMemoryUsed;
    }
    
    public long getHeapMemoryTotal() {
        return heapMemoryTotal;
    }
    
    public void setHeapMemoryTotal(long heapMemoryTotal) {
        this.heapMemoryTotal = heapMemoryTotal;
    }
    
    public long getHeapMemoryMax() {
        return heapMemoryMax;
    }
    
    public void setHeapMemoryMax(long heapMemoryMax) {
        this.heapMemoryMax = heapMemoryMax;
    }
    
    public long getNonHeapMemoryUsed() {
        return nonHeapMemoryUsed;
    }
    
    public void setNonHeapMemoryUsed(long nonHeapMemoryUsed) {
        this.nonHeapMemoryUsed = nonHeapMemoryUsed;
    }
    
    public double getResponseTime() {
        return responseTime;
    }
    
    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }
    
    public double getThroughput() {
        return throughput;
    }
    
    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }
    
    public int getActiveConnections() {
        return activeConnections;
    }
    
    public void setActiveConnections(int activeConnections) {
        this.activeConnections = activeConnections;
    }
    
    public double getErrorRate() {
        return errorRate;
    }
    
    public void setErrorRate(double errorRate) {
        this.errorRate = errorRate;
    }
    
    public double getDatabaseResponseTime() {
        return databaseResponseTime;
    }
    
    public void setDatabaseResponseTime(double databaseResponseTime) {
        this.databaseResponseTime = databaseResponseTime;
    }
    
    public int getDatabaseConnections() {
        return databaseConnections;
    }
    
    public void setDatabaseConnections(int databaseConnections) {
        this.databaseConnections = databaseConnections;
    }
    
    public long getDatabaseQueryCount() {
        return databaseQueryCount;
    }
    
    public void setDatabaseQueryCount(long databaseQueryCount) {
        this.databaseQueryCount = databaseQueryCount;
    }
    
    public double getCacheHitRate() {
        return cacheHitRate;
    }
    
    public void setCacheHitRate(double cacheHitRate) {
        this.cacheHitRate = cacheHitRate;
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
    
    public long getGcCount() {
        return gcCount;
    }
    
    public void setGcCount(long gcCount) {
        this.gcCount = gcCount;
    }
    
    public long getGcTime() {
        return gcTime;
    }
    
    public void setGcTime(long gcTime) {
        this.gcTime = gcTime;
    }
    
    public int getThreadCount() {
        return threadCount;
    }
    
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
    
    public int getPeakThreadCount() {
        return peakThreadCount;
    }
    
    public void setPeakThreadCount(int peakThreadCount) {
        this.peakThreadCount = peakThreadCount;
    }
    
    public int getDaemonThreadCount() {
        return daemonThreadCount;
    }
    
    public void setDaemonThreadCount(int daemonThreadCount) {
        this.daemonThreadCount = daemonThreadCount;
    }
    
    public Map<String, Object> getCustomMetrics() {
        return customMetrics;
    }
    
    public void setCustomMetrics(Map<String, Object> customMetrics) {
        this.customMetrics = customMetrics;
    }
    
    // Utility methods
    public double getMemoryUsagePercentage() {
        return memoryUsage;
    }
    
    public long getAvailableHeapMemory() {
        return heapMemoryMax - heapMemoryUsed;
    }
    
    public double getHeapMemoryUsagePercentage() {
        return heapMemoryMax > 0 ? (double) heapMemoryUsed / heapMemoryMax * 100 : 0.0;
    }
    
    public boolean isMemoryUsageHigh() {
        return memoryUsage > 80.0;
    }
    
    public boolean isCpuUsageHigh() {
        return cpuUsage > 80.0;
    }
    
    public boolean isCachePerformanceGood() {
        return cacheHitRate > 80.0;
    }
    
    public boolean isDatabasePerformanceGood() {
        return databaseResponseTime < 100.0; // Less than 100ms
    }
    
    @Override
    public String toString() {
        return "PerformanceMetrics{" +
                "tenantId=" + tenantId +
                ", timestamp=" + timestamp +
                ", cpuUsage=" + cpuUsage +
                ", memoryUsage=" + memoryUsage +
                ", responseTime=" + responseTime +
                ", cacheHitRate=" + cacheHitRate +
                '}';
    }
}
