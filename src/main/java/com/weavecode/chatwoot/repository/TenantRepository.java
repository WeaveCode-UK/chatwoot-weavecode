package com.weavecode.chatwoot.repository;

import com.weavecode.chatwoot.entity.Tenant;
import com.weavecode.chatwoot.enums.PlanType;
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
 * Repository interface for Tenant entity
 * Provides data access methods for multi-tenant operations
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Repository
public interface TenantRepository extends BaseRepository<Tenant, UUID> {

    /**
     * Find tenant by company name (case-insensitive)
     */
    Optional<Tenant> findByCompanyNameIgnoreCase(String companyName);

    /**
     * Find tenant by domain (case-insensitive)
     */
    Optional<Tenant> findByDomainIgnoreCase(String domain);

    /**
     * Find tenant by email (case-insensitive)
     */
    Optional<Tenant> findByContactEmailIgnoreCase(String email);

    /**
     * Find tenants by plan type
     */
    List<Tenant> findByPlanType(PlanType planType);

    /**
     * Find tenants by status
     */
    List<Tenant> findByStatus(String status);

    /**
     * Find active tenants
     */
    List<Tenant> findByStatusAndIsArchivedFalse(String status);

    /**
     * Find tenants by country
     */
    List<Tenant> findByCountryIgnoreCase(String country);

    /**
     * Find tenants by timezone
     */
    List<Tenant> findByTimezone(String timezone);

    /**
     * Find tenants created after specific date
     */
    List<Tenant> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find tenants created between dates
     */
    List<Tenant> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find tenants by subscription status
     */
    @Query("SELECT t FROM Tenant t WHERE t.subscriptionStatus = :status")
    List<Tenant> findBySubscriptionStatus(@Param("status") String status);

    /**
     * Find tenants with trial expiring soon
     */
    @Query("SELECT t FROM Tenant t WHERE t.trialEndDate IS NOT NULL AND t.trialEndDate BETWEEN :startDate AND :endDate")
    List<Tenant> findTenantsWithTrialExpiringBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find tenants by usage limits
     */
    @Query("SELECT t FROM Tenant t WHERE t.currentUsage >= t.usageLimit * 0.8")
    List<Tenant> findTenantsNearUsageLimit();

    /**
     * Find tenants by custom field value
     */
    @Query("SELECT t FROM Tenant t WHERE t.customFields::text LIKE %:value%")
    List<Tenant> findByCustomFieldValue(@Param("value") String value);

    /**
     * Count tenants by plan type
     */
    long countByPlanType(PlanType planType);

    /**
     * Count tenants by status
     */
    long countByStatus(String status);

    /**
     * Count tenants by country
     */
    long countByCountryIgnoreCase(String country);

    /**
     * Count tenants created in specific month
     */
    @Query("SELECT COUNT(t) FROM Tenant t WHERE YEAR(t.createdAt) = :year AND MONTH(t.createdAt) = :month")
    long countByCreatedYearAndMonth(@Param("year") int year, @Param("month") int month);

    /**
     * Find tenants with pagination
     */
    Page<Tenant> findAll(Pageable pageable);

    /**
     * Find tenants by status with pagination
     */
    Page<Tenant> findByStatus(String status, Pageable pageable);

    /**
     * Find tenants by plan type with pagination
     */
    Page<Tenant> findByPlanType(PlanType planType, Pageable pageable);

    /**
     * Search tenants by company name containing text
     */
    Page<Tenant> findByCompanyNameContainingIgnoreCase(String companyName, Pageable pageable);

    /**
     * Find tenants by multiple criteria
     */
    @Query("SELECT t FROM Tenant t WHERE " +
           "(:companyName IS NULL OR t.companyName LIKE %:companyName%) AND " +
           "(:planType IS NULL OR t.planType = :planType) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:country IS NULL OR t.country = :country)")
    Page<Tenant> findByMultipleCriteria(
            @Param("companyName") String companyName,
            @Param("planType") PlanType planType,
            @Param("status") String status,
            @Param("country") String country,
            Pageable pageable
    );

    /**
     * Find tenants with expired subscriptions
     */
    @Query("SELECT t FROM Tenant t WHERE t.subscriptionEndDate IS NOT NULL AND t.subscriptionEndDate < :currentDate")
    List<Tenant> findTenantsWithExpiredSubscriptions(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find tenants with active subscriptions
     */
    @Query("SELECT t FROM Tenant t WHERE t.subscriptionStatus = 'active' AND (t.subscriptionEndDate IS NULL OR t.subscriptionEndDate > :currentDate)")
    List<Tenant> findTenantsWithActiveSubscriptions(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find tenants by custom tag
     */
    @Query("SELECT t FROM Tenant t WHERE t.tags LIKE %:tag%")
    List<Tenant> findByTag(@Param("tag") String tag);

    /**
     * Find tenants by industry
     */
    List<Tenant> findByIndustryIgnoreCase(String industry);

    /**
     * Find tenants by company size
     */
    List<Tenant> findByCompanySize(String companySize);

    /**
     * Find tenants by subscription amount range
     */
    @Query("SELECT t FROM Tenant t WHERE t.subscriptionAmount BETWEEN :minAmount AND :maxAmount")
    List<Tenant> findBySubscriptionAmountBetween(@Param("minAmount") Double minAmount, @Param("maxAmount") Double maxAmount);

    /**
     * Find tenants by last activity
     */
    @Query("SELECT t FROM Tenant t WHERE t.lastActivityAt < :date")
    List<Tenant> findTenantsInactiveSince(@Param("date") LocalDateTime date);

    /**
     * Find tenants by custom domain
     */
    @Query("SELECT t FROM Tenant t WHERE t.customDomains::text LIKE %:domain%")
    List<Tenant> findByCustomDomain(@Param("domain") String domain);

    /**
     * Find tenants by integration type
     */
    @Query("SELECT t FROM Tenant t WHERE t.integrations::text LIKE %:integrationType%")
    List<Tenant> findByIntegrationType(@Param("integrationType") String integrationType);

    /**
     * Find tenants by feature flag
     */
    @Query("SELECT t FROM Tenant t WHERE t.featureFlags::text LIKE %:feature%")
    List<Tenant> findByFeatureFlag(@Param("feature") String feature);

    /**
     * Find tenants by compliance status
     */
    List<Tenant> findByGdprCompliantAndLgpdCompliant(Boolean gdprCompliant, Boolean lgpdCompliant);

    /**
     * Find tenants by data retention policy
     */
    @Query("SELECT t FROM Tenant t WHERE t.dataRetentionDays = :retentionDays")
    List<Tenant> findByDataRetentionDays(@Param("retentionDays") Integer retentionDays);

    /**
     * Find tenants by backup frequency
     */
    List<Tenant> findByBackupFrequency(String backupFrequency);

    /**
     * Find tenants by monitoring level
     */
    List<Tenant> findByMonitoringLevel(String monitoringLevel);

    /**
     * Find tenants by support tier
     */
    List<Tenant> findBySupportTier(String supportTier);

    /**
     * Find tenants by SLA level
     */
    List<Tenant> findBySlaLevel(String slaLevel);

    /**
     * Find tenants by custom branding
     */
    @Query("SELECT t FROM Tenant t WHERE t.branding::text LIKE %:brandingElement%")
    List<Tenant> findByBrandingElement(@Param("brandingElement") String brandingElement);

    /**
     * Find tenants by webhook configuration
     */
    @Query("SELECT t FROM Tenant t WHERE t.webhooks::text LIKE %:webhookType%")
    List<Tenant> findByWebhookType(@Param("webhookType") String webhookType);

    /**
     * Find tenants by API usage
     */
    @Query("SELECT t FROM Tenant t WHERE t.apiUsageCount > :minUsage")
    List<Tenant> findByMinApiUsage(@Param("minUsage") Integer minUsage);

    /**
     * Find tenants by storage usage
     */
    @Query("SELECT t FROM Tenant t WHERE t.storageUsage > :minStorage")
    List<Tenant> findByMinStorageUsage(@Param("minStorage") Long minStorage);

    /**
     * Find tenants by concurrent user limit
     */
    @Query("SELECT t FROM Tenant t WHERE t.concurrentUserLimit > :minLimit")
    List<Tenant> findByMinConcurrentUserLimit(@Param("minLimit") Integer minLimit);

    /**
     * Find tenants by custom field
     */
    @Query("SELECT t FROM Tenant t WHERE t.customFields::text LIKE %:fieldName%")
    List<Tenant> findByCustomField(@Param("fieldName") String fieldName);

    /**
     * Find tenants by metadata
     */
    @Query("SELECT t FROM Tenant t WHERE t.metadata::text LIKE %:metadataValue%")
    List<Tenant> findByMetadata(@Param("metadataValue") String metadataValue);

    /**
     * Find tenants by audit trail
     */
    @Query("SELECT t FROM Tenant t WHERE t.auditTrail::text LIKE %:auditEvent%")
    List<Tenant> findByAuditEvent(@Param("auditEvent") String auditEvent);
}
