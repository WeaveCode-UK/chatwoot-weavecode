package com.weavecode.chatwoot.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object for tenant usage statistics
 * Provides comprehensive usage metrics for billing and monitoring
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
public class TenantUsageStats {

    private UUID tenantId;
    private String tenantName;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    
    // User metrics
    private int totalUsers;
    private int activeUsers;
    private int adminUsers;
    private int agentUsers;
    
    // Conversation metrics
    private int totalConversations;
    private int activeConversations;
    private int resolvedConversations;
    private int pendingConversations;
    
    // Message metrics
    private int totalMessages;
    private int aiGeneratedMessages;
    private int humanMessages;
    private int attachmentsCount;
    
    // Customer metrics
    private int totalCustomers;
    private int newCustomers;
    private int returningCustomers;
    
    // Automation metrics
    private int totalAutomations;
    private int activeAutomations;
    private int automationTriggers;
    
    // Storage metrics
    private long storageUsedBytes;
    private long storageLimitBytes;
    private double storageUsagePercentage;
    
    // API usage
    private int apiCallsCount;
    private int apiCallsLimit;
    private double apiUsagePercentage;
    
    // Performance metrics
    private double averageResponseTime;
    private double customerSatisfactionScore;
    private int slaBreaches;
    
    // Custom metrics
    private Map<String, Object> customMetrics;
    
    // Timestamps
    private LocalDateTime lastUpdated;
    private LocalDateTime nextResetDate;

    // Constructors
    public TenantUsageStats() {}

    public TenantUsageStats(UUID tenantId, String tenantName) {
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }

    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }

    public LocalDateTime getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDateTime periodStart) { this.periodStart = periodStart; }

    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDateTime periodEnd) { this.periodEnd = periodEnd; }

    public int getTotalUsers() { return totalUsers; }
    public void setTotalUsers(int totalUsers) { this.totalUsers = totalUsers; }

    public int getActiveUsers() { return activeUsers; }
    public void setActiveUsers(int activeUsers) { this.activeUsers = activeUsers; }

    public int getAdminUsers() { return adminUsers; }
    public void setAdminUsers(int adminUsers) { this.adminUsers = adminUsers; }

    public int getAgentUsers() { return agentUsers; }
    public void setAgentUsers(int agentUsers) { this.agentUsers = agentUsers; }

    public int getTotalConversations() { return totalConversations; }
    public void setTotalConversations(int totalConversations) { this.totalConversations = totalConversations; }

    public int getActiveConversations() { return activeConversations; }
    public void setActiveConversations(int activeConversations) { this.activeConversations = activeConversations; }

    public int getResolvedConversations() { return resolvedConversations; }
    public void setResolvedConversations(int resolvedConversations) { this.resolvedConversations = resolvedConversations; }

    public int getPendingConversations() { return pendingConversations; }
    public void setPendingConversations(int pendingConversations) { this.pendingConversations = pendingConversations; }

    public int getTotalMessages() { return totalMessages; }
    public void setTotalMessages(int totalMessages) { this.totalMessages = totalMessages; }

    public int getAiGeneratedMessages() { return aiGeneratedMessages; }
    public void setAiGeneratedMessages(int aiGeneratedMessages) { this.aiGeneratedMessages = aiGeneratedMessages; }

    public int getHumanMessages() { return humanMessages; }
    public void setHumanMessages(int humanMessages) { this.humanMessages = humanMessages; }

    public int getAttachmentsCount() { return attachmentsCount; }
    public void setAttachmentsCount(int attachmentsCount) { this.attachmentsCount = attachmentsCount; }

    public int getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(int totalCustomers) { this.totalCustomers = totalCustomers; }

    public int getNewCustomers() { return newCustomers; }
    public void setNewCustomers(int newCustomers) { this.newCustomers = newCustomers; }

    public int getReturningCustomers() { return returningCustomers; }
    public void setReturningCustomers(int returningCustomers) { this.returningCustomers = returningCustomers; }

    public int getTotalAutomations() { return totalAutomations; }
    public void setTotalAutomations(int totalAutomations) { this.totalAutomations = totalAutomations; }

    public int getActiveAutomations() { return activeAutomations; }
    public void setActiveAutomations(int activeAutomations) { this.activeAutomations = activeAutomations; }

    public int getAutomationTriggers() { return automationTriggers; }
    public void setAutomationTriggers(int automationTriggers) { this.automationTriggers = automationTriggers; }

    public long getStorageUsedBytes() { return storageUsedBytes; }
    public void setStorageUsedBytes(long storageUsedBytes) { this.storageUsedBytes = storageUsedBytes; }

    public long getStorageLimitBytes() { return storageLimitBytes; }
    public void setStorageLimitBytes(long storageLimitBytes) { this.storageLimitBytes = storageLimitBytes; }

    public double getStorageUsagePercentage() { return storageUsagePercentage; }
    public void setStorageUsagePercentage(double storageUsagePercentage) { this.storageUsagePercentage = storageUsagePercentage; }

    public int getApiCallsCount() { return apiCallsCount; }
    public void setApiCallsCount(int apiCallsCount) { this.apiCallsCount = apiCallsCount; }

    public int getApiCallsLimit() { return apiCallsLimit; }
    public void setApiCallsLimit(int apiCallsLimit) { this.apiCallsLimit = apiCallsLimit; }

    public double getApiUsagePercentage() { return apiUsagePercentage; }
    public void setApiUsagePercentage(double apiUsagePercentage) { this.apiUsagePercentage = apiUsagePercentage; }

    public double getAverageResponseTime() { return averageResponseTime; }
    public void setAverageResponseTime(double averageResponseTime) { this.averageResponseTime = averageResponseTime; }

    public double getCustomerSatisfactionScore() { return customerSatisfactionScore; }
    public void setCustomerSatisfactionScore(double customerSatisfactionScore) { this.customerSatisfactionScore = customerSatisfactionScore; }

    public int getSlaBreaches() { return slaBreaches; }
    public void setSlaBreaches(int slaBreaches) { this.slaBreaches = slaBreaches; }

    public Map<String, Object> getCustomMetrics() { return customMetrics; }
    public void setCustomMetrics(Map<String, Object> customMetrics) { this.customMetrics = customMetrics; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public LocalDateTime getNextResetDate() { return nextResetDate; }
    public void setNextResetDate(LocalDateTime nextResetDate) { this.nextResetDate = nextResetDate; }

    // Utility methods
    public boolean isStorageLimitExceeded() {
        return storageUsagePercentage >= 100.0;
    }

    public boolean isApiLimitExceeded() {
        return apiUsagePercentage >= 100.0;
    }

    public double getStorageUsageGB() {
        return storageUsedBytes / (1024.0 * 1024.0 * 1024.0);
    }

    public double getStorageLimitGB() {
        return storageLimitBytes / (1024.0 * 1024.0 * 1024.0);
    }

    public String getStorageUsageFormatted() {
        return String.format("%.2f GB / %.2f GB", getStorageUsageGB(), getStorageLimitGB());
    }

    public String getApiUsageFormatted() {
        return String.format("%d / %d", apiCallsCount, apiCallsLimit);
    }

    @Override
    public String toString() {
        return "TenantUsageStats{" +
                "tenantId=" + tenantId +
                ", tenantName='" + tenantName + '\'' +
                ", totalUsers=" + totalUsers +
                ", totalConversations=" + totalConversations +
                ", totalMessages=" + totalMessages +
                ", storageUsage=" + getStorageUsageFormatted() +
                ", apiUsage=" + getApiUsageFormatted() +
                '}';
    }
}
