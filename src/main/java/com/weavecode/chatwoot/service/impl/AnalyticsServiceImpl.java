package com.weavecode.chatwoot.service.impl;

import com.weavecode.chatwoot.dto.AnalyticsData;
import com.weavecode.chatwoot.dto.ConversationMetrics;
import com.weavecode.chatwoot.dto.UserPerformanceMetrics;
import com.weavecode.chatwoot.dto.TenantUsageStats;
import com.weavecode.chatwoot.service.AnalyticsService;
import com.weavecode.chatwoot.service.ConversationService;
import com.weavecode.chatwoot.service.UserService;
import com.weavecode.chatwoot.service.TenantService;
import com.weavecode.chatwoot.service.MessageService;
import com.weavecode.chatwoot.service.CustomerService;
import com.weavecode.chatwoot.service.AutomationService;
import com.weavecode.chatwoot.repository.ConversationRepository;
import com.weavecode.chatwoot.repository.MessageRepository;
import com.weavecode.chatwoot.repository.UserRepository;
import com.weavecode.chatwoot.repository.CustomerRepository;
import com.weavecode.chatwoot.repository.AutomationRepository;
import com.weavecode.chatwoot.enums.ConversationStatus;
import com.weavecode.chatwoot.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsServiceImpl.class);

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AutomationRepository automationRepository;

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserService userService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AutomationService automationService;

    @Override
    public ConversationMetrics getConversationMetrics(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            // Get conversation counts
            long totalConversations = conversationRepository.countByTenantIdAndCreatedAtBetween(tenantId, startDateTime, endDateTime);
            long activeConversations = conversationRepository.countByTenantIdAndStatusAndCreatedAtBetween(tenantId, ConversationStatus.ACTIVE, startDateTime, endDateTime);
            long resolvedConversations = conversationRepository.countByTenantIdAndStatusAndCreatedAtBetween(tenantId, ConversationStatus.RESOLVED, startDateTime, endDateTime);

            // Get response time metrics
            Double avgFirstResponseTime = messageRepository.getAverageFirstResponseTimeByTenantAndPeriod(tenantId, startDateTime, endDateTime);
            Double avgResolutionTime = conversationRepository.getAverageResolutionTimeByTenantAndPeriod(tenantId, startDateTime, endDateTime);

            // Get satisfaction metrics
            Double avgSatisfaction = conversationRepository.getAverageSatisfactionScoreByTenantAndPeriod(tenantId, startDateTime, endDateTime);

            // Get agent metrics
            long totalAgents = userRepository.countByTenantIdAndRole(tenantId, UserRole.AGENT);

            // Get channel distribution
            Map<String, Long> conversationsByChannel = conversationRepository.getConversationCountByChannelAndTenantAndPeriod(tenantId, startDateTime, endDateTime);

            // Get hourly distribution
            Map<String, Long> conversationsByHour = conversationRepository.getConversationCountByHourAndTenantAndPeriod(tenantId, startDateTime, endDateTime);

            // Get status distribution
            Map<String, Long> conversationsByStatus = conversationRepository.getConversationCountByStatusAndTenantAndPeriod(tenantId, startDateTime, endDateTime);

            // Get priority distribution
            Map<String, Long> conversationsByPriority = conversationRepository.getConversationCountByPriorityAndTenantAndPeriod(tenantId, startDateTime, endDateTime);

            ConversationMetrics metrics = new ConversationMetrics();
            metrics.setTenantId(tenantId);
            metrics.setPeriodStart(startDateTime);
            metrics.setPeriodEnd(endDateTime);
            metrics.setTotalConversations(totalConversations);
            metrics.setActiveConversations(activeConversations);
            metrics.setResolvedConversations(resolvedConversations);
            metrics.setAverageFirstResponseTime(avgFirstResponseTime != null ? avgFirstResponseTime : 0.0);
            metrics.setAverageResolutionTime(avgResolutionTime != null ? avgResolutionTime : 0.0);
            metrics.setCustomerSatisfactionScore(avgSatisfaction != null ? avgSatisfaction : 0.0);
            metrics.setTotalAgents(totalAgents);
            metrics.setConversationsByChannel(conversationsByChannel);
            metrics.setConversationsByHour(conversationsByHour);
            metrics.setConversationsByStatus(conversationsByStatus);
            metrics.setConversationsByPriority(conversationsByPriority);

            logger.info("Generated conversation metrics for tenant {}: {} conversations", tenantId, totalConversations);
            return metrics;

        } catch (Exception e) {
            logger.error("Error generating conversation metrics for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate conversation metrics", e);
        }
    }

    @Override
    public List<UserPerformanceMetrics> getUserPerformanceMetrics(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            List<UserPerformanceMetrics> metrics = new ArrayList<>();

            // Get all agents for the tenant
            List<UUID> agentIds = userRepository.findUserIdsByTenantIdAndRole(tenantId, UserRole.AGENT);

            for (UUID agentId : agentIds) {
                UserPerformanceMetrics userMetrics = new UserPerformanceMetrics();
                userMetrics.setUserId(agentId);
                userMetrics.setTenantId(tenantId);
                userMetrics.setPeriodStart(startDateTime);
                userMetrics.setPeriodEnd(endDateTime);

                // Get conversation metrics
                long totalConversations = conversationRepository.countByAssignedUserIdAndCreatedAtBetween(agentId, startDateTime, endDateTime);
                long resolvedConversations = conversationRepository.countByAssignedUserIdAndStatusAndCreatedAtBetween(agentId, ConversationStatus.RESOLVED, startDateTime, endDateTime);
                long activeConversations = conversationRepository.countByAssignedUserIdAndStatusAndCreatedAtBetween(agentId, ConversationStatus.ACTIVE, startDateTime, endDateTime);

                userMetrics.setTotalConversations(totalConversations);
                userMetrics.setResolvedConversations(resolvedConversations);
                userMetrics.setActiveConversations(activeConversations);

                // Get response time metrics
                Double avgFirstResponseTime = messageRepository.getAverageFirstResponseTimeByUserAndPeriod(agentId, startDateTime, endDateTime);
                Double avgResolutionTime = conversationRepository.getAverageResolutionTimeByUserAndPeriod(agentId, startDateTime, endDateTime);

                userMetrics.setAverageFirstResponseTime(avgFirstResponseTime != null ? avgFirstResponseTime : 0.0);
                userMetrics.setAverageResolutionTime(avgResolutionTime != null ? avgResolutionTime : 0.0);

                // Get satisfaction metrics
                Double avgSatisfaction = conversationRepository.getAverageSatisfactionScoreByUserAndPeriod(agentId, startDateTime, endDateTime);
                userMetrics.setAverageSatisfactionScore(avgSatisfaction != null ? avgSatisfaction : 0.0);

                // Get message metrics
                long totalMessages = messageRepository.countByUserIdAndCreatedAtBetween(agentId, startDateTime, endDateTime);
                userMetrics.setTotalMessages(totalMessages);

                // Calculate efficiency metrics
                if (totalConversations > 0) {
                    userMetrics.setResolutionRate((double) resolvedConversations / totalConversations * 100);
                    userMetrics.setEfficiencyScore(calculateEfficiencyScore(resolvedConversations, avgFirstResponseTime, avgSatisfaction));
                }

                metrics.add(userMetrics);
            }

            logger.info("Generated performance metrics for {} agents in tenant {}", metrics.size(), tenantId);
            return metrics;

        } catch (Exception e) {
            logger.error("Error generating user performance metrics for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate user performance metrics", e);
        }
    }

    @Override
    public TenantUsageStats getTenantUsageStats(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            TenantUsageStats stats = new TenantUsageStats();
            stats.setTenantId(tenantId);
            stats.setPeriodStart(startDateTime);
            stats.setPeriodEnd(endDateTime);

            // Get conversation usage
            long totalConversations = conversationRepository.countByTenantIdAndCreatedAtBetween(tenantId, startDateTime, endDateTime);
            long totalMessages = messageRepository.countByTenantIdAndCreatedAtBetween(tenantId, startDateTime, endDateTime);
            long totalCustomers = customerRepository.countByTenantIdAndCreatedAtBetween(tenantId, startDateTime, endDateTime);

            stats.setTotalConversations(totalConversations);
            stats.setTotalMessages(totalMessages);
            stats.setTotalCustomers(totalCustomers);

            // Get automation usage
            long totalAutomations = automationRepository.countByTenantIdAndCreatedAtBetween(tenantId, startDateTime, endDateTime);
            long triggeredAutomations = automationRepository.countByTenantIdAndTriggeredAtBetween(tenantId, startDateTime, endDateTime);

            stats.setTotalAutomations(totalAutomations);
            stats.setTriggeredAutomations(triggeredAutomations);

            // Get user activity
            long activeUsers = userRepository.countByTenantIdAndLastActiveAtAfter(tenantId, startDateTime);
            stats.setActiveUsers(activeUsers);

            // Calculate daily averages
            long daysInPeriod = startDate.until(endDate).getDays() + 1;
            if (daysInPeriod > 0) {
                stats.setAverageDailyConversations((double) totalConversations / daysInPeriod);
                stats.setAverageDailyMessages((double) totalMessages / daysInPeriod);
                stats.setAverageDailyCustomers((double) totalCustomers / daysInPeriod);
            }

            // Get peak usage hours
            Map<String, Long> peakHours = conversationRepository.getConversationCountByHourAndTenantAndPeriod(tenantId, startDateTime, endDateTime);
            stats.setPeakUsageHours(peakHours);

            logger.info("Generated usage stats for tenant {}: {} conversations, {} messages", tenantId, totalConversations, totalMessages);
            return stats;

        } catch (Exception e) {
            logger.error("Error generating tenant usage stats for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate tenant usage stats", e);
        }
    }

    @Override
    public AnalyticsData getResponseTimeAnalytics(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            AnalyticsData analyticsData = new AnalyticsData();
            analyticsData.setReportType("Response Time Analytics");
            analyticsData.setTenantId(tenantId.toString());
            analyticsData.setGeneratedAt(LocalDateTime.now());

            // Get response time metrics
            Double avgFirstResponse = messageRepository.getAverageFirstResponseTimeByTenantAndPeriod(tenantId, startDateTime, endDateTime);
            Double avgResolution = conversationRepository.getAverageResolutionTimeByTenantAndPeriod(tenantId, startDateTime, endDateTime);

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("averageFirstResponseTime", avgFirstResponse != null ? avgFirstResponse : 0.0);
            metrics.put("averageResolutionTime", avgResolution != null ? avgResolution : 0.0);
            metrics.put("responseTimeTarget", 300.0); // 5 minutes target
            metrics.put("resolutionTimeTarget", 3600.0); // 1 hour target

            analyticsData.setMetrics(metrics);

            // Get time series data for response times
            List<AnalyticsData.DataPoint> timeSeriesData = new ArrayList<>();
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                LocalDateTime dayStart = currentDate.atStartOfDay();
                LocalDateTime dayEnd = currentDate.atTime(LocalTime.MAX);

                Double dayAvgResponse = messageRepository.getAverageFirstResponseTimeByTenantAndPeriod(tenantId, dayStart, dayEnd);
                Double dayAvgResolution = conversationRepository.getAverageResolutionTimeByTenantAndPeriod(tenantId, dayStart, dayEnd);

                AnalyticsData.DataPoint dataPoint = new AnalyticsData.DataPoint();
                dataPoint.setTimestamp(dayStart);
                dataPoint.setValue(dayAvgResponse != null ? dayAvgResponse : 0.0);
                dataPoint.setLabel(currentDate.format(DateTimeFormatter.ofPattern("MMM dd")));

                timeSeriesData.add(dataPoint);
                currentDate = currentDate.plusDays(1);
            }

            analyticsData.setTimeSeriesData(timeSeriesData);

            // Generate insights
            List<String> insights = new ArrayList<>();
            if (avgFirstResponse != null && avgFirstResponse > 300) {
                insights.add("First response time is above target (5 minutes)");
            }
            if (avgResolution != null && avgResolution > 3600) {
                insights.add("Resolution time is above target (1 hour)");
            }
            if (insights.isEmpty()) {
                insights.add("Response times are within acceptable targets");
            }

            analyticsData.setInsights(insights);

            logger.info("Generated response time analytics for tenant {}", tenantId);
            return analyticsData;

        } catch (Exception e) {
            logger.error("Error generating response time analytics for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate response time analytics", e);
        }
    }

    @Override
    public AnalyticsData getCustomerSatisfactionMetrics(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            AnalyticsData analyticsData = new AnalyticsData();
            analyticsData.setReportType("Customer Satisfaction Metrics");
            analyticsData.setTenantId(tenantId.toString());
            analyticsData.setGeneratedAt(LocalDateTime.now());

            // Get satisfaction metrics
            Double avgSatisfaction = conversationRepository.getAverageSatisfactionScoreByTenantAndPeriod(tenantId, startDateTime, endDateTime);
            Long totalRatedConversations = conversationRepository.countByTenantIdAndSatisfactionScoreNotNullAndCreatedAtBetween(tenantId, startDateTime, endDateTime);

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("averageSatisfactionScore", avgSatisfaction != null ? avgSatisfaction : 0.0);
            metrics.put("totalRatedConversations", totalRatedConversations != null ? totalRatedConversations : 0L);
            metrics.put("satisfactionTarget", 4.0); // Target score out of 5

            analyticsData.setMetrics(metrics);

            // Get satisfaction distribution
            Map<String, Long> satisfactionDistribution = conversationRepository.getSatisfactionScoreDistributionByTenantAndPeriod(tenantId, startDateTime, endDateTime);
            analyticsData.setChartData(createSatisfactionChartData(satisfactionDistribution));

            // Generate insights
            List<String> insights = new ArrayList<>();
            if (avgSatisfaction != null) {
                if (avgSatisfaction >= 4.5) {
                    insights.add("Excellent customer satisfaction score");
                } else if (avgSatisfaction >= 4.0) {
                    insights.add("Good customer satisfaction score");
                } else if (avgSatisfaction >= 3.0) {
                    insights.add("Average customer satisfaction score - room for improvement");
                } else {
                    insights.add("Low customer satisfaction score - immediate attention required");
                }
            }

            analyticsData.setInsights(insights);

            logger.info("Generated customer satisfaction metrics for tenant {}", tenantId);
            return analyticsData;

        } catch (Exception e) {
            logger.error("Error generating customer satisfaction metrics for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate customer satisfaction metrics", e);
        }
    }

    @Override
    public AnalyticsData getAutomationEffectivenessMetrics(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            AnalyticsData analyticsData = new AnalyticsData();
            analyticsData.setReportType("Automation Effectiveness Metrics");
            analyticsData.setTenantId(tenantId.toString());
            analyticsData.setGeneratedAt(LocalDateTime.now());

            // Get automation metrics
            long totalAutomations = automationRepository.countByTenantIdAndCreatedAtBetween(tenantId, startDateTime, endDateTime);
            long triggeredAutomations = automationRepository.countByTenantIdAndTriggeredAtBetween(tenantId, startDateTime, endDateTime);
            long successfulAutomations = automationRepository.countByTenantIdAndSuccessAndTriggeredAtBetween(tenantId, true, startDateTime, endDateTime);

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("totalAutomations", totalAutomations);
            metrics.put("triggeredAutomations", triggeredAutomations);
            metrics.put("successfulAutomations", successfulAutomations);
            metrics.put("automationSuccessRate", totalAutomations > 0 ? (double) successfulAutomations / totalAutomations * 100 : 0.0);

            analyticsData.setMetrics(metrics);

            // Get automation type distribution
            Map<String, Long> automationTypeDistribution = automationRepository.getAutomationCountByTypeAndTenantAndPeriod(tenantId, startDateTime, endDateTime);
            analyticsData.setChartData(createAutomationChartData(automationTypeDistribution));

            // Generate insights
            List<String> insights = new ArrayList<>();
            if (totalAutomations > 0) {
                double successRate = (double) successfulAutomations / totalAutomations * 100;
                if (successRate >= 90) {
                    insights.add("Excellent automation success rate");
                } else if (successRate >= 80) {
                    insights.add("Good automation success rate");
                } else if (successRate >= 70) {
                    insights.add("Average automation success rate - consider optimization");
                } else {
                    insights.add("Low automation success rate - review and improve");
                }
            }

            analyticsData.setInsights(insights);

            logger.info("Generated automation effectiveness metrics for tenant {}", tenantId);
            return analyticsData;

        } catch (Exception e) {
            logger.error("Error generating automation effectiveness metrics for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate automation effectiveness metrics", e);
        }
    }

    @Override
    public AnalyticsData getPeakHoursAnalysis(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            AnalyticsData analyticsData = new AnalyticsData();
            analyticsData.setReportType("Peak Hours Analysis");
            analyticsData.setTenantId(tenantId.toString());
            analyticsData.setGeneratedAt(LocalDateTime.now());

            // Get hourly distribution
            Map<String, Long> hourlyDistribution = conversationRepository.getConversationCountByHourAndTenantAndPeriod(tenantId, startDateTime, endDateTime);

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("totalConversations", hourlyDistribution.values().stream().mapToLong(Long::longValue).sum());
            metrics.put("peakHour", findPeakHour(hourlyDistribution));
            metrics.put("peakVolume", findPeakVolume(hourlyDistribution));

            analyticsData.setMetrics(metrics);

            // Create chart data for hourly distribution
            List<AnalyticsData.ChartData> chartData = new ArrayList<>();
            AnalyticsData.ChartData hourlyChart = new AnalyticsData.ChartData();
            hourlyChart.setType("line");
            hourlyChart.setLabel("Conversations by Hour");
            hourlyChart.setData(createHourlyDataset(hourlyDistribution));

            chartData.add(hourlyChart);
            analyticsData.setChartData(chartData);

            // Generate insights
            List<String> insights = new ArrayList<>();
            String peakHour = findPeakHour(hourlyDistribution);
            if (peakHour != null) {
                insights.add("Peak activity occurs at " + peakHour);
                insights.add("Consider increasing staff during peak hours");
            }

            analyticsData.setInsights(insights);

            logger.info("Generated peak hours analysis for tenant {}", tenantId);
            return analyticsData;

        } catch (Exception e) {
            logger.error("Error generating peak hours analysis for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate peak hours analysis", e);
        }
    }

    @Override
    public AnalyticsData getConversationVolumeTrends(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            AnalyticsData analyticsData = new AnalyticsData();
            analyticsData.setReportType("Conversation Volume Trends");
            analyticsData.setTenantId(tenantId.toString());
            analyticsData.setGeneratedAt(LocalDateTime.now());

            // Get daily conversation counts
            List<AnalyticsData.DataPoint> timeSeriesData = new ArrayList<>();
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                LocalDateTime dayStart = currentDate.atStartOfDay();
                LocalDateTime dayEnd = currentDate.atTime(LocalTime.MAX);

                long dailyConversations = conversationRepository.countByTenantIdAndCreatedAtBetween(tenantId, dayStart, dayEnd);

                AnalyticsData.DataPoint dataPoint = new AnalyticsData.DataPoint();
                dataPoint.setTimestamp(dayStart);
                dataPoint.setValue((double) dailyConversations);
                dataPoint.setLabel(currentDate.format(DateTimeFormatter.ofPattern("MMM dd")));

                timeSeriesData.add(dataPoint);
                currentDate = currentDate.plusDays(1);
            }

            analyticsData.setTimeSeriesData(timeSeriesData);

            // Calculate trends
            Map<String, Object> metrics = calculateTrendMetrics(timeSeriesData);
            analyticsData.setMetrics(metrics);

            // Generate insights
            List<String> insights = generateTrendInsights(metrics);
            analyticsData.setInsights(insights);

            logger.info("Generated conversation volume trends for tenant {}", tenantId);
            return analyticsData;

        } catch (Exception e) {
            logger.error("Error generating conversation volume trends for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate conversation volume trends", e);
        }
    }

    @Override
    public AnalyticsData getAgentWorkloadDistribution(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            AnalyticsData analyticsData = new AnalyticsData();
            analyticsData.setReportType("Agent Workload Distribution");
            analyticsData.setTenantId(tenantId.toString());
            analyticsData.setGeneratedAt(LocalDateTime.now());

            // Get agent workload data
            Map<String, Long> agentWorkload = conversationRepository.getConversationCountByAgentAndTenantAndPeriod(tenantId, startDateTime, endDateTime);

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("totalAgents", agentWorkload.size());
            metrics.put("totalConversations", agentWorkload.values().stream().mapToLong(Long::longValue).sum());
            metrics.put("averageConversationsPerAgent", agentWorkload.values().stream().mapToLong(Long::longValue).average().orElse(0.0));

            analyticsData.setMetrics(metrics);

            // Create chart data
            List<AnalyticsData.ChartData> chartData = new ArrayList<>();
            AnalyticsData.ChartData workloadChart = new AnalyticsData.ChartData();
            workloadChart.setType("bar");
            workloadChart.setLabel("Agent Workload");
            workloadChart.setData(createWorkloadDataset(agentWorkload));

            chartData.add(workloadChart);
            analyticsData.setChartData(chartData);

            // Generate insights
            List<String> insights = generateWorkloadInsights(agentWorkload);
            analyticsData.setInsights(insights);

            logger.info("Generated agent workload distribution for tenant {}", tenantId);
            return analyticsData;

        } catch (Exception e) {
            logger.error("Error generating agent workload distribution for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate agent workload distribution", e);
        }
    }

    @Override
    public AnalyticsData getCustomerEngagementMetrics(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        try {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

            AnalyticsData analyticsData = new AnalyticsData();
            analyticsData.setReportType("Customer Engagement Metrics");
            analyticsData.setTenantId(tenantId.toString());
            analyticsData.setGeneratedAt(LocalDateTime.now());

            // Get customer engagement metrics
            long totalCustomers = customerRepository.countByTenantIdAndCreatedAtBetween(tenantId, startDateTime, endDateTime);
            long activeCustomers = customerRepository.countByTenantIdAndLastActiveAtAfter(tenantId, startDateTime);
            long returningCustomers = customerRepository.countByTenantIdAndConversationCountGreaterThanAndCreatedAtBetween(tenantId, 1L, startDateTime, endDateTime);

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("totalCustomers", totalCustomers);
            metrics.put("activeCustomers", activeCustomers);
            metrics.put("returningCustomers", returningCustomers);
            metrics.put("engagementRate", totalCustomers > 0 ? (double) activeCustomers / totalCustomers * 100 : 0.0);
            metrics.put("retentionRate", totalCustomers > 0 ? (double) returningCustomers / totalCustomers * 100 : 0.0);

            analyticsData.setMetrics(metrics);

            // Get customer activity distribution
            Map<String, Long> customerActivity = customerRepository.getCustomerCountByActivityLevelAndTenantAndPeriod(tenantId, startDateTime, endDateTime);
            analyticsData.setChartData(createCustomerActivityChartData(customerActivity));

            // Generate insights
            List<String> insights = generateCustomerEngagementInsights(metrics);
            analyticsData.setInsights(insights);

            logger.info("Generated customer engagement metrics for tenant {}", tenantId);
            return analyticsData;

        } catch (Exception e) {
            logger.error("Error generating customer engagement metrics for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate customer engagement metrics", e);
        }
    }

    @Override
    public AnalyticsData generateComprehensiveReport(UUID tenantId, LocalDate startDate, LocalDate endDate) {
        try {
            AnalyticsData comprehensiveReport = new AnalyticsData();
            comprehensiveReport.setReportType("Comprehensive Analytics Report");
            comprehensiveReport.setTenantId(tenantId.toString());
            comprehensiveReport.setGeneratedAt(LocalDateTime.now());

            // Generate all individual reports
            ConversationMetrics conversationMetrics = getConversationMetrics(tenantId, startDate, endDate);
            List<UserPerformanceMetrics> userMetrics = getUserPerformanceMetrics(tenantId, startDate, endDate);
            TenantUsageStats usageStats = getTenantUsageStats(tenantId, startDate, endDate);

            // Combine metrics
            Map<String, Object> combinedMetrics = new HashMap<>();
            combinedMetrics.put("conversationMetrics", conversationMetrics);
            combinedMetrics.put("userMetrics", userMetrics);
            combinedMetrics.put("usageStats", usageStats);

            comprehensiveReport.setMetrics(combinedMetrics);

            // Generate summary
            Map<String, Object> summary = generateComprehensiveSummary(conversationMetrics, userMetrics, usageStats);
            comprehensiveReport.setSummary(summary);

            // Generate insights
            List<String> insights = generateComprehensiveInsights(conversationMetrics, userMetrics, usageStats);
            comprehensiveReport.setInsights(insights);

            // Generate recommendations
            Map<String, Object> recommendations = generateComprehensiveRecommendations(conversationMetrics, userMetrics, usageStats);
            comprehensiveReport.setRecommendations(recommendations);

            logger.info("Generated comprehensive report for tenant {}", tenantId);
            return comprehensiveReport;

        } catch (Exception e) {
            logger.error("Error generating comprehensive report for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate comprehensive report", e);
        }
    }

    @Override
    public String exportAnalyticsToCSV(UUID tenantId, LocalDate startDate, LocalDate endDate, String reportType) {
        try {
            // This would implement CSV export logic
            // For now, return a placeholder
            logger.info("CSV export requested for tenant {}: {} report from {} to {}", tenantId, reportType, startDate, endDate);
            return "CSV export functionality to be implemented";
        } catch (Exception e) {
            logger.error("Error exporting analytics to CSV for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Failed to export analytics to CSV", e);
        }
    }

    @Override
    public AnalyticsData getRealTimeDashboardData(UUID tenantId) {
        try {
            AnalyticsData realTimeData = new AnalyticsData();
            realTimeData.setReportType("Real-time Dashboard Data");
            realTimeData.setTenantId(tenantId.toString());
            realTimeData.setGeneratedAt(LocalDateTime.now());

            // Get real-time metrics
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime last24Hours = now.minusHours(24);

            long activeConversations = conversationRepository.countByTenantIdAndStatus(tenantId, ConversationStatus.ACTIVE);
            long conversationsLast24h = conversationRepository.countByTenantIdAndCreatedAtBetween(tenantId, last24Hours, now);
            long messagesLast24h = messageRepository.countByTenantIdAndCreatedAtBetween(tenantId, last24Hours, now);
            long onlineUsers = userRepository.countByTenantIdAndLastActiveAtAfter(tenantId, now.minusMinutes(5));

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("activeConversations", activeConversations);
            metrics.put("conversationsLast24h", conversationsLast24h);
            metrics.put("messagesLast24h", messagesLast24h);
            metrics.put("onlineUsers", onlineUsers);

            realTimeData.setMetrics(metrics);

            logger.info("Generated real-time dashboard data for tenant {}", tenantId);
            return realTimeData;

        } catch (Exception e) {
            logger.error("Error generating real-time dashboard data for tenant {}: {}", tenantId, e.getMessage(), e);
            throw new RuntimeException("Failed to generate real-time dashboard data", e);
        }
    }

    // Helper methods
    private double calculateEfficiencyScore(long resolvedConversations, Double avgResponseTime, Double avgSatisfaction) {
        double score = 0.0;
        
        // Base score from resolved conversations
        score += Math.min(resolvedConversations * 10, 100);
        
        // Response time bonus (faster = higher score)
        if (avgResponseTime != null && avgResponseTime > 0) {
            if (avgResponseTime <= 300) score += 50; // 5 minutes or less
            else if (avgResponseTime <= 600) score += 30; // 10 minutes or less
            else if (avgResponseTime <= 1800) score += 10; // 30 minutes or less
        }
        
        // Satisfaction bonus
        if (avgSatisfaction != null && avgSatisfaction > 0) {
            score += avgSatisfaction * 10;
        }
        
        return Math.min(score, 100.0);
    }

    private List<AnalyticsData.ChartData> createSatisfactionChartData(Map<String, Long> satisfactionDistribution) {
        List<AnalyticsData.ChartData> chartData = new ArrayList<>();
        
        AnalyticsData.ChartData satisfactionChart = new AnalyticsData.ChartData();
        satisfactionChart.setType("pie");
        satisfactionChart.setLabel("Satisfaction Distribution");
        
        List<AnalyticsData.Dataset> datasets = new ArrayList<>();
        AnalyticsData.Dataset dataset = new AnalyticsData.Dataset();
        dataset.setLabel("Satisfaction Scores");
        dataset.setData(new ArrayList<>(satisfactionDistribution.values()));
        dataset.setBackgroundColor(Arrays.asList("#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF"));
        
        datasets.add(dataset);
        satisfactionChart.setData(datasets);
        chartData.add(satisfactionChart);
        
        return chartData;
    }

    private List<AnalyticsData.ChartData> createAutomationChartData(Map<String, Long> automationTypeDistribution) {
        List<AnalyticsData.ChartData> chartData = new ArrayList<>();
        
        AnalyticsData.ChartData automationChart = new AnalyticsData.ChartData();
        automationChart.setType("doughnut");
        automationChart.setLabel("Automation Types");
        
        List<AnalyticsData.Dataset> datasets = new ArrayList<>();
        AnalyticsData.Dataset dataset = new AnalyticsData.Dataset();
        dataset.setLabel("Automation Count");
        dataset.setData(new ArrayList<>(automationTypeDistribution.values()));
        dataset.setBackgroundColor(Arrays.asList("#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0"));
        
        datasets.add(dataset);
        automationChart.setData(datasets);
        chartData.add(automationChart);
        
        return chartData;
    }

    private String findPeakHour(Map<String, Long> hourlyDistribution) {
        return hourlyDistribution.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private Long findPeakVolume(Map<String, Long> hourlyDistribution) {
        return hourlyDistribution.values().stream()
                .max(Long::compareTo)
                .orElse(0L);
    }

    private List<AnalyticsData.Dataset> createHourlyDataset(Map<String, Long> hourlyDistribution) {
        List<AnalyticsData.Dataset> datasets = new ArrayList<>();
        
        AnalyticsData.Dataset dataset = new AnalyticsData.Dataset();
        dataset.setLabel("Conversations");
        dataset.setData(new ArrayList<>(hourlyDistribution.values()));
        dataset.setBackgroundColor("#36A2EB");
        dataset.setBorderColor("#36A2EB");
        
        datasets.add(dataset);
        return datasets;
    }

    private Map<String, Object> calculateTrendMetrics(List<AnalyticsData.DataPoint> timeSeriesData) {
        Map<String, Object> metrics = new HashMap<>();
        
        if (timeSeriesData.size() < 2) {
            metrics.put("trend", "insufficient_data");
            metrics.put("trendPercentage", 0.0);
            return metrics;
        }
        
        double firstValue = timeSeriesData.get(0).getValue();
        double lastValue = timeSeriesData.get(timeSeriesData.size() - 1).getValue();
        
        double trendPercentage = firstValue > 0 ? ((lastValue - firstValue) / firstValue) * 100 : 0.0;
        String trend = trendPercentage > 0 ? "increasing" : trendPercentage < 0 ? "decreasing" : "stable";
        
        metrics.put("trend", trend);
        metrics.put("trendPercentage", trendPercentage);
        
        return metrics;
    }

    private List<String> generateTrendInsights(Map<String, Object> metrics) {
        List<String> insights = new ArrayList<>();
        
        String trend = (String) metrics.get("trend");
        Double trendPercentage = (Double) metrics.get("trendPercentage");
        
        if ("increasing".equals(trend)) {
            insights.add("Conversation volume is increasing");
            if (trendPercentage > 20) {
                insights.add("Significant growth in conversation volume - consider scaling up support");
            }
        } else if ("decreasing".equals(trend)) {
            insights.add("Conversation volume is decreasing");
            if (trendPercentage < -20) {
                insights.add("Significant decline in conversation volume - investigate potential issues");
            }
        } else {
            insights.add("Conversation volume is stable");
        }
        
        return insights;
    }

    private List<AnalyticsData.Dataset> createWorkloadDataset(Map<String, Long> agentWorkload) {
        List<AnalyticsData.Dataset> datasets = new ArrayList<>();
        
        AnalyticsData.Dataset dataset = new AnalyticsData.Dataset();
        dataset.setLabel("Conversations");
        dataset.setData(new ArrayList<>(agentWorkload.values()));
        dataset.setBackgroundColor("#FF6384");
        dataset.setBorderColor("#FF6384");
        
        datasets.add(dataset);
        return datasets;
    }

    private List<String> generateWorkloadInsights(Map<String, Long> agentWorkload) {
        List<String> insights = new ArrayList<>();
        
        if (agentWorkload.isEmpty()) {
            insights.add("No agent workload data available");
            return insights;
        }
        
        double average = agentWorkload.values().stream().mapToLong(Long::longValue).average().orElse(0.0);
        long max = agentWorkload.values().stream().mapToLong(Long::longValue).max().orElse(0L);
        long min = agentWorkload.values().stream().mapToLong(Long::longValue).min().orElse(0L);
        
        if (max > average * 2) {
            insights.add("Some agents have significantly higher workload than average");
        }
        
        if (min < average * 0.5) {
            insights.add("Some agents have significantly lower workload than average");
        }
        
        if (max - min <= average * 0.5) {
            insights.add("Workload is well distributed among agents");
        }
        
        return insights;
    }

    private List<AnalyticsData.ChartData> createCustomerActivityChartData(Map<String, Long> customerActivity) {
        List<AnalyticsData.ChartData> chartData = new ArrayList<>();
        
        AnalyticsData.ChartData activityChart = new AnalyticsData.ChartData();
        activityChart.setType("bar");
        activityChart.setLabel("Customer Activity Levels");
        
        List<AnalyticsData.Dataset> datasets = new ArrayList<>();
        AnalyticsData.Dataset dataset = new AnalyticsData.Dataset();
        dataset.setLabel("Customer Count");
        dataset.setData(new ArrayList<>(customerActivity.values()));
        dataset.setBackgroundColor("#4BC0C0");
        dataset.setBorderColor("#4BC0C0");
        
        datasets.add(dataset);
        activityChart.setData(datasets);
        chartData.add(activityChart);
        
        return chartData;
    }

    private List<String> generateCustomerEngagementInsights(Map<String, Object> metrics) {
        List<String> insights = new ArrayList<>();
        
        Double engagementRate = (Double) metrics.get("engagementRate");
        Double retentionRate = (Double) metrics.get("retentionRate");
        
        if (engagementRate >= 80) {
            insights.add("Excellent customer engagement rate");
        } else if (engagementRate >= 60) {
            insights.add("Good customer engagement rate");
        } else {
            insights.add("Low customer engagement rate - consider improving customer experience");
        }
        
        if (retentionRate >= 70) {
            insights.add("Strong customer retention");
        } else if (retentionRate >= 50) {
            insights.add("Moderate customer retention");
        } else {
            insights.add("Low customer retention - focus on customer satisfaction");
        }
        
        return insights;
    }

    private Map<String, Object> generateComprehensiveSummary(ConversationMetrics conversationMetrics, 
                                                           List<UserPerformanceMetrics> userMetrics, 
                                                           TenantUsageStats usageStats) {
        Map<String, Object> summary = new HashMap<>();
        
        summary.put("totalConversations", conversationMetrics.getTotalConversations());
        summary.put("totalMessages", usageStats.getTotalMessages());
        summary.put("totalCustomers", usageStats.getTotalCustomers());
        summary.put("totalAgents", conversationMetrics.getTotalAgents());
        summary.put("averageResponseTime", conversationMetrics.getAverageFirstResponseTime());
        summary.put("averageSatisfaction", conversationMetrics.getCustomerSatisfactionScore());
        
        return summary;
    }

    private List<String> generateComprehensiveInsights(ConversationMetrics conversationMetrics, 
                                                     List<UserPerformanceMetrics> userMetrics, 
                                                     TenantUsageStats usageStats) {
        List<String> insights = new ArrayList<>();
        
        // Conversation insights
        if (conversationMetrics.getAverageFirstResponseTime() > 300) {
            insights.add("First response time is above target - consider increasing support staff");
        }
        
        if (conversationMetrics.getCustomerSatisfactionScore() < 4.0) {
            insights.add("Customer satisfaction is below target - review support quality");
        }
        
        // User performance insights
        double avgEfficiency = userMetrics.stream()
                .mapToDouble(UserPerformanceMetrics::getEfficiencyScore)
                .average()
                .orElse(0.0);
        
        if (avgEfficiency < 70) {
            insights.add("Agent efficiency is below target - consider training or process improvements");
        }
        
        // Usage insights
        if (usageStats.getTotalAutomations() > 0) {
            double automationUsage = (double) usageStats.getTriggeredAutomations() / usageStats.getTotalAutomations();
            if (automationUsage < 0.5) {
                insights.add("Automation usage is low - consider optimizing triggers");
            }
        }
        
        return insights;
    }

    private Map<String, Object> generateComprehensiveRecommendations(ConversationMetrics conversationMetrics, 
                                                                   List<UserPerformanceMetrics> userMetrics, 
                                                                   TenantUsageStats usageStats) {
        Map<String, Object> recommendations = new HashMap<>();
        
        List<String> immediate = new ArrayList<>();
        List<String> shortTerm = new ArrayList<>();
        List<String> longTerm = new ArrayList<>();
        
        // Immediate recommendations
        if (conversationMetrics.getAverageFirstResponseTime() > 600) {
            immediate.add("Urgently increase support staff or implement auto-responses");
        }
        
        if (conversationMetrics.getCustomerSatisfactionScore() < 3.0) {
            immediate.add("Immediate review of support processes and agent training");
        }
        
        // Short-term recommendations
        if (conversationMetrics.getAverageFirstResponseTime() > 300) {
            shortTerm.add("Implement response time monitoring and alerts");
        }
        
        if (usageStats.getTotalAutomations() < 5) {
            shortTerm.add("Develop more automation workflows");
        }
        
        // Long-term recommendations
        longTerm.add("Implement advanced analytics dashboard");
        longTerm.add("Develop predictive analytics for capacity planning");
        longTerm.add("Create comprehensive agent training programme");
        
        recommendations.put("immediate", immediate);
        recommendations.put("shortTerm", shortTerm);
        recommendations.put("longTerm", longTerm);
        
        return recommendations;
    }
}
