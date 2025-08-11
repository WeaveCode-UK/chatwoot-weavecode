package com.weavecode.chatwoot.repository;

import com.weavecode.chatwoot.entity.Conversation;
import com.weavecode.chatwoot.enums.ConversationStatus;
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
 * Repository interface for Conversation entity
 * Provides data access methods for conversation management
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Repository
public interface ConversationRepository extends BaseRepository<Conversation, UUID> {

    /**
     * Find conversations by tenant ID
     */
    List<Conversation> findByTenantId(UUID tenantId);

    /**
     * Find conversations by customer ID
     */
    List<Conversation> findByCustomerId(UUID customerId);

    /**
     * Find conversations by assigned agent ID
     */
    List<Conversation> findByAssignedAgentId(UUID assignedAgentId);

    /**
     * Find conversations by status
     */
    List<Conversation> findByStatus(ConversationStatus status);

    /**
     * Find conversations by tenant ID and status
     */
    List<Conversation> findByTenantIdAndStatus(UUID tenantId, ConversationStatus status);

    /**
     * Find conversations by priority
     */
    List<Conversation> findByPriority(String priority);

    /**
     * Find conversations by source
     */
    List<Conversation> findBySource(String source);

    /**
     * Find conversations by channel
     */
    List<Conversation> findByChannel(String channel);

    /**
     * Find conversations by tag
     */
    @Query("SELECT c FROM Conversation c WHERE c.tags LIKE %:tag%")
    List<Conversation> findByTag(@Param("tag") String tag);

    /**
     * Find conversations by SLA level
     */
    List<Conversation> findBySlaLevel(String slaLevel);

    /**
     * Find conversations created after specific date
     */
    List<Conversation> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find conversations created between dates
     */
    List<Conversation> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find conversations by last message date
     */
    List<Conversation> findByLastMessageAtAfter(LocalDateTime date);

    /**
     * Find conversations by resolution date
     */
    List<Conversation> findByResolvedAtAfter(LocalDateTime date);

    /**
     * Find conversations by customer satisfaction score
     */
    @Query("SELECT c FROM Conversation c WHERE c.customerSatisfactionScore >= :minScore")
    List<Conversation> findByMinCustomerSatisfactionScore(@Param("minScore") Double minScore);

    /**
     * Find conversations by response time
     */
    @Query("SELECT c FROM Conversation c WHERE c.averageResponseTime <= :maxResponseTime")
    List<Conversation> findByMaxResponseTime(@Param("maxResponseTime") Long maxResponseTime);

    /**
     * Find conversations by message count
     */
    @Query("SELECT c FROM Conversation c WHERE c.messageCount >= :minMessages")
    List<Conversation> findByMinMessageCount(@Param("minMessages") Integer minMessages);

    /**
     * Find conversations by duration
     */
    @Query("SELECT c FROM Conversation c WHERE c.duration >= :minDuration")
    List<Conversation> findByMinDuration(@Param("minDuration") Long minDuration);

    /**
     * Find conversations by custom field value
     */
    @Query("SELECT c FROM Conversation c WHERE c.customFields::text LIKE %:value%")
    List<Conversation> findByCustomFieldValue(@Param("value") String value);

    /**
     * Find conversations by metadata
     */
    @Query("SELECT c FROM Conversation c WHERE c.metadata::text LIKE %:metadataValue%")
    List<Conversation> findByMetadata(@Param("metadataValue") String metadataValue);

    /**
     * Count conversations by tenant ID
     */
    long countByTenantId(UUID tenantId);

    /**
     * Count conversations by status
     */
    long countByStatus(ConversationStatus status);

    /**
     * Count conversations by tenant ID and status
     */
    long countByTenantIdAndStatus(UUID tenantId, ConversationStatus status);

    /**
     * Count conversations by priority
     */
    long countByPriority(String priority);

    /**
     * Count conversations by source
     */
    long countBySource(String source);

    /**
     * Count conversations by channel
     */
    long countByChannel(String channel);

    /**
     * Count conversations created in specific month
     */
    @Query("SELECT COUNT(c) FROM Conversation c WHERE YEAR(c.createdAt) = :year AND MONTH(c.createdAt) = :month")
    long countByCreatedYearAndMonth(@Param("year") int year, @Param("month") int month);

    /**
     * Find conversations with pagination
     */
    Page<Conversation> findAll(Pageable pageable);

    /**
     * Find conversations by tenant ID with pagination
     */
    Page<Conversation> findByTenantId(UUID tenantId, Pageable pageable);

    /**
     * Find conversations by status with pagination
     */
    Page<Conversation> findByStatus(ConversationStatus status, Pageable pageable);

    /**
     * Find conversations by tenant ID and status with pagination
     */
    Page<Conversation> findByTenantIdAndStatus(UUID tenantId, ConversationStatus status, Pageable pageable);

    /**
     * Search conversations by subject containing text
     */
    Page<Conversation> findBySubjectContainingIgnoreCase(String subject, Pageable pageable);

    /**
     * Find conversations by multiple criteria
     */
    @Query("SELECT c FROM Conversation c WHERE " +
           "(:tenantId IS NULL OR c.tenantId = :tenantId) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:priority IS NULL OR c.priority = :priority) AND " +
           "(:source IS NULL OR c.source = :source)")
    Page<Conversation> findByMultipleCriteria(
            @Param("tenantId") UUID tenantId,
            @Param("status") ConversationStatus status,
            @Param("priority") String priority,
            @Param("source") String source,
            Pageable pageable
    );

    /**
     * Find conversations with expired SLA
     */
    @Query("SELECT c FROM Conversation c WHERE c.slaDeadline IS NOT NULL AND c.slaDeadline < :currentDate")
    List<Conversation> findConversationsWithExpiredSLA(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find conversations approaching SLA deadline
     */
    @Query("SELECT c FROM Conversation c WHERE c.slaDeadline IS NOT NULL AND c.slaDeadline BETWEEN :startDate AND :endDate")
    List<Conversation> findConversationsApproachingSLADeadline(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find conversations by escalation level
     */
    List<Conversation> findByEscalationLevel(String escalationLevel);

    /**
     * Find conversations by escalation reason
     */
    @Query("SELECT c FROM Conversation c WHERE c.escalationReason LIKE %:reason%")
    List<Conversation> findByEscalationReason(@Param("reason") String reason);

    /**
     * Find conversations by category
     */
    List<Conversation> findByCategory(String category);

    /**
     * Find conversations by subcategory
     */
    List<Conversation> findBySubcategory(String subcategory);

    /**
     * Find conversations by product
     */
    List<Conversation> findByProduct(String product);

    /**
     * Find conversations by order ID
     */
    Optional<Conversation> findByOrderId(String orderId);

    /**
     * Find conversations by invoice ID
     */
    Optional<Conversation> findByInvoiceId(String invoiceId);

    /**
     * Find conversations by reference number
     */
    Optional<Conversation> findByReferenceNumber(String referenceNumber);

    /**
     * Find conversations by external ID
     */
    Optional<Conversation> findByExternalId(String externalId);

    /**
     * Find conversations by integration ID
     */
    Optional<Conversation> findByIntegrationId(String integrationId);

    /**
     * Find conversations by campaign ID
     */
    Optional<Conversation> findByCampaignId(String campaignId);

    /**
     * Find conversations by lead ID
     */
    Optional<Conversation> findByLeadId(String leadId);

    /**
     * Find conversations by opportunity ID
     */
    Optional<Conversation> findByOpportunityId(String opportunityId);

    /**
     * Find conversations by case ID
     */
    Optional<Conversation> findByCaseId(String caseId);

    /**
     * Find conversations by ticket ID
     */
    Optional<Conversation> findByTicketId(String ticketId);

    /**
     * Find conversations by project ID
     */
    Optional<Conversation> findByProjectId(String projectId);

    /**
     * Find conversations by task ID
     */
    Optional<Conversation> findByTaskId(String taskId);

    /**
     * Find conversations by milestone ID
     */
    Optional<Conversation> findByMilestoneId(String milestoneId);

    /**
     * Find conversations by release ID
     */
    Optional<Conversation> findByReleaseId(String releaseId);

    /**
     * Find conversations by version ID
     */
    Optional<Conversation> findByVersionId(String versionId);

    /**
     * Find conversations by build ID
     */
    Optional<Conversation> findByBuildId(String buildId);

    /**
     * Find conversations by deployment ID
     */
    Optional<Conversation> findByDeploymentId(String deploymentId);

    /**
     * Find conversations by environment
     */
    List<Conversation> findByEnvironment(String environment);

    /**
     * Find conversations by platform
     */
    List<Conversation> findByPlatform(String platform);

    /**
     * Find conversations by browser
     */
    List<Conversation> findByBrowser(String browser);

    /**
     * Find conversations by operating system
     */
    List<Conversation> findByOperatingSystem(String operatingSystem);

    /**
     * Find conversations by device type
     */
    List<Conversation> findByDeviceType(String deviceType);

    /**
     * Find conversations by user agent
     */
    @Query("SELECT c FROM Conversation c WHERE c.userAgent LIKE %:userAgent%")
    List<Conversation> findByUserAgent(@Param("userAgent") String userAgent);

    /**
     * Find conversations by IP address
     */
    List<Conversation> findByIpAddress(String ipAddress);

    /**
     * Find conversations by location
     */
    List<Conversation> findByLocation(String location);

    /**
     * Find conversations by timezone
     */
    List<Conversation> findByTimezone(String timezone);

    /**
     * Find conversations by language
     */
    List<Conversation> findByLanguage(String language);

    /**
     * Find conversations by currency
     */
    List<Conversation> findByCurrency(String currency);

    /**
     * Find conversations by country
     */
    List<Conversation> findByCountry(String country);

    /**
     * Find conversations by region
     */
    List<Conversation> findByRegion(String region);

    /**
     * Find conversations by city
     */
    List<Conversation> findByCity(String city);

    /**
     * Find conversations by postal code
     */
    List<Conversation> findByPostalCode(String postalCode);

    /**
     * Find conversations by custom field
     */
    @Query("SELECT c FROM Conversation c WHERE c.customFields::text LIKE %:fieldName%")
    List<Conversation> findByCustomField(@Param("fieldName") String fieldName);

    /**
     * Find conversations by audit trail
     */
    @Query("SELECT c FROM Conversation c WHERE c.auditTrail::text LIKE %:auditEvent%")
    List<Conversation> findByAuditEvent(@Param("auditEvent") String auditEvent);
}
