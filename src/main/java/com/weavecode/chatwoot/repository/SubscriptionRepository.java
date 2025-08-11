package com.weavecode.chatwoot.repository;

import com.weavecode.chatwoot.entity.Subscription;
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
 * Repository interface for Subscription entity
 * Provides data access methods for subscription management
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Repository
public interface SubscriptionRepository extends BaseRepository<Subscription, UUID> {

    /**
     * Find subscription by tenant ID
     */
    List<Subscription> findByTenantId(UUID tenantId);

    /**
     * Find subscription by tenant ID and status
     */
    List<Subscription> findByTenantIdAndStatus(UUID tenantId, String status);

    /**
     * Find subscription by plan type
     */
    List<Subscription> findByPlanType(PlanType planType);

    /**
     * Find subscription by plan name
     */
    List<Subscription> findByPlanNameIgnoreCase(String planName);

    /**
     * Find subscription by billing cycle
     */
    List<Subscription> findByBillingCycle(String billingCycle);

    /**
     * Find subscription by currency
     */
    List<Subscription> findByCurrency(String currency);

    /**
     * Find subscription by status
     */
    List<Subscription> findByStatus(String status);

    /**
     * Find subscription by payment status
     */
    List<Subscription> findByPaymentStatus(String paymentStatus);

    /**
     * Find subscription by payment provider
     */
    List<Subscription> findByPaymentProvider(String paymentProvider);

    /**
     * Find subscription by payment method ID
     */
    List<Subscription> findByPaymentMethodId(String paymentMethodId);

    /**
     * Find subscription by invoice ID
     */
    Optional<Subscription> findByInvoiceId(String invoiceId);

    /**
     * Find subscription by tag
     */
    @Query("SELECT s FROM Subscription s WHERE s.tags LIKE %:tag%")
    List<Subscription> findByTag(@Param("tag") String tag);

    /**
     * Find subscription by custom field value
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:value%")
    List<Subscription> findByCustomFieldValue(@Param("value") String value);

    /**
     * Find subscription by feature
     */
    @Query("SELECT s FROM Subscription s WHERE s.features::text LIKE %:feature%")
    List<Subscription> findByFeature(@Param("feature") String feature);

    /**
     * Find subscription by limit
     */
    @Query("SELECT s FROM Subscription s WHERE s.limits::text LIKE %:limit%")
    List<Subscription> findByLimit(@Param("limit") String limit);

    /**
     * Find subscription created after specific date
     */
    List<Subscription> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find subscription created between dates
     */
    List<Subscription> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find subscription by start date
     */
    List<Subscription> findByStartDateAfter(LocalDateTime date);

    /**
     * Find subscription by end date
     */
    List<Subscription> findByEndDateAfter(LocalDateTime date);

    /**
     * Find subscription by trial start date
     */
    List<Subscription> findByTrialStartDateAfter(LocalDateTime date);

    /**
     * Find subscription by trial end date
     */
    List<Subscription> findByTrialEndDateAfter(LocalDateTime date);

    /**
     * Find subscription by next billing date
     */
    List<Subscription> findByNextBillingDateAfter(LocalDateTime date);

    /**
     * Find subscription by last billing date
     */
    List<Subscription> findByLastBillingDateAfter(LocalDateTime date);

    /**
     * Find subscription by last payment date
     */
    List<Subscription> findByLastPaymentDateAfter(LocalDateTime date);

    /**
     * Find subscription by last failed payment date
     */
    List<Subscription> findByLastFailedPaymentDateAfter(LocalDateTime date);

    /**
     * Find subscription by refund date
     */
    List<Subscription> findByRefundDateAfter(LocalDateTime date);

    /**
     * Find subscription by deployment date
     */
    List<Subscription> findByDeploymentDateAfter(LocalDateTime date);

    /**
     * Find subscription by archive date
     */
    List<Subscription> findByArchivedAtAfter(LocalDateTime date);

    /**
     * Find subscription by amount range
     */
    @Query("SELECT s FROM Subscription s WHERE s.amount BETWEEN :minAmount AND :maxAmount")
    List<Subscription> findByAmountBetween(@Param("minAmount") Double minAmount, @Param("maxAmount") Double maxAmount);

    /**
     * Find subscription by total amount range
     */
    @Query("SELECT s FROM Subscription s WHERE s.totalAmount BETWEEN :minAmount AND :maxAmount")
    List<Subscription> findByTotalAmountBetween(@Param("minAmount") Double minAmount, @Param("maxAmount") Double maxAmount);

    /**
     * Find subscription by discount percentage range
     */
    @Query("SELECT s FROM Subscription s WHERE s.discountPercentage BETWEEN :minPercentage AND :maxPercentage")
    List<Subscription> findByDiscountPercentageBetween(@Param("minPercentage") Double minPercentage, @Param("maxPercentage") Double maxPercentage);

    /**
     * Find subscription by tax percentage range
     */
    @Query("SELECT s FROM Subscription s WHERE s.taxPercentage BETWEEN :minPercentage AND :maxPercentage")
    List<Subscription> findByTaxPercentageBetween(@Param("minPercentage") Double minPercentage, @Param("maxPercentage") Double maxPercentage);

    /**
     * Find subscription by trial days range
     */
    @Query("SELECT s FROM Subscription s WHERE s.trialDays BETWEEN :minDays AND :maxDays")
    List<Subscription> findByTrialDaysBetween(@Param("minDays") Integer minDays, @Param("maxDays") Integer maxDays);

    /**
     * Find subscription by grace period days range
     */
    @Query("SELECT s FROM Subscription s WHERE s.gracePeriodDays BETWEEN :minDays AND :maxDays")
    List<Subscription> findByGracePeriodDaysBetween(@Param("minDays") Integer minDays, @Param("maxDays") Integer maxDays);

    /**
     * Find subscription by failed payment count range
     */
    @Query("SELECT s FROM Subscription s WHERE s.failedPaymentCount BETWEEN :minCount AND :maxCount")
    List<Subscription> findByFailedPaymentCountBetween(@Param("minCount") Integer minCount, @Param("maxCount") Integer maxCount);

    /**
     * Find subscription by max failed payments range
     */
    @Query("SELECT s FROM Subscription s WHERE s.maxFailedPayments BETWEEN :minCount AND :maxCount")
    List<Subscription> findByMaxFailedPaymentsBetween(@Param("minCount") Integer minCount, @Param("maxCount") Integer maxCount);

    /**
     * Find subscription by auto-renewal status
     */
    List<Subscription> findByAutoRenew(Boolean autoRenew);

    /**
     * Find subscription by approval required status
     */
    List<Subscription> findByApprovalRequired(Boolean approvalRequired);

    /**
     * Find subscription by rollback enabled status
     */
    List<Subscription> findByRollbackEnabled(Boolean rollbackEnabled);

    /**
     * Find subscription by archived status
     */
    List<Subscription> findByIsArchived(Boolean isArchived);

    /**
     * Find subscription by latest version status
     */
    List<Subscription> findByIsLatestVersion(Boolean isLatestVersion);

    /**
     * Find subscription by approved by user
     */
    List<Subscription> findByApprovedBy(UUID approvedBy);

    /**
     * Find subscription by deployed by user
     */
    List<Subscription> findByDeployedBy(UUID deployedBy);

    /**
     * Find subscription by archived by user
     */
    List<Subscription> findByArchivedBy(UUID archivedBy);

    /**
     * Find subscription by cancelled by user
     */
    List<Subscription> findByCancelledBy(UUID cancelledBy);

    /**
     * Find subscription by parent version ID
     */
    List<Subscription> findByParentVersionId(UUID parentVersionId);

    /**
     * Find subscription by rollback version ID
     */
    List<Subscription> findByRollbackVersionId(UUID rollbackVersionId);

    /**
     * Find subscription by version
     */
    List<Subscription> findByVersion(String version);

    /**
     * Find subscription by category
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:category%")
    List<Subscription> findByCategory(@Param("category") String category);

    /**
     * Find subscription by industry
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:industry%")
    List<Subscription> findByIndustry(@Param("industry") String industry);

    /**
     * Find subscription by company size
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:companySize%")
    List<Subscription> findByCompanySize(@Param("companySize") String companySize);

    /**
     * Find subscription by region
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:region%")
    List<Subscription> findByRegion(@Param("region") String region);

    /**
     * Find subscription by country
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:country%")
    List<Subscription> findByCountry(@Param("country") String country);

    /**
     * Find subscription by timezone
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:timezone%")
    List<Subscription> findByTimezone(@Param("timezone") String timezone);

    /**
     * Find subscription by language
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:language%")
    List<Subscription> findByLanguage(@Param("language") String language);

    /**
     * Find subscription by platform
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:platform%")
    List<Subscription> findByPlatform(@Param("platform") String platform);

    /**
     * Find subscription by browser
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:browser%")
    List<Subscription> findByBrowser(@Param("browser") String browser);

    /**
     * Find subscription by operating system
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:operatingSystem%")
    List<Subscription> findByOperatingSystem(@Param("operatingSystem") String operatingSystem);

    /**
     * Find subscription by device type
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:deviceType%")
    List<Subscription> findByDeviceType(@Param("deviceType") String deviceType);

    /**
     * Find subscription by user agent
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:userAgent%")
    List<Subscription> findByUserAgent(@Param("userAgent") String userAgent);

    /**
     * Find subscription by IP address
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:ipAddress%")
    List<Subscription> findByIpAddress(@Param("ipAddress") String ipAddress);

    /**
     * Find subscription by location
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:location%")
    List<Subscription> findByLocation(@Param("location") String location);

    /**
     * Find subscription by city
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:city%")
    List<Subscription> findByCity(@Param("city") String city);

    /**
     * Find subscription by postal code
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:postalCode%")
    List<Subscription> findByPostalCode(@Param("postalCode") String postalCode);

    /**
     * Count subscription by tenant ID
     */
    long countByTenantId(UUID tenantId);

    /**
     * Count subscription by plan type
     */
    long countByPlanType(PlanType planType);

    /**
     * Count subscription by status
     */
    long countByStatus(String status);

    /**
     * Count subscription by payment status
     */
    long countByPaymentStatus(String paymentStatus);

    /**
     * Count subscription by billing cycle
     */
    long countByBillingCycle(String billingCycle);

    /**
     * Count subscription by currency
     */
    long countByCurrency(String currency);

    /**
     * Count subscription by auto-renewal status
     */
    long countByAutoRenew(Boolean autoRenew);

    /**
     * Count subscription by approval required status
     */
    long countByApprovalRequired(Boolean approvalRequired);

    /**
     * Count subscription by rollback enabled status
     */
    long countByRollbackEnabled(Boolean rollbackEnabled);

    /**
     * Count subscription by archived status
     */
    long countByIsArchived(Boolean isArchived);

    /**
     * Count subscription by latest version status
     */
    long countByIsLatestVersion(Boolean isLatestVersion);

    /**
     * Count subscription created in specific month
     */
    @Query("SELECT COUNT(s) FROM Subscription s WHERE YEAR(s.createdAt) = :year AND MONTH(s.createdAt) = :month")
    long countByCreatedYearAndMonth(@Param("year") int year, @Param("month") int month);

    /**
     * Count subscription by tenant ID and status
     */
    long countByTenantIdAndStatus(UUID tenantId, String status);

    /**
     * Count subscription by tenant ID and plan type
     */
    long countByTenantIdAndPlanType(UUID tenantId, PlanType planType);

    /**
     * Find subscription with pagination
     */
    Page<Subscription> findAll(Pageable pageable);

    /**
     * Find subscription by tenant ID with pagination
     */
    Page<Subscription> findByTenantId(UUID tenantId, Pageable pageable);

    /**
     * Find subscription by status with pagination
     */
    Page<Subscription> findByStatus(String status, Pageable pageable);

    /**
     * Find subscription by plan type with pagination
     */
    Page<Subscription> findByPlanType(PlanType planType, Pageable pageable);

    /**
     * Find subscription by payment status with pagination
     */
    Page<Subscription> findByPaymentStatus(String paymentStatus, Pageable pageable);

    /**
     * Find subscription by billing cycle with pagination
     */
    Page<Subscription> findByBillingCycle(String billingCycle, Pageable pageable);

    /**
     * Find subscription by currency with pagination
     */
    Page<Subscription> findByCurrency(String currency, Pageable pageable);

    /**
     * Find subscription by auto-renewal status with pagination
     */
    Page<Subscription> findByAutoRenew(Boolean autoRenew, Pageable pageable);

    /**
     * Find subscription by approval required status with pagination
     */
    Page<Subscription> findByApprovalRequired(Boolean approvalRequired, Pageable pageable);

    /**
     * Find subscription by rollback enabled status with pagination
     */
    Page<Subscription> findByRollbackEnabled(Boolean rollbackEnabled, Pageable pageable);

    /**
     * Find subscription by archived status with pagination
     */
    Page<Subscription> findByIsArchived(Boolean isArchived, Pageable pageable);

    /**
     * Find subscription by latest version status with pagination
     */
    Page<Subscription> findByIsLatestVersion(Boolean isLatestVersion, Pageable pageable);

    /**
     * Search subscription by plan name containing text
     */
    Page<Subscription> findByPlanNameContainingIgnoreCase(String planName, Pageable pageable);

    /**
     * Find subscription by multiple criteria
     */
    @Query("SELECT s FROM Subscription s WHERE " +
           "(:tenantId IS NULL OR s.tenantId = :tenantId) AND " +
           "(:planType IS NULL OR s.planType = :planType) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:paymentStatus IS NULL OR s.paymentStatus = :paymentStatus)")
    Page<Subscription> findByMultipleCriteria(
            @Param("tenantId") UUID tenantId,
            @Param("planType") PlanType planType,
            @Param("status") String status,
            @Param("paymentStatus") String paymentStatus,
            Pageable pageable
    );

    /**
     * Find subscription with expired trial
     */
    @Query("SELECT s FROM Subscription s WHERE s.trialEndDate IS NOT NULL AND s.trialEndDate < :currentDate")
    List<Subscription> findSubscriptionsWithExpiredTrial(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find subscription approaching trial expiry
     */
    @Query("SELECT s FROM Subscription s WHERE s.trialEndDate IS NOT NULL AND s.trialEndDate BETWEEN :startDate AND :endDate")
    List<Subscription> findSubscriptionsApproachingTrialExpiry(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find subscription with expired grace period
     */
    @Query("SELECT s FROM Subscription s WHERE s.gracePeriodEnd IS NOT NULL AND s.gracePeriodEnd < :currentDate")
    List<Subscription> findSubscriptionsWithExpiredGracePeriod(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find subscription approaching grace period expiry
     */
    @Query("SELECT s FROM Subscription s WHERE s.gracePeriodEnd IS NOT NULL AND s.gracePeriodEnd BETWEEN :startDate AND :endDate")
    List<Subscription> findSubscriptionsApproachingGracePeriodExpiry(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find subscription with expired next billing date
     */
    @Query("SELECT s FROM Subscription s WHERE s.nextBillingDate IS NOT NULL AND s.nextBillingDate < :currentDate")
    List<Subscription> findSubscriptionsWithExpiredNextBillingDate(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find subscription approaching next billing date
     */
    @Query("SELECT s FROM Subscription s WHERE s.nextBillingDate IS NOT NULL AND s.nextBillingDate BETWEEN :startDate AND :endDate")
    List<Subscription> findSubscriptionsApproachingNextBillingDate(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find subscription with max failed payments
     */
    @Query("SELECT s FROM Subscription s WHERE s.failedPaymentCount >= s.maxFailedPayments")
    List<Subscription> findSubscriptionsWithMaxFailedPayments();

    /**
     * Find subscription that can be retried
     */
    @Query("SELECT s FROM Subscription s WHERE s.failedPaymentCount < s.maxFailedPayments")
    List<Subscription> findSubscriptionsThatCanBeRetried();

    /**
     * Find subscription by custom field
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:fieldName%")
    List<Subscription> findByCustomField(@Param("fieldName") String fieldName);

    /**
     * Find subscription by audit trail
     */
    @Query("SELECT s FROM Subscription s WHERE s.metadata::text LIKE %:auditEvent%")
    List<Subscription> findByAuditEvent(@Param("auditEvent") String auditEvent);
}
