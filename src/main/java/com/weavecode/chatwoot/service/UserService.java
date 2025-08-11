package com.weavecode.chatwoot.service;

import com.weavecode.chatwoot.entity.User;
import com.weavecode.chatwoot.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for User entity business logic
 * Implements user management operations and business rules
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
public interface UserService extends BaseService<User, UUID> {

    /**
     * Find user by email (global search)
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email within tenant
     */
    Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

    /**
     * Find user by username within tenant
     */
    Optional<User> findByUsernameAndTenantId(String username, UUID tenantId);

    /**
     * Find users by role within tenant
     */
    List<User> findByRoleAndTenantId(UserRole role, UUID tenantId);

    /**
     * Find users by department within tenant
     */
    List<User> findByDepartmentAndTenantId(String department, UUID tenantId);

    /**
     * Find active users within tenant
     */
    List<User> findActiveUsersByTenantId(UUID tenantId);

    /**
     * Find users by status within tenant
     */
    Page<User> findByStatusAndTenantId(String status, UUID tenantId, Pageable pageable);

    /**
     * Find users by role within tenant with pagination
     */
    Page<User> findByRoleAndTenantId(UserRole role, UUID tenantId, Pageable pageable);

    /**
     * Find users by department within tenant with pagination
     */
    Page<User> findByDepartmentAndTenantId(String department, UUID tenantId, Pageable pageable);

    /**
     * Find users created within date range for tenant
     */
    List<User> findByCreatedAtBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, UUID tenantId);

    /**
     * Find users by working hours within tenant
     */
    List<User> findByWorkingHoursAndTenantId(String workingHours, UUID tenantId);

    /**
     * Find users by timezone within tenant
     */
    List<User> findByTimezoneAndTenantId(String timezone, UUID tenantId);

    /**
     * Find users by language within tenant
     */
    List<User> findByLanguageAndTenantId(String language, UUID tenantId);

    /**
     * Check if email is available within tenant
     */
    boolean isEmailAvailable(String email, UUID tenantId);

    /**
     * Check if username is available within tenant
     */
    boolean isUsernameAvailable(String username, UUID tenantId);

    /**
     * Authenticate user with email and password
     */
    Optional<User> authenticateUser(String email, String password, UUID tenantId);

    /**
     * Change user password
     */
    User changePassword(UUID userId, String currentPassword, String newPassword);

    /**
     * Reset user password
     */
    User resetPassword(UUID userId, String newPassword);

    /**
     * Activate user account
     */
    User activateUser(UUID userId);

    /**
     * Deactivate user account
     */
    User deactivateUser(UUID userId, String reason);

    /**
     * Suspend user account
     */
    User suspendUser(UUID userId, String reason, LocalDateTime suspensionEndDate);

    /**
     * Reactivate suspended user
     */
    User reactivateUser(UUID userId);

    /**
     * Update user role
     */
    User updateUserRole(UUID userId, UserRole newRole);

    /**
     * Update user department
     */
    User updateUserDepartment(UUID userId, String newDepartment);

    /**
     * Update user working hours
     */
    User updateWorkingHours(UUID userId, String workingHours);

    /**
     * Update user timezone
     */
    User updateTimezone(UUID userId, String timezone);

    /**
     * Update user language preferences
     */
    User updateLanguagePreferences(UUID userId, String language);

    /**
     * Update user notification settings
     */
    User updateNotificationSettings(UUID userId, String notificationSettings);

    /**
     * Update user profile picture
     */
    User updateProfilePicture(UUID userId, String pictureUrl);

    /**
     * Get user performance metrics
     */
    UserPerformanceMetrics getUserPerformanceMetrics(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get user satisfaction score
     */
    double getUserSatisfactionScore(UUID userId);

    /**
     * Get user working hours
     */
    UserWorkingHours getUserWorkingHours(UUID userId);

    /**
     * Check if user is available for work
     */
    boolean isUserAvailable(UUID userId);

    /**
     * Get user availability for specific time
     */
    boolean isUserAvailableAt(UUID userId, LocalDateTime dateTime);

    /**
     * Get user next available time
     */
    LocalDateTime getUserNextAvailableTime(UUID userId);

    /**
     * Update user last activity
     */
    User updateLastActivity(UUID userId);

    /**
     * Get user activity log
     */
    List<UserActivityLog> getUserActivityLog(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Search users within tenant with advanced criteria
     */
    Page<User> searchUsers(String query, UserRole role, String department, String status, 
                          UUID tenantId, Pageable pageable);

    /**
     * Get user dashboard data
     */
    UserDashboardData getUserDashboardData(UUID userId);

    /**
     * Bulk update user status
     */
    List<User> bulkUpdateUserStatus(List<UUID> userIds, String newStatus);

    /**
     * Bulk update user role
     */
    List<User> bulkUpdateUserRole(List<UUID> userIds, UserRole newRole);

    /**
     * Bulk update user department
     */
    List<User> bulkUpdateUserDepartment(List<UUID> userIds, String newDepartment);

    /**
     * Get user statistics for tenant
     */
    UserStatistics getUserStatistics(UUID tenantId);

    /**
     * Get user onboarding progress
     */
    UserOnboardingProgress getUserOnboardingProgress(UUID userId);

    /**
     * Update user onboarding progress
     */
    User updateOnboardingProgress(UUID userId, String step, boolean completed);

    /**
     * Get user training recommendations
     */
    List<TrainingRecommendation> getUserTrainingRecommendations(UUID userId);

    /**
     * Record user training completion
     */
    User recordTrainingCompletion(UUID userId, String trainingType, String trainingId);
}
