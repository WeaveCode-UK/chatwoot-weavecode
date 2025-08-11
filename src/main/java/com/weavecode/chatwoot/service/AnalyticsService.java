package com.weavecode.chatwoot.service;

import com.weavecode.chatwoot.dto.AnalyticsData;
import com.weavecode.chatwoot.dto.ConversationMetrics;
import com.weavecode.chatwoot.dto.UserPerformanceMetrics;
import com.weavecode.chatwoot.dto.TenantUsageStats;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AnalyticsService {
    
    /**
     * Get conversation metrics for a tenant
     */
    ConversationMetrics getConversationMetrics(UUID tenantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get user performance metrics for a tenant
     */
    List<UserPerformanceMetrics> getUserPerformanceMetrics(UUID tenantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get tenant usage statistics
     */
    TenantUsageStats getTenantUsageStats(UUID tenantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get response time analytics
     */
    AnalyticsData getResponseTimeAnalytics(UUID tenantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get customer satisfaction metrics
     */
    AnalyticsData getCustomerSatisfactionMetrics(UUID tenantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get automation effectiveness metrics
     */
    AnalyticsData getAutomationEffectivenessMetrics(UUID tenantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get peak hours analysis
     */
    AnalyticsData getPeakHoursAnalysis(UUID tenantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get conversation volume trends
     */
    AnalyticsData getConversationVolumeTrends(UUID tenantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get agent workload distribution
     */
    AnalyticsData getAgentWorkloadDistribution(UUID tenantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get customer engagement metrics
     */
    AnalyticsData getCustomerEngagementMetrics(UUID tenantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Generate comprehensive analytics report
     */
    AnalyticsData generateComprehensiveReport(UUID tenantId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Export analytics data to CSV
     */
    String exportAnalyticsToCSV(UUID tenantId, LocalDate startDate, LocalDate endDate, String reportType);
    
    /**
     * Get real-time analytics dashboard data
     */
    AnalyticsData getRealTimeDashboardData(UUID tenantId);
}
