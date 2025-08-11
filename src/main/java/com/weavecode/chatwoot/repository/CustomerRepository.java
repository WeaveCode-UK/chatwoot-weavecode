package com.weavecode.chatwoot.repository;

import com.weavecode.chatwoot.entity.Customer;
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
 * Repository interface for Customer entity
 * Provides data access methods for customer management
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Repository
public interface CustomerRepository extends BaseRepository<Customer, UUID> {

    /**
     * Find customer by email (case-insensitive)
     */
    Optional<Customer> findByEmailIgnoreCase(String email);

    /**
     * Find customer by phone number
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    /**
     * Find customers by tenant ID
     */
    List<Customer> findByTenantId(UUID tenantId);

    /**
     * Find customers by company name
     */
    List<Customer> findByCompanyNameIgnoreCase(String companyName);

    /**
     * Find customers by job title
     */
    List<Customer> findByJobTitleIgnoreCase(String jobTitle);

    /**
     * Find customers by country
     */
    List<Customer> findByCountryIgnoreCase(String country);

    /**
     * Find customers by city
     */
    List<Customer> findByCityIgnoreCase(String city);

    /**
     * Find customers by state/province
     */
    List<Customer> findByStateProvinceIgnoreCase(String stateProvince);

    /**
     * Find customers by postal code
     */
    List<Customer> findByPostalCode(String postalCode);

    /**
     * Find customers by language
     */
    List<Customer> findByLanguage(String language);

    /**
     * Find customers by timezone
     */
    List<Customer> findByTimezone(String timezone);

    /**
     * Find customers by gender
     */
    List<Customer> findByGender(String gender);

    /**
     * Find customers by source
     */
    List<Customer> findBySource(String source);

    /**
     * Find customers by source ID
     */
    List<Customer> findBySourceId(String sourceId);

    /**
     * Find customers by verification status
     */
    List<Customer> findByIsVerified(Boolean isVerified);

    /**
     * Find customers by blocked status
     */
    List<Customer> findByIsBlocked(Boolean isBlocked);

    /**
     * Find customers by archived status
     */
    List<Customer> findByIsArchived(Boolean isArchived);

    /**
     * Find customers by GDPR consent
     */
    List<Customer> findByGdprConsent(Boolean gdprConsent);

    /**
     * Find customers by marketing consent
     */
    List<Customer> findByMarketingConsent(Boolean marketingConsent);

    /**
     * Find customers by tag
     */
    @Query("SELECT c FROM Customer c WHERE c.tags LIKE %:tag%")
    List<Customer> findByTag(@Param("tag") String tag);

    /**
     * Find customers by social media
     */
    @Query("SELECT c FROM Customer c WHERE c.socialMedia::text LIKE %:platform%")
    List<Customer> findBySocialMediaPlatform(@Param("platform") String platform);

    /**
     * Find customers by preference
     */
    @Query("SELECT c FROM Customer c WHERE c.preferences::text LIKE %:preference%")
    List<Customer> findByPreference(@Param("preference") String preference);

    /**
     * Find customers by custom field value
     */
    @Query("SELECT c FROM Customer c WHERE c.metadata::text LIKE %:value%")
    List<Customer> findByCustomFieldValue(@Param("value") String value);

    /**
     * Find customers created after specific date
     */
    List<Customer> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find customers created between dates
     */
    List<Customer> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find customers by first contact date
     */
    List<Customer> findByFirstContactAtAfter(LocalDateTime date);

    /**
     * Find customers by last contact date
     */
    List<Customer> findByLastContactAtAfter(LocalDateTime date);

    /**
     * Find customers by verification date
     */
    List<Customer> findByVerifiedAtAfter(LocalDateTime date);

    /**
     * Find customers by block date
     */
    List<Customer> findByBlockedAtAfter(LocalDateTime date);

    /**
     * Find customers by archive date
     */
    List<Customer> findByArchivedAtAfter(LocalDateTime date);

    /**
     * Find customers by GDPR consent date
     */
    List<Customer> findByGdprConsentAtAfter(LocalDateTime date);

    /**
     * Find customers by marketing consent date
     */
    List<Customer> findByMarketingConsentAtAfter(LocalDateTime date);

    /**
     * Find customers by data retention date
     */
    List<Customer> findByDataRetentionUntilAfter(LocalDateTime date);

    /**
     * Find customers by conversation count
     */
    @Query("SELECT c FROM Customer c WHERE c.totalConversations >= :minConversations")
    List<Customer> findByMinConversationCount(@Param("minConversations") Integer minConversations);

    /**
     * Find customers by message count
     */
    @Query("SELECT c FROM Customer c WHERE c.totalMessages >= :minMessages")
    List<Customer> findByMinMessageCount(@Param("minMessages") Integer minMessages);

    /**
     * Find customers by response time
     */
    @Query("SELECT c FROM Customer c WHERE c.averageResponseTime <= :maxResponseTime")
    List<Customer> findByMaxResponseTime(@Param("maxResponseTime") Long maxResponseTime);

    /**
     * Find customers by satisfaction score
     */
    @Query("SELECT c FROM Customer c WHERE c.customerSatisfactionScore >= :minScore")
    List<Customer> findByMinSatisfactionScore(@Param("minScore") Double minScore);

    /**
     * Find customers by lifetime value
     */
    @Query("SELECT c FROM Customer c WHERE c.lifetimeValue >= :minValue")
    List<Customer> findByMinLifetimeValue(@Param("minValue") Double minValue);

    /**
     * Find customers by currency
     */
    List<Customer> findByCurrency(String currency);

    /**
     * Find customers by blocked by user
     */
    List<Customer> findByBlockedBy(UUID blockedBy);

    /**
     * Find customers by archived by user
     */
    List<Customer> findByArchivedBy(UUID archivedBy);

    /**
     * Count customers by tenant ID
     */
    long countByTenantId(UUID tenantId);

    /**
     * Count customers by country
     */
    long countByCountryIgnoreCase(String country);

    /**
     * Count customers by city
     */
    long countByCityIgnoreCase(String city);

    /**
     * Count customers by state/province
     */
    long countByStateProvinceIgnoreCase(String stateProvince);

    /**
     * Count customers by language
     */
    long countByLanguage(String language);

    /**
     * Count customers by timezone
     */
    long countByTimezone(String timezone);

    /**
     * Count customers by gender
     */
    long countByGender(String gender);

    /**
     * Count customers by source
     */
    long countBySource(String source);

    /**
     * Count customers by verification status
     */
    long countByIsVerified(Boolean isVerified);

    /**
     * Count customers by blocked status
     */
    long countByIsBlocked(Boolean isBlocked);

    /**
     * Count customers by archived status
     */
    long countByIsArchived(Boolean isArchived);

    /**
     * Count customers by GDPR consent
     */
    long countByGdprConsent(Boolean gdprConsent);

    /**
     * Count customers by marketing consent
     */
    long countByMarketingConsent(Boolean marketingConsent);

    /**
     * Count customers created in specific month
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE YEAR(c.createdAt) = :year AND MONTH(c.createdAt) = :month")
    long countByCreatedYearAndMonth(@Param("year") int year, @Param("month") int month);

    /**
     * Count customers by tenant ID and verification status
     */
    long countByTenantIdAndIsVerified(UUID tenantId, Boolean isVerified);

    /**
     * Count customers by tenant ID and blocked status
     */
    long countByTenantIdAndIsBlocked(UUID tenantId, Boolean isBlocked);

    /**
     * Count customers by tenant ID and archived status
     */
    long countByTenantIdAndIsArchived(UUID tenantId, Boolean isArchived);

    /**
     * Find customers with pagination
     */
    Page<Customer> findAll(Pageable pageable);

    /**
     * Find customers by tenant ID with pagination
     */
    Page<Customer> findByTenantId(UUID tenantId, Pageable pageable);

    /**
     * Find customers by verification status with pagination
     */
    Page<Customer> findByIsVerified(Boolean isVerified, Pageable pageable);

    /**
     * Find customers by blocked status with pagination
     */
    Page<Customer> findByIsBlocked(Boolean isBlocked, Pageable pageable);

    /**
     * Find customers by archived status with pagination
     */
    Page<Customer> findByIsArchived(Boolean isArchived, Pageable pageable);

    /**
     * Search customers by name containing text
     */
    Page<Customer> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Search customers by email containing text
     */
    Page<Customer> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    /**
     * Search customers by company name containing text
     */
    Page<Customer> findByCompanyNameContainingIgnoreCase(String companyName, Pageable pageable);

    /**
     * Find customers by multiple criteria
     */
    @Query("SELECT c FROM Customer c WHERE " +
           "(:tenantId IS NULL OR c.tenantId = :tenantId) AND " +
           "(:isVerified IS NULL OR c.isVerified = :isVerified) AND " +
           "(:isBlocked IS NULL OR c.isBlocked = :isBlocked) AND " +
           "(:isArchived IS NULL OR c.isArchived = :isArchived)")
    Page<Customer> findByMultipleCriteria(
            @Param("tenantId") UUID tenantId,
            @Param("isVerified") Boolean isVerified,
            @Param("isBlocked") Boolean isBlocked,
            @Param("isArchived") Boolean isArchived,
            Pageable pageable
    );

    /**
     * Find customers with expired data retention
     */
    @Query("SELECT c FROM Customer c WHERE c.dataRetentionUntil IS NOT NULL AND c.dataRetentionUntil < :currentDate")
    List<Customer> findCustomersWithExpiredDataRetention(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find customers approaching data retention expiry
     */
    @Query("SELECT c FROM Customer c WHERE c.dataRetentionUntil IS NOT NULL AND c.dataRetentionUntil BETWEEN :startDate AND :endDate")
    List<Customer> findCustomersApproachingDataRetentionExpiry(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find customers by age range
     */
    @Query("SELECT c FROM Customer c WHERE c.dateOfBirth IS NOT NULL AND c.dateOfBirth BETWEEN :startDate AND :endDate")
    List<Customer> findByDateOfBirthBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find customers by age
     */
    @Query("SELECT c FROM Customer c WHERE c.dateOfBirth IS NOT NULL AND YEAR(c.dateOfBirth) = :birthYear")
    List<Customer> findByBirthYear(@Param("birthYear") int birthYear);

    /**
     * Find customers by age group
     */
    @Query("SELECT c FROM Customer c WHERE c.dateOfBirth IS NOT NULL AND c.dateOfBirth BETWEEN :startDate AND :endDate")
    List<Customer> findByAgeGroup(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find customers by custom field
     */
    @Query("SELECT c FROM Customer c WHERE c.metadata::text LIKE %:fieldName%")
    List<Customer> findByCustomField(@Param("fieldName") String fieldName);

    /**
     * Find customers by audit trail
     */
    @Query("SELECT c FROM Customer c WHERE c.metadata::text LIKE %:auditEvent%")
    List<Customer> findByAuditEvent(@Param("auditEvent") String auditEvent);

    /**
     * Find customers by platform
     */
    @Query("SELECT c FROM Customer c WHERE c.metadata::text LIKE %:platform%")
    List<Customer> findByPlatform(@Param("platform") String platform);

    /**
     * Find customers by browser
     */
    @Query("SELECT c FROM Customer c WHERE c.metadata::text LIKE %:browser%")
    List<Customer> findByBrowser(@Param("browser") String browser);

    /**
     * Find customers by operating system
     */
    @Query("SELECT c FROM Customer c WHERE c.metadata::text LIKE %:operatingSystem%")
    List<Customer> findByOperatingSystem(@Param("operatingSystem") String operatingSystem);

    /**
     * Find customers by device type
     */
    @Query("SELECT c FROM Customer c WHERE c.metadata::text LIKE %:deviceType%")
    List<Customer> findByDeviceType(@Param("deviceType") String deviceType);

    /**
     * Find customers by user agent
     */
    @Query("SELECT c FROM Customer c WHERE c.metadata::text LIKE %:userAgent%")
    List<Customer> findByUserAgent(@Param("userAgent") String userAgent);

    /**
     * Find customers by IP address
     */
    @Query("SELECT c FROM Customer c WHERE c.metadata::text LIKE %:ipAddress%")
    List<Customer> findByIpAddress(@Param("ipAddress") String ipAddress);

    /**
     * Find customers by location
     */
    @Query("SELECT c FROM Customer c WHERE c.metadata::text LIKE %:location%")
    List<Customer> findByLocation(@Param("location") String location);

    /**
     * Find customers by region
     */
    @Query("SELECT c FROM Customer c WHERE c.metadata::text LIKE %:region%")
    List<Customer> findByRegion(@Param("region") String region);

    /**
     * Find customers by postal code
     */
    @Query("SELECT c FROM Customer c WHERE c.metadata::text LIKE %:postalCode%")
    List<Customer> findByPostalCodeFromMetadata(@Param("postalCode") String postalCode);
}
