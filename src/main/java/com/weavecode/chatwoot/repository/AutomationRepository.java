package com.weavecode.chatwoot.repository;

import com.weavecode.chatwoot.entity.Automation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Automation entity
 * Provides data access methods for automation management
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Repository
public interface AutomationRepository extends BaseRepository<Automation, UUID> {

    /**
     * Find automation by tenant ID
     */
    List<Automation> findByTenantId(UUID tenantId);

    /**
     * Find automation by tenant ID and status
     */
    List<Automation> findByTenantIdAndStatus(UUID tenantId, String status);

    /**
     * Find automation by type
     */
    List<Automation> findByType(String type);

    /**
     * Find automation by status
     */
    List<Automation> findByStatus(String status);

    /**
     * Find automation by priority
     */
    List<Automation> findByPriority(String priority);

    /**
     * Find automation by category
     */
    List<Automation> findByCategory(String category);

    /**
     * Find automation by tag
     */
    @Query("SELECT a FROM Automation a WHERE a.tags LIKE %:tag%")
    List<Automation> findByTag(@Param("tag") String tag);

    /**
     * Find automation by AI model
     */
    List<Automation> findByAiModel(String aiModel);

    /**
     * Find automation by AI enabled status
     */
    List<Automation> findByIsAiEnabled(Boolean isAiEnabled);

    /**
     * Find automation by sentiment analysis status
     */
    List<Automation> findBySentimentAnalysis(Boolean sentimentAnalysis);

    /**
     * Find automation by intent detection status
     */
    List<Automation> findByIntentDetection(Boolean intentDetection);

    /**
     * Find automation by entity extraction status
     */
    List<Automation> findByEntityExtraction(Boolean entityExtraction);

    /**
     * Find automation by language detection status
     */
    List<Automation> findByLanguageDetection(Boolean languageDetection);

    /**
     * Find automation by multilingual support status
     */
    List<Automation> findByMultilingualSupport(Boolean multilingualSupport);

    /**
     * Find automation by conversation memory status
     */
    List<Automation> findByConversationMemory(Boolean conversationMemory);

    /**
     * Find automation by business hours only status
     */
    List<Automation> findByBusinessHoursOnly(Boolean businessHoursOnly);

    /**
     * Find automation by timezone
     */
    List<Automation> findByTimezone(String timezone);

    /**
     * Find automation by scheduling enabled status
     */
    List<Automation> findBySchedulingEnabled(Boolean schedulingEnabled);

    /**
     * Find automation by testing enabled status
     */
    List<Automation> findByTestingEnabled(Boolean testingEnabled);

    /**
     * Find automation by approval required status
     */
    List<Automation> findByApprovalRequired(Boolean approvalRequired);

    /**
     * Find automation by rollback enabled status
     */
    List<Automation> findByRollbackEnabled(Boolean rollbackEnabled);

    /**
     * Find automation by archived status
     */
    List<Automation> findByIsArchived(Boolean isArchived);

    /**
     * Find automation by latest version status
     */
    List<Automation> findByIsLatestVersion(Boolean isLatestVersion);

    /**
     * Find automation by version
     */
    List<Automation> findByVersion(String version);

    /**
     * Find automation by approved by user
     */
    List<Automation> findByApprovedBy(UUID approvedBy);

    /**
     * Find automation by deployed by user
     */
    List<Automation> findByDeployedBy(UUID deployedBy);

    /**
     * Find automation by archived by user
     */
    List<Automation> findByArchivedBy(UUID archivedBy);

    /**
     * Find automation by parent version ID
     */
    List<Automation> findByParentVersionId(UUID parentVersionId);

    /**
     * Find automation by rollback version ID
     */
    List<Automation> findByRollbackVersionId(UUID rollbackVersionId);

    /**
     * Find automation by custom field value
     */
    @Query("SELECT a FROM Automation a WHERE a.metadata::text LIKE %:value%")
    List<Automation> findByCustomFieldValue(@Param("value") String value);

    /**
     * Find automation by trigger
     */
    @Query("SELECT a FROM Automation a WHERE a.triggers::text LIKE %:trigger%")
    List<Automation> findByTrigger(@Param("trigger") String trigger);

    /**
     * Find automation by condition
     */
    @Query("SELECT a FROM Automation a WHERE a.conditions::text LIKE %:condition%")
    List<Automation> findByCondition(@Param("condition") String condition);

    /**
     * Find automation by action
     */
    @Query("SELECT a FROM Automation a WHERE a.actions::text LIKE %:action%")
    List<Automation> findByAction(@Param("action") String action);

    /**
     * Find automation by AI configuration
     */
    @Query("SELECT a FROM Automation a WHERE a.aiConfig::text LIKE %:config%")
    List<Automation> findByAiConfig(@Param("config") String config);

    /**
     * Find automation by workflow steps
     */
    @Query("SELECT a FROM Automation a WHERE a.workflowSteps::text LIKE %:step%")
    List<Automation> findByWorkflowStep(@Param("step") String step);

    /**
     * Find automation by response template
     */
    @Query("SELECT a FROM Automation a WHERE a.responseTemplates::text LIKE %:template%")
    List<Automation> findByResponseTemplate(@Param("template") String template);

    /**
     * Find automation by fallback response
     */
    @Query("SELECT a FROM Automation a WHERE a.fallbackResponses::text LIKE %:response%")
    List<Automation> findByFallbackResponse(@Param("response") String response);

    /**
     * Find automation by learning data
     */
    @Query("SELECT a FROM Automation a WHERE a.learningData::text LIKE %:data%")
    List<Automation> findByLearningData(@Param("data") String data);

    /**
     * Find automation by performance metrics
     */
    @Query("SELECT a FROM Automation a WHERE a.performanceMetrics::text LIKE %:metric%")
    List<Automation> findByPerformanceMetric(@Param("metric") String metric);

    /**
     * Find automation by integration webhook
     */
    @Query("SELECT a FROM Automation a WHERE a.integrationWebhooks::text LIKE %:webhook%")
    List<Automation> findByIntegrationWebhook(@Param("webhook") String webhook);

    /**
     * Find automation by API endpoint
     */
    @Query("SELECT a FROM Automation a WHERE a.apiEndpoints::text LIKE %:endpoint%")
    List<Automation> findByApiEndpoint(@Param("endpoint") String endpoint);

    /**
     * Find automation by external service
     */
    @Query("SELECT a FROM Automation a WHERE a.externalServices::text LIKE %:service%")
    List<Automation> findByExternalService(@Param("service") String service);

    /**
     * Find automation by testing configuration
     */
    @Query("SELECT a FROM Automation a WHERE a.testingConfig::text LIKE %:config%")
    List<Automation> findByTestingConfig(@Param("config") String config);

    /**
     * Find automation by business hours configuration
     */
    @Query("SELECT a FROM Automation a WHERE a.businessHours::text LIKE %:hours%")
    List<Automation> findByBusinessHours(@Param("hours") String hours);

    /**
     * Find automation by holiday calendar
     */
    @Query("SELECT a FROM Automation a WHERE a.holidayCalendar::text LIKE %:holiday%")
    List<Automation> findByHolidayCalendar(@Param("holiday") String holiday);

    /**
     * Find automation by scheduling configuration
     */
    @Query("SELECT a FROM Automation a WHERE a.schedulingConfig::text LIKE %:config%")
    List<Automation> findBySchedulingConfig(@Param("config") String config);

    /**
     * Find automation created after specific date
     */
    List<Automation> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find automation created between dates
     */
    List<Automation> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find automation by approved date
     */
    List<Automation> findByApprovedAtAfter(LocalDateTime date);

    /**
     * Find automation by deployment date
     */
    List<Automation> findByDeploymentDateAfter(LocalDateTime date);

    /**
     * Find automation by last rollback date
     */
    List<Automation> findByLastRollbackDateAfter(LocalDateTime date);

    /**
     * Find automation by archive date
     */
    List<Automation> findByArchivedAtAfter(LocalDateTime date);

    /**
     * Find automation by AI temperature range
     */
    @Query("SELECT a FROM Automation a WHERE a.aiTemperature BETWEEN :minTemp AND :maxTemp")
    List<Automation> findByAiTemperatureBetween(@Param("minTemp") Double minTemp, @Param("maxTemp") Double maxTemp);

    /**
     * Find automation by AI max tokens range
     */
    @Query("SELECT a FROM Automation a WHERE a.aiMaxTokens BETWEEN :minTokens AND :maxTokens")
    List<Automation> findByAiMaxTokensBetween(@Param("minTokens") Integer minTokens, @Param("maxTokens") Integer maxTokens);

    /**
     * Find automation by AI context window range
     */
    @Query("SELECT a FROM Automation a WHERE a.aiContextWindow BETWEEN :minWindow AND :maxWindow")
    List<Automation> findByAiContextWindowBetween(@Param("minWindow") Integer minWindow, @Param("maxWindow") Integer maxWindow);

    /**
     * Find automation by memory duration hours range
     */
    @Query("SELECT a FROM Automation a WHERE a.memoryDurationHours BETWEEN :minHours AND :maxHours")
    List<Automation> findByMemoryDurationHoursBetween(@Param("minHours") Integer minHours, @Param("maxHours") Integer maxHours);

    /**
     * Find automation by max conversation length range
     */
    @Query("SELECT a FROM Automation a WHERE a.maxConversationLength BETWEEN :minLength AND :maxLength")
    List<Automation> findByMaxConversationLengthBetween(@Param("minLength") Integer minLength, @Param("maxLength") Integer maxLength);

    /**
     * Find automation by escalation threshold range
     */
    @Query("SELECT a FROM Automation a WHERE a.escalationThreshold BETWEEN :minThreshold AND :maxThreshold")
    List<Automation> findByEscalationThresholdBetween(@Param("minThreshold") Integer minThreshold, @Param("maxThreshold") Integer maxThreshold);

    /**
     * Find automation by grace period days range
     */
    @Query("SELECT a FROM Automation a WHERE a.gracePeriodDays BETWEEN :minDays AND :maxDays")
    List<Automation> findByGracePeriodDaysBetween(@Param("minDays") Integer minDays, @Param("maxDays") Integer maxDays);

    /**
     * Count automation by tenant ID
     */
    long countByTenantId(UUID tenantId);

    /**
     * Count automation by type
     */
    long countByType(String type);

    /**
     * Count automation by status
     */
    long countByStatus(String status);

    /**
     * Count automation by priority
     */
    long countByPriority(String priority);

    /**
     * Count automation by category
     */
    long countByCategory(String category);

    /**
     * Count automation by AI model
     */
    long countByAiModel(String aiModel);

    /**
     * Count automation by AI enabled status
     */
    long countByIsAiEnabled(Boolean isAiEnabled);

    /**
     * Count automation by sentiment analysis status
     */
    long countBySentimentAnalysis(Boolean sentimentAnalysis);

    /**
     * Count automation by intent detection status
     */
    long countByIntentDetection(Boolean intentDetection);

    /**
     * Count automation by entity extraction status
     */
    long countByEntityExtraction(Boolean entityExtraction);

    /**
     * Count automation by language detection status
     */
    long countByLanguageDetection(Boolean languageDetection);

    /**
     * Count automation by multilingual support status
     */
    long countByMultilingualSupport(Boolean multilingualSupport);

    /**
     * Count automation by conversation memory status
     */
    long countByConversationMemory(Boolean conversationMemory);

    /**
     * Count automation by business hours only status
     */
    long countByBusinessHoursOnly(Boolean businessHoursOnly);

    /**
     * Count automation by timezone
     */
    long countByTimezone(String timezone);

    /**
     * Count automation by scheduling enabled status
     */
    long countBySchedulingEnabled(Boolean schedulingEnabled);

    /**
     * Count automation by testing enabled status
     */
    long countByTestingEnabled(Boolean testingEnabled);

    /**
     * Count automation by approval required status
     */
    long countByApprovalRequired(Boolean approvalRequired);

    /**
     * Count automation by rollback enabled status
     */
    long countByRollbackEnabled(Boolean rollbackEnabled);

    /**
     * Count automation by archived status
     */
    long countByIsArchived(Boolean isArchived);

    /**
     * Count automation by latest version status
     */
    long countByIsLatestVersion(Boolean isLatestVersion);

    /**
     * Count automation created in specific month
     */
    @Query("SELECT COUNT(a) FROM Automation a WHERE YEAR(a.createdAt) = :year AND MONTH(a.createdAt) = :month")
    long countByCreatedYearAndMonth(@Param("year") int year, @Param("month") int month);

    /**
     * Count automation by tenant ID and status
     */
    long countByTenantIdAndStatus(UUID tenantId, String status);

    /**
     * Count automation by tenant ID and type
     */
    long countByTenantIdAndType(UUID tenantId, String type);

    /**
     * Count automation by tenant ID and priority
     */
    long countByTenantIdAndPriority(UUID tenantId, String priority);

    /**
     * Find automation with pagination
     */
    Page<Automation> findAll(Pageable pageable);

    /**
     * Find automation by tenant ID with pagination
     */
    Page<Automation> findByTenantId(UUID tenantId, Pageable pageable);

    /**
     * Find automation by type with pagination
     */
    Page<Automation> findByType(String type, Pageable pageable);

    /**
     * Find automation by status with pagination
     */
    Page<Automation> findByStatus(String status, Pageable pageable);

    /**
     * Find automation by priority with pagination
     */
    Page<Automation> findByPriority(String priority, Pageable pageable);

    /**
     * Find automation by category with pagination
     */
    Page<Automation> findByCategory(String category, Pageable pageable);

    /**
     * Find automation by AI model with pagination
     */
    Page<Automation> findByAiModel(String aiModel, Pageable pageable);

    /**
     * Find automation by AI enabled status with pagination
     */
    Page<Automation> findByIsAiEnabled(Boolean isAiEnabled, Pageable pageable);

    /**
     * Find automation by sentiment analysis status with pagination
     */
    Page<Automation> findBySentimentAnalysis(Boolean sentimentAnalysis, Pageable pageable);

    /**
     * Find automation by intent detection status with pagination
     */
    Page<Automation> findByIntentDetection(Boolean intentDetection, Pageable pageable);

    /**
     * Find automation by entity extraction status with pagination
     */
    Page<Automation> findByEntityExtraction(Boolean entityExtraction, Pageable pageable);

    /**
     * Find automation by language detection status with pagination
     */
    Page<Automation> findByLanguageDetection(Boolean languageDetection, Pageable pageable);

    /**
     * Find automation by multilingual support status with pagination
     */
    Page<Automation> findByMultilingualSupport(Boolean multilingualSupport, Pageable pageable);

    /**
     * Find automation by conversation memory status with pagination
     */
    Page<Automation> findByConversationMemory(Boolean conversationMemory, Pageable pageable);

    /**
     * Find automation by business hours only status with pagination
     */
    Page<Automation> findByBusinessHoursOnly(Boolean businessHoursOnly, Pageable pageable);

    /**
     * Find automation by timezone with pagination
     */
    Page<Automation> findByTimezone(String timezone, Pageable pageable);

    /**
     * Find automation by scheduling enabled status with pagination
     */
    Page<Automation> findBySchedulingEnabled(Boolean schedulingEnabled, Pageable pageable);

    /**
     * Find automation by testing enabled status with pagination
     */
    Page<Automation> findByTestingEnabled(Boolean testingEnabled, Pageable pageable);

    /**
     * Find automation by approval required status with pagination
     */
    Page<Automation> findByApprovalRequired(Boolean approvalRequired, Pageable pageable);

    /**
     * Find automation by rollback enabled status with pagination
     */
    Page<Automation> findByRollbackEnabled(Boolean rollbackEnabled, Pageable pageable);

    /**
     * Find automation by archived status with pagination
     */
    Page<Automation> findByIsArchived(Boolean isArchived, Pageable pageable);

    /**
     * Find automation by latest version status with pagination
     */
    Page<Automation> findByIsLatestVersion(Boolean isLatestVersion, Pageable pageable);

    /**
     * Search automation by name containing text
     */
    Page<Automation> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Search automation by description containing text
     */
    Page<Automation> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);

    /**
     * Find automation by multiple criteria
     */
    @Query("SELECT a FROM Automation a WHERE " +
           "(:tenantId IS NULL OR a.tenantId = :tenantId) AND " +
           "(:type IS NULL OR a.type = :type) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:priority IS NULL OR a.priority = :priority)")
    Page<Automation> findByMultipleCriteria(
            @Param("tenantId") UUID tenantId,
            @Param("type") String type,
            @Param("status") String status,
            @Param("priority") String priority,
            Pageable pageable
    );

    /**
     * Find automation with expired trial
     */
    @Query("SELECT a FROM Automation a WHERE a.trialEndDate IS NOT NULL AND a.trialEndDate < :currentDate")
    List<Automation> findAutomationsWithExpiredTrial(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find automation approaching trial expiry
     */
    @Query("SELECT a FROM Automation a WHERE a.trialEndDate IS NOT NULL AND a.trialEndDate BETWEEN :startDate AND :endDate")
    List<Automation> findAutomationsApproachingTrialExpiry(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find automation with expired grace period
     */
    @Query("SELECT a FROM Automation a WHERE a.gracePeriodEnd IS NOT NULL AND a.gracePeriodEnd < :currentDate")
    List<Automation> findAutomationsWithExpiredGracePeriod(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find automation approaching grace period expiry
     */
    @Query("SELECT a FROM Automation a WHERE a.gracePeriodEnd IS NOT NULL AND a.gracePeriodEnd BETWEEN :startDate AND :endDate")
    List<Automation> findAutomationsApproachingGracePeriodExpiry(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find automation with expired next billing date
     */
    @Query("SELECT a FROM Automation a WHERE a.nextBillingDate IS NOT NULL AND a.nextBillingDate < :currentDate")
    List<Automation> findAutomationsWithExpiredNextBillingDate(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find automation approaching next billing date
     */
    @Query("SELECT a FROM Automation a WHERE a.nextBillingDate IS NOT NULL AND a.nextBillingDate BETWEEN :startDate AND :endDate")
    List<Automation> findAutomationsApproachingNextBillingDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find automation with max failed payments
     */
    @Query("SELECT a FROM Automation a WHERE a.failedPaymentCount >= a.maxFailedPayments")
    List<Automation> findAutomationsWithMaxFailedPayments();

    /**
     * Find automation that can be retried
     */
    @Query("SELECT a FROM Automation a WHERE a.failedPaymentCount < a.maxFailedPayments")
    List<Automation> findAutomationsThatCanBeRetried();

    /**
     * Find automation by custom field
     */
    @Query("SELECT a FROM Automation a WHERE a.metadata::text LIKE %:fieldName%")
    List<Automation> findByCustomField(@Param("fieldName") String fieldName);

    /**
     * Find automation by audit trail
     */
    @Query("SELECT a FROM Automation a WHERE a.metadata::text LIKE %:auditEvent%")
    List<Automation> findByAuditEvent(@Param("auditEvent") String auditEvent);
}
