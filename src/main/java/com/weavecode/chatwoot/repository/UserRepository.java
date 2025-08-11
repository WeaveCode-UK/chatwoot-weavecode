package com.weavecode.chatwoot.repository;

import com.weavecode.chatwoot.entity.User;
import com.weavecode.chatwoot.enums.UserRole;
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
 * Repository interface for User entity
 * Provides data access methods for multi-tenant user management
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Repository
public interface UserRepository extends BaseRepository<User, UUID> {

    /**
     * Find user by email (case-insensitive)
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Find user by username (case-insensitive)
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * Find user by phone number
     */
    Optional<User> findByPhoneNumber(String phoneNumber);

    /**
     * Find users by tenant ID
     */
    List<User> findByTenantId(UUID tenantId);

    /**
     * Find users by role
     */
    List<User> findByRole(UserRole role);

    /**
     * Find users by tenant ID and role
     */
    List<User> findByTenantIdAndRole(UUID tenantId, UserRole role);

    /**
     * Find users by status
     */
    List<User> findByStatus(String status);

    /**
     * Find active users by tenant ID
     */
    List<User> findByTenantIdAndStatusAndIsArchivedFalse(UUID tenantId, String status);

    /**
     * Find users by department
     */
    List<User> findByDepartmentIgnoreCase(String department);

    /**
     * Find users by job title
     */
    List<User> findByJobTitleIgnoreCase(String jobTitle);

    /**
     * Find users by country
     */
    List<User> findByCountryIgnoreCase(String country);

    /**
     * Find users by timezone
     */
    List<User> findByTimezone(String timezone);

    /**
     * Find users by language
     */
    List<User> findByLanguage(String language);

    /**
     * Find users created after specific date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find users created between dates
     */
    List<User> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find users by last login date
     */
    List<User> findByLastLoginAtAfter(LocalDateTime date);

    /**
     * Find users by last activity date
     */
    List<User> findByLastActivityAtAfter(LocalDateTime date);

    /**
     * Find users with 2FA enabled
     */
    List<User> findByTwoFactorEnabledTrue();

    /**
     * Find users with 2FA disabled
     */
    List<User> findByTwoFactorEnabledFalse();

    /**
     * Find users by verification status
     */
    List<User> findByIsVerified(Boolean isVerified);

    /**
     * Find users by account status
     */
    List<User> findByAccountStatus(String accountStatus);

    /**
     * Find users by subscription status
     */
    @Query("SELECT u FROM User u WHERE u.subscriptionStatus = :status")
    List<User> findBySubscriptionStatus(@Param("status") String status);

    /**
     * Find users by custom field value
     */
    @Query("SELECT u FROM User u WHERE u.customFields::text LIKE %:value%")
    List<User> findByCustomFieldValue(@Param("value") String value);

    /**
     * Find users by tag
     */
    @Query("SELECT u FROM User u WHERE u.tags LIKE %:tag%")
    List<User> findByTag(@Param("tag") String tag);

    /**
     * Find users by skill
     */
    @Query("SELECT u FROM User u WHERE u.skills::text LIKE %:skill%")
    List<User> findBySkill(@Param("skill") String skill);

    /**
     * Find users by permission
     */
    @Query("SELECT u FROM User u WHERE u.permissions::text LIKE %:permission%")
    List<User> findByPermission(@Param("permission") String permission);

    /**
     * Find users by team
     */
    @Query("SELECT u FROM User u WHERE u.teams::text LIKE %:team%")
    List<User> findByTeam(@Param("team") String team);

    /**
     * Find users by project
     */
    @Query("SELECT u FROM User u WHERE u.projects::text LIKE %:project%")
    List<User> findByProject(@Param("project") String project);

    /**
     * Find users by availability status
     */
    List<User> findByAvailabilityStatus(String availabilityStatus);

    /**
     * Find users by working hours
     */
    @Query("SELECT u FROM User u WHERE u.workingHours::text LIKE %:workingHours%")
    List<User> findByWorkingHours(@Param("workingHours") String workingHours);

    /**
     * Find users by holiday calendar
     */
    @Query("SELECT u FROM User u WHERE u.holidayCalendar::text LIKE %:holiday%")
    List<User> findByHolidayCalendar(@Param("holiday") String holiday);

    /**
     * Find users by notification preference
     */
    @Query("SELECT u FROM User u WHERE u.notificationPreferences::text LIKE %:preference%")
    List<User> findByNotificationPreference(@Param("preference") String preference);

    /**
     * Find users by integration account
     */
    @Query("SELECT u FROM User u WHERE u.integrationAccounts::text LIKE %:integration%")
    List<User> findByIntegrationAccount(@Param("integration") String integration);

    /**
     * Find users by API key
     */
    Optional<User> findByApiKey(String apiKey);

    /**
     * Find users by session token
     */
    Optional<User> findBySessionToken(String sessionToken);

    /**
     * Find users by password reset token
     */
    Optional<User> findByPasswordResetToken(String passwordResetToken);

    /**
     * Find users by email verification token
     */
    Optional<User> findByEmailVerificationToken(String emailVerificationToken);

    /**
     * Find users by phone verification code
     */
    Optional<User> findByPhoneVerificationCode(String phoneVerificationCode);

    /**
     * Count users by tenant ID
     */
    long countByTenantId(UUID tenantId);

    /**
     * Count users by role
     */
    long countByRole(UserRole role);

    /**
     * Count users by tenant ID and role
     */
    long countByTenantIdAndRole(UUID tenantId, UserRole role);

    /**
     * Count users by status
     */
    long countByStatus(String status);

    /**
     * Count users by department
     */
    long countByDepartmentIgnoreCase(String department);

    /**
     * Count users by country
     */
    long countByCountryIgnoreCase(String country);

    /**
     * Count users created in specific month
     */
    @Query("SELECT COUNT(u) FROM User u WHERE YEAR(u.createdAt) = :year AND MONTH(u.createdAt) = :month")
    long countByCreatedYearAndMonth(@Param("year") int year, @Param("month") int month);

    /**
     * Count users by tenant ID and status
     */
    long countByTenantIdAndStatus(UUID tenantId, String status);

    /**
     * Find users with pagination
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Find users by tenant ID with pagination
     */
    Page<User> findByTenantId(UUID tenantId, Pageable pageable);

    /**
     * Find users by role with pagination
     */
    Page<User> findByRole(UserRole role, Pageable pageable);

    /**
     * Find users by status with pagination
     */
    Page<User> findByStatus(String status, Pageable pageable);

    /**
     * Find users by tenant ID and role with pagination
     */
    Page<User> findByTenantIdAndRole(UUID tenantId, UserRole role, Pageable pageable);

    /**
     * Search users by name containing text
     */
    Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName, Pageable pageable);

    /**
     * Search users by email containing text
     */
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    /**
     * Find users by multiple criteria
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:tenantId IS NULL OR u.tenantId = :tenantId) AND " +
           "(:role IS NULL OR u.role = :role) AND " +
           "(:status IS NULL OR u.status = :status) AND " +
           "(:department IS NULL OR u.department = :department)")
    Page<User> findByMultipleCriteria(
            @Param("tenantId") UUID tenantId,
            @Param("role") UserRole role,
            @Param("status") String status,
            @Param("department") String department,
            Pageable pageable
    );

    /**
     * Find users with expired passwords
     */
    @Query("SELECT u FROM User u WHERE u.passwordExpiresAt IS NOT NULL AND u.passwordExpiresAt < :currentDate")
    List<User> findUsersWithExpiredPasswords(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find users with inactive accounts
     */
    @Query("SELECT u FROM User u WHERE u.lastActivityAt < :date")
    List<User> findInactiveUsers(@Param("date") LocalDateTime date);

    /**
     * Find users by login attempt count
     */
    @Query("SELECT u FROM User u WHERE u.failedLoginAttempts >= :maxAttempts")
    List<User> findUsersWithMaxFailedLoginAttempts(@Param("maxAttempts") Integer maxAttempts);

    /**
     * Find users by session expiry
     */
    @Query("SELECT u FROM User u WHERE u.sessionExpiresAt IS NOT NULL AND u.sessionExpiresAt < :currentDate")
    List<User> findUsersWithExpiredSessions(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find users by subscription expiry
     */
    @Query("SELECT u FROM User u WHERE u.subscriptionExpiresAt IS NOT NULL AND u.subscriptionExpiresAt < :currentDate")
    List<User> findUsersWithExpiredSubscriptions(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find users by custom field
     */
    @Query("SELECT u FROM User u WHERE u.customFields::text LIKE %:fieldName%")
    List<User> findByCustomField(@Param("fieldName") String fieldName);

    /**
     * Find users by metadata
     */
    @Query("SELECT u FROM User u WHERE u.metadata::text LIKE %:metadataValue%")
    List<User> findByMetadata(@Param("metadataValue") String metadataValue);

    /**
     * Find users by audit trail
     */
    @Query("SELECT u FROM User u WHERE u.auditTrail::text LIKE %:auditEvent%")
    List<User> findByAuditEvent(@Param("auditEvent") String auditEvent);

    /**
     * Find users by performance rating
     */
    @Query("SELECT u FROM User u WHERE u.performanceRating >= :minRating")
    List<User> findByMinPerformanceRating(@Param("minRating") Double minRating);

    /**
     * Find users by satisfaction score
     */
    @Query("SELECT u FROM User u WHERE u.customerSatisfactionScore >= :minScore")
    List<User> findByMinCustomerSatisfactionScore(@Param("minScore") Double minScore);

    /**
     * Find users by response time
     */
    @Query("SELECT u FROM User u WHERE u.averageResponseTime <= :maxResponseTime")
    List<User> findByMaxResponseTime(@Param("maxResponseTime") Long maxResponseTime);

    /**
     * Find users by conversation count
     */
    @Query("SELECT u FROM User u WHERE u.totalConversations >= :minConversations")
    List<User> findByMinConversationCount(@Param("minConversations") Integer minConversations);

    /**
     * Find users by message count
     */
    @Query("SELECT u FROM User u WHERE u.totalMessages >= :minMessages")
    List<User> findByMinMessageCount(@Param("minMessages") Integer minMessages);

    /**
     * Find users by online status
     */
    List<User> findByOnlineStatus(String onlineStatus);

    /**
     * Find users by busy status
     */
    List<User> findByBusyStatus(String busyStatus);

    /**
     * Find users by away status
     */
    List<User> findByAwayStatus(String awayStatus);

    /**
     * Find users by break status
     */
    List<User> findByBreakStatus(String breakStatus);

    /**
     * Find users by training status
     */
    List<User> findByTrainingStatus(String trainingStatus);

    /**
     * Find users by certification
     */
    @Query("SELECT u FROM User u WHERE u.certifications::text LIKE %:certification%")
    List<User> findByCertification(@Param("certification") String certification);

    /**
     * Find users by experience level
     */
    List<User> findByExperienceLevel(String experienceLevel);

    /**
     * Find users by specialisation
     */
    @Query("SELECT u FROM User u WHERE u.specialisations::text LIKE %:specialisation%")
    List<User> findBySpecialisation(@Param("specialisation") String specialisation);

    /**
     * Find users by language proficiency
     */
    @Query("SELECT u FROM User u WHERE u.languageProficiencies::text LIKE %:language%")
    List<User> findByLanguageProficiency(@Param("language") String language);

    /**
     * Find users by timezone range
     */
    @Query("SELECT u FROM User u WHERE u.timezoneOffset BETWEEN :minOffset AND :maxOffset")
    List<User> findByTimezoneOffsetBetween(@Param("minOffset") Integer minOffset, @Param("maxOffset") Integer maxOffset);

    /**
     * Find users by working pattern
     */
    @Query("SELECT u FROM User u WHERE u.workingPattern::text LIKE %:pattern%")
    List<User> findByWorkingPattern(@Param("pattern") String pattern);

    /**
     * Find users by shift preference
     */
    List<User> findByShiftPreference(String shiftPreference);

    /**
     * Find users by overtime preference
     */
    List<User> findByOvertimePreference(String overtimePreference);

    /**
     * Find users by remote work preference
     */
    List<User> findByRemoteWorkPreference(String remoteWorkPreference);

    /**
     * Find users by travel preference
     */
    List<User> findByTravelPreference(String travelPreference);

    /**
     * Find users by equipment preference
     */
    @Query("SELECT u FROM User u WHERE u.equipmentPreferences::text LIKE %:equipment%")
    List<User> findByEquipmentPreference(@Param("equipment") String equipment);

    /**
     * Find users by software preference
     */
    @Query("SELECT u FROM User u WHERE u.softwarePreferences::text LIKE %:software%")
    List<User> findBySoftwarePreference(@Param("software") String software);

    /**
     * Find users by hardware preference
     */
    @Query("SELECT u FROM User u WHERE u.hardwarePreferences::text LIKE %:hardware%")
    List<User> findByHardwarePreference(@Param("hardware") String hardware);
}
