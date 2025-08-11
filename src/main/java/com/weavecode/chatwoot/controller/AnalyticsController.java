package com.weavecode.chatwoot.controller;

import com.weavecode.chatwoot.dto.AnalyticsData;
import com.weavecode.chatwoot.dto.ConversationMetrics;
import com.weavecode.chatwoot.dto.UserPerformanceMetrics;
import com.weavecode.chatwoot.dto.TenantUsageStats;
import com.weavecode.chatwoot.service.AnalyticsService;
import com.weavecode.chatwoot.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private SecurityService securityService;

    /**
     * Get conversation metrics for current tenant
     */
    @GetMapping("/conversations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ConversationMetrics> getConversationMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            ConversationMetrics metrics = analyticsService.getConversationMetrics(tenantId, startDate, endDate);
            
            logger.info("Retrieved conversation metrics for tenant {} from {} to {}", tenantId, startDate, endDate);
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            logger.error("Error getting conversation metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get user performance metrics for current tenant
     */
    @GetMapping("/users/performance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserPerformanceMetrics>> getUserPerformanceMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            List<UserPerformanceMetrics> metrics = analyticsService.getUserPerformanceMetrics(tenantId, startDate, endDate);
            
            logger.info("Retrieved user performance metrics for tenant {} from {} to {}", tenantId, startDate, endDate);
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            logger.error("Error getting user performance metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get tenant usage statistics
     */
    @GetMapping("/usage")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TenantUsageStats> getTenantUsageStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            TenantUsageStats stats = analyticsService.getTenantUsageStats(tenantId, startDate, endDate);
            
            logger.info("Retrieved usage statistics for tenant {} from {} to {}", tenantId, startDate, endDate);
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            logger.error("Error getting usage statistics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get response time analytics
     */
    @GetMapping("/response-time")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnalyticsData> getResponseTimeAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            AnalyticsData analytics = analyticsService.getResponseTimeAnalytics(tenantId, startDate, endDate);
            
            logger.info("Retrieved response time analytics for tenant {} from {} to {}", tenantId, startDate, endDate);
            return ResponseEntity.ok(analytics);
            
        } catch (Exception e) {
            logger.error("Error getting response time analytics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get customer satisfaction metrics
     */
    @GetMapping("/satisfaction")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnalyticsData> getCustomerSatisfactionMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            AnalyticsData analytics = analyticsService.getCustomerSatisfactionMetrics(tenantId, startDate, endDate);
            
            logger.info("Retrieved customer satisfaction metrics for tenant {} from {} to {}", tenantId, startDate, endDate);
            return ResponseEntity.ok(analytics);
            
        } catch (Exception e) {
            logger.error("Error getting customer satisfaction metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get automation effectiveness metrics
     */
    @GetMapping("/automation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnalyticsData> getAutomationEffectivenessMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            AnalyticsData analytics = analyticsService.getAutomationEffectivenessMetrics(tenantId, startDate, endDate);
            
            logger.info("Retrieved automation effectiveness metrics for tenant {} from {} to {}", tenantId, startDate, endDate);
            return ResponseEntity.ok(analytics);
            
        } catch (Exception e) {
            logger.error("Error getting automation effectiveness metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get peak hours analysis
     */
    @GetMapping("/peak-hours")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnalyticsData> getPeakHoursAnalysis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            AnalyticsData analytics = analyticsService.getPeakHoursAnalysis(tenantId, startDate, endDate);
            
            logger.info("Retrieved peak hours analysis for tenant {} from {} to {}", tenantId, startDate, endDate);
            return ResponseEntity.ok(analytics);
            
        } catch (Exception e) {
            logger.error("Error getting peak hours analysis: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get conversation volume trends
     */
    @GetMapping("/volume-trends")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnalyticsData> getConversationVolumeTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            AnalyticsData analytics = analyticsService.getConversationVolumeTrends(tenantId, startDate, endDate);
            
            logger.info("Retrieved conversation volume trends for tenant {} from {} to {}", tenantId, startDate, endDate);
            return ResponseEntity.ok(analytics);
            
        } catch (Exception e) {
            logger.error("Error getting conversation volume trends: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get agent workload distribution
     */
    @GetMapping("/agent-workload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnalyticsData> getAgentWorkloadDistribution(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            AnalyticsData analytics = analyticsService.getAgentWorkloadDistribution(tenantId, startDate, endDate);
            
            logger.info("Retrieved agent workload distribution for tenant {} from {} to {}", tenantId, startDate, endDate);
            return ResponseEntity.ok(analytics);
            
        } catch (Exception e) {
            logger.error("Error getting agent workload distribution: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get customer engagement metrics
     */
    @GetMapping("/engagement")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnalyticsData> getCustomerEngagementMetrics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            AnalyticsData analytics = analyticsService.getCustomerEngagementMetrics(tenantId, startDate, endDate);
            
            logger.info("Retrieved customer engagement metrics for tenant {} from {} to {}", tenantId, startDate, endDate);
            return ResponseEntity.ok(analytics);
            
        } catch (Exception e) {
            logger.error("Error getting customer engagement metrics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Generate comprehensive analytics report
     */
    @GetMapping("/comprehensive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnalyticsData> generateComprehensiveReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            AnalyticsData report = analyticsService.generateComprehensiveReport(tenantId, startDate, endDate);
            
            logger.info("Generated comprehensive report for tenant {} from {} to {}", tenantId, startDate, endDate);
            return ResponseEntity.ok(report);
            
        } catch (Exception e) {
            logger.error("Error generating comprehensive report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export analytics data to CSV
     */
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> exportAnalyticsToCSV(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String reportType) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            String csvData = analyticsService.exportAnalyticsToCSV(tenantId, startDate, endDate, reportType);
            
            logger.info("Exported {} analytics data to CSV for tenant {} from {} to {}", reportType, tenantId, startDate, endDate);
            return ResponseEntity.ok()
                    .header("Content-Type", "text/csv")
                    .header("Content-Disposition", "attachment; filename=\"analytics_" + reportType + "_" + startDate + "_" + endDate + ".csv\"")
                    .body(csvData);
            
        } catch (Exception e) {
            logger.error("Error exporting analytics to CSV: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get real-time analytics dashboard data
     */
    @GetMapping("/realtime")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AnalyticsData> getRealTimeDashboardData() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            AnalyticsData dashboardData = analyticsService.getRealTimeDashboardData(tenantId);
            
            logger.debug("Retrieved real-time dashboard data for tenant {}", tenantId);
            return ResponseEntity.ok(dashboardData);
            
        } catch (Exception e) {
            logger.error("Error getting real-time dashboard data: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
