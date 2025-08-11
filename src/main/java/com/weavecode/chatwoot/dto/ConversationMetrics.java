package com.weavecode.chatwoot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConversationMetrics {
    private UUID tenantId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime periodStart;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime periodEnd;
    
    // Volume metrics
    private long totalConversations;
    private long newConversations;
    private long resolvedConversations;
    private long reopenedConversations;
    private long activeConversations;
    
    // Response time metrics
    private double averageFirstResponseTime; // in minutes
    private double averageResolutionTime; // in minutes
    private double averageResponseTime; // in minutes
    private long conversationsWithinSLA;
    private long conversationsExceedingSLA;
    
    // Quality metrics
    private double customerSatisfactionScore;
    private long positiveRatings;
    private long negativeRatings;
    private long neutralRatings;
    
    // Agent metrics
    private long totalAgents;
    private long activeAgents;
    private double averageConversationsPerAgent;
    private double averageResolutionTimePerAgent;
    
    // Channel metrics
    private Map<String, Long> conversationsByChannel;
    private Map<String, Double> satisfactionByChannel;
    
    // Time-based metrics
    private Map<String, Long> conversationsByHour;
    private Map<String, Long> conversationsByDay;
    private Map<String, Long> conversationsByMonth;
    
    // Status distribution
    private Map<String, Long> conversationsByStatus;
    
    // Priority metrics
    private Map<String, Long> conversationsByPriority;
    private Map<String, Double> resolutionTimeByPriority;

    // Constructors
    public ConversationMetrics() {}

    public ConversationMetrics(UUID tenantId, LocalDateTime periodStart, LocalDateTime periodEnd) {
        this.tenantId = tenantId;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    // Getters and Setters
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }

    public LocalDateTime getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDateTime periodStart) { this.periodStart = periodStart; }

    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDateTime periodEnd) { this.periodEnd = periodEnd; }

    public long getTotalConversations() { return totalConversations; }
    public void setTotalConversations(long totalConversations) { this.totalConversations = totalConversations; }

    public long getNewConversations() { return newConversations; }
    public void setNewConversations(long newConversations) { this.newConversations = newConversations; }

    public long getResolvedConversations() { return resolvedConversations; }
    public void setResolvedConversations(long resolvedConversations) { this.resolvedConversations = resolvedConversations; }

    public long getReopenedConversations() { return reopenedConversations; }
    public void setReopenedConversations(long reopenedConversations) { this.reopenedConversations = reopenedConversations; }

    public long getActiveConversations() { return activeConversations; }
    public void setActiveConversations(long activeConversations) { this.activeConversations = activeConversations; }

    public double getAverageFirstResponseTime() { return averageFirstResponseTime; }
    public void setAverageFirstResponseTime(double averageFirstResponseTime) { this.averageFirstResponseTime = averageFirstResponseTime; }

    public double getAverageResolutionTime() { return averageResolutionTime; }
    public void setAverageResolutionTime(double averageResolutionTime) { this.averageResolutionTime = averageResolutionTime; }

    public double getAverageResponseTime() { return averageResponseTime; }
    public void setAverageResponseTime(double averageResponseTime) { this.averageResponseTime = averageResponseTime; }

    public long getConversationsWithinSLA() { return conversationsWithinSLA; }
    public void setConversationsWithinSLA(long conversationsWithinSLA) { this.conversationsWithinSLA = conversationsWithinSLA; }

    public long getConversationsExceedingSLA() { return conversationsExceedingSLA; }
    public void setConversationsExceedingSLA(long conversationsExceedingSLA) { this.conversationsExceedingSLA = conversationsExceedingSLA; }

    public double getCustomerSatisfactionScore() { return customerSatisfactionScore; }
    public void setCustomerSatisfactionScore(double customerSatisfactionScore) { this.customerSatisfactionScore = customerSatisfactionScore; }

    public long getPositiveRatings() { return positiveRatings; }
    public void setPositiveRatings(long positiveRatings) { this.positiveRatings = positiveRatings; }

    public long getNegativeRatings() { return negativeRatings; }
    public void setNegativeRatings(long negativeRatings) { this.negativeRatings = negativeRatings; }

    public long getNeutralRatings() { return neutralRatings; }
    public void setNeutralRatings(long neutralRatings) { this.neutralRatings = neutralRatings; }

    public long getTotalAgents() { return totalAgents; }
    public void setTotalAgents(long totalAgents) { this.totalAgents = totalAgents; }

    public long getActiveAgents() { return activeAgents; }
    public void setActiveAgents(long activeAgents) { this.activeAgents = activeAgents; }

    public double getAverageConversationsPerAgent() { return averageConversationsPerAgent; }
    public void setAverageConversationsPerAgent(double averageConversationsPerAgent) { this.averageConversationsPerAgent = averageConversationsPerAgent; }

    public double getAverageResolutionTimePerAgent() { return averageResolutionTimePerAgent; }
    public void setAverageResolutionTimePerAgent(double averageResolutionTimePerAgent) { this.averageResolutionTimePerAgent = averageResolutionTimePerAgent; }

    public Map<String, Long> getConversationsByChannel() { return conversationsByChannel; }
    public void setConversationsByChannel(Map<String, Long> conversationsByChannel) { this.conversationsByChannel = conversationsByChannel; }

    public Map<String, Double> getSatisfactionByChannel() { return satisfactionByChannel; }
    public void setSatisfactionByChannel(Map<String, Double> satisfactionByChannel) { this.satisfactionByChannel = satisfactionByChannel; }

    public Map<String, Long> getConversationsByHour() { return conversationsByHour; }
    public void setConversationsByHour(Map<String, Long> conversationsByHour) { this.conversationsByHour = conversationsByHour; }

    public Map<String, Long> getConversationsByDay() { return conversationsByDay; }
    public void setConversationsByDay(Map<String, Long> conversationsByDay) { this.conversationsByDay = conversationsByDay; }

    public Map<String, Long> getConversationsByMonth() { return conversationsByMonth; }
    public void setConversationsByMonth(Map<String, Long> conversationsByMonth) { this.conversationsByMonth = conversationsByMonth; }

    public Map<String, Long> getConversationsByStatus() { return conversationsByStatus; }
    public void setConversationsByStatus(Map<String, Long> conversationsByStatus) { this.conversationsByStatus = conversationsByStatus; }

    public Map<String, Long> getConversationsByPriority() { return conversationsByPriority; }
    public void setConversationsByPriority(Map<String, Long> conversationsByPriority) { this.conversationsByPriority = conversationsByPriority; }

    public Map<String, Double> getResolutionTimeByPriority() { return resolutionTimeByPriority; }
    public void setResolutionTimeByPriority(Map<String, Double> resolutionTimeByPriority) { this.resolutionTimeByPriority = resolutionTimeByPriority; }

    // Utility methods
    public double getSLAComplianceRate() {
        if (totalConversations == 0) return 0.0;
        return (double) conversationsWithinSLA / totalConversations * 100;
    }

    public double getResolutionRate() {
        if (totalConversations == 0) return 0.0;
        return (double) resolvedConversations / totalConversations * 100;
    }

    public double getReopenRate() {
        if (resolvedConversations == 0) return 0.0;
        return (double) reopenedConversations / resolvedConversations * 100;
    }

    public double getAgentUtilisationRate() {
        if (totalAgents == 0) return 0.0;
        return (double) activeAgents / totalAgents * 100;
    }

    public long getTotalRatings() {
        return positiveRatings + negativeRatings + neutralRatings;
    }

    public double getPositiveRatingPercentage() {
        long total = getTotalRatings();
        if (total == 0) return 0.0;
        return (double) positiveRatings / total * 100;
    }
}
