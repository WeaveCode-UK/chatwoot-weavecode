package com.weavecode.chatwoot.service.impl;

import com.weavecode.chatwoot.dto.UserPerformanceMetrics;
import com.weavecode.chatwoot.entity.User;
import com.weavecode.chatwoot.enums.UserRole;
import com.weavecode.chatwoot.exception.EntityNotFoundException;
import com.weavecode.chatwoot.exception.ValidationException;
import com.weavecode.chatwoot.repository.UserRepository;
import com.weavecode.chatwoot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<User, UUID> implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        super(userRepository);
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    protected UUID getEntityId(User entity) {
        return entity.getId();
    }
    
    @Override
    protected User mergeEntities(User existing, User updated) {
        if (updated.getFirstName() != null) {
            existing.setFirstName(updated.getFirstName());
        }
        if (updated.getLastName() != null) {
            existing.setLastName(updated.getLastName());
        }
        if (updated.getEmail() != null) {
            existing.setEmail(updated.getEmail());
        }
        if (updated.getRole() != null) {
            existing.setRole(updated.getRole());
        }
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }
        if (updated.getDepartment() != null) {
            existing.setDepartment(updated.getDepartment());
        }
        if (updated.getWorkingHours() != null) {
            existing.setWorkingHours(updated.getWorkingHours());
        }
        if (updated.getTimezone() != null) {
            existing.setTimezone(updated.getTimezone());
        }
        if (updated.getLanguage() != null) {
            existing.setLanguage(updated.getLanguage());
        }
        if (updated.getNotificationSettings() != null) {
            existing.setNotificationSettings(updated.getNotificationSettings());
        }
        if (updated.getProfilePictureUrl() != null) {
            existing.setProfilePictureUrl(updated.getProfilePictureUrl());
        }
        
        existing.setUpdatedAt(LocalDateTime.now());
        return existing;
    }
    
    @Override
    public void validateBusinessRules(User user) {
        // Validate email format
        if (!StringUtils.hasText(user.getEmail()) || !user.getEmail().contains("@")) {
            throw new ValidationException("Invalid email format");
        }
        
        // Validate password strength
        if (user.getPassword() != null && user.getPassword().length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long");
        }
        
        // Validate role
        if (user.getRole() == null) {
            throw new ValidationException("User role is required");
        }
        
        // Validate tenant ID
        if (user.getTenantId() == null) {
            throw new ValidationException("Tenant ID is required");
        }
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    @Override
    public Optional<User> findByEmailAndTenantId(String email, UUID tenantId) {
        return userRepository.findByEmailAndTenantId(email, tenantId);
    }
    
    @Override
    public Optional<User> findByUsernameAndTenantId(String username, UUID tenantId) {
        return userRepository.findByUsernameAndTenantId(username, tenantId);
    }
    
    @Override
    public List<User> findByRoleAndTenantId(UserRole role, UUID tenantId) {
        return userRepository.findByRoleAndTenantId(role, tenantId);
    }
    
    @Override
    public List<User> findByDepartmentAndTenantId(String department, UUID tenantId) {
        return userRepository.findByDepartmentAndTenantId(department, tenantId);
    }
    
    @Override
    public List<User> findActiveUsersByTenantId(UUID tenantId) {
        return userRepository.findByStatusAndTenantId("active", tenantId);
    }
    
    @Override
    public Page<User> findByStatusAndTenantId(String status, UUID tenantId, Pageable pageable) {
        return userRepository.findByStatusAndTenantId(status, tenantId, pageable);
    }
    
    @Override
    public Page<User> findByRoleAndTenantId(UserRole role, UUID tenantId, Pageable pageable) {
        return userRepository.findByRoleAndTenantId(role, tenantId, pageable);
    }
    
    @Override
    public Page<User> findByDepartmentAndTenantId(String department, UUID tenantId, Pageable pageable) {
        return userRepository.findByDepartmentAndTenantId(department, tenantId, pageable);
    }
    
    @Override
    public List<User> findByCreatedAtBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, UUID tenantId) {
        return userRepository.findByCreatedAtBetweenAndTenantId(startDate, endDate, tenantId);
    }
    
    @Override
    public List<User> findByWorkingHoursAndTenantId(String workingHours, UUID tenantId) {
        return userRepository.findByWorkingHoursAndTenantId(workingHours, tenantId);
    }
    
    @Override
    public List<User> findByTimezoneAndTenantId(String timezone, UUID tenantId) {
        return userRepository.findByTimezoneAndTenantId(timezone, tenantId);
    }
    
    @Override
    public List<User> findByLanguageAndTenantId(String language, UUID tenantId) {
        return userRepository.findByLanguageAndTenantId(language, tenantId);
    }
    
    @Override
    public boolean isEmailAvailable(String email, UUID tenantId) {
        return !userRepository.existsByEmailAndTenantId(email, tenantId);
    }
    
    @Override
    public boolean isUsernameAvailable(String username, UUID tenantId) {
        return !userRepository.existsByUsernameAndTenantId(username, tenantId);
    }
    
    @Override
    public Optional<User> authenticateUser(String email, String password, UUID tenantId) {
        Optional<User> userOpt = findByEmailAndTenantId(email, tenantId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
    
    @Override
    public User changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = findByIdOrThrow(userId);
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new ValidationException("Current password is incorrect");
        }
        
        if (newPassword.length() < 8) {
            throw new ValidationException("New password must be at least 8 characters long");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public User resetPassword(UUID userId, String newPassword) {
        User user = findByIdOrThrow(userId);
        
        if (newPassword.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters long");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public User activateUser(UUID userId) {
        User user = findByIdOrThrow(userId);
        user.setStatus("active");
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public User deactivateUser(UUID userId, String reason) {
        User user = findByIdOrThrow(userId);
        user.setStatus("inactive");
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public User suspendUser(UUID userId, String reason, LocalDateTime suspensionEndDate) {
        User user = findByIdOrThrow(userId);
        user.setStatus("suspended");
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public User reactivateUser(UUID userId) {
        User user = findByIdOrThrow(userId);
        user.setStatus("active");
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public User updateUserRole(UUID userId, UserRole newRole) {
        User user = findByIdOrThrow(userId);
        user.setRole(newRole);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public User updateUserDepartment(UUID userId, String newDepartment) {
        User user = findByIdOrThrow(userId);
        user.setDepartment(newDepartment);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public User updateWorkingHours(UUID userId, String workingHours) {
        User user = findByIdOrThrow(userId);
        user.setWorkingHours(workingHours);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public User updateTimezone(UUID userId, String timezone) {
        User user = findByIdOrThrow(userId);
        user.setTimezone(timezone);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public User updateLanguagePreferences(UUID userId, String language) {
        User user = findByIdOrThrow(userId);
        user.setLanguage(language);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public User updateNotificationSettings(UUID userId, String notificationSettings) {
        User user = findByIdOrThrow(userId);
        user.setNotificationSettings(notificationSettings);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public User updateProfilePicture(UUID userId, String pictureUrl) {
        User user = findByIdOrThrow(userId);
        user.setProfilePictureUrl(pictureUrl);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public UserPerformanceMetrics getUserPerformanceMetrics(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        User user = findByIdOrThrow(userId);
        
        // This would typically involve complex queries to calculate metrics
        // For now, returning a basic implementation
        UserPerformanceMetrics metrics = new UserPerformanceMetrics();
        metrics.setUserId(userId);
        metrics.setUserEmail(user.getEmail());
        metrics.setPeriodStart(startDate);
        metrics.setPeriodEnd(endDate);
        
        // TODO: Implement actual metrics calculation
        metrics.setTotalConversationsHandled(0);
        metrics.setTotalMessagesSent(0);
        metrics.setTotalMessagesReceived(0);
        metrics.setAverageResponseTimeMinutes(0.0);
        metrics.setTotalResolvedConversations(0);
        metrics.setTotalEscalatedConversations(0);
        metrics.setCustomerSatisfactionScore(0.0);
        metrics.setTotalWorkingHours(0);
        metrics.setEfficiencyRating(0.0);
        metrics.setTotalTicketsClosed(0);
        metrics.setFirstResponseTimeMinutes(0.0);
        metrics.setTotalFollowUps(0);
        metrics.setPerformanceGrade("N/A");
        
        return metrics;
    }
    
    @Override
    public double getUserSatisfactionScore(UUID userId) {
        // TODO: Implement satisfaction score calculation
        return 0.0;
    }
    
    @Override
    public UserWorkingHours getUserWorkingHours(UUID userId) {
        // TODO: Implement working hours calculation
        return null;
    }
    
    @Override
    public boolean isUserAvailable(UUID userId) {
        // TODO: Implement availability check
        return true;
    }
    
    @Override
    public boolean isUserAvailableAt(UUID userId, LocalDateTime dateTime) {
        // TODO: Implement availability check for specific time
        return true;
    }
    
    @Override
    public LocalDateTime getUserNextAvailableTime(UUID userId) {
        // TODO: Implement next available time calculation
        return LocalDateTime.now().plusHours(1);
    }
    
    @Override
    public User updateLastActivity(UUID userId) {
        User user = findByIdOrThrow(userId);
        user.setLastActivityAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    @Override
    public List<UserActivityLog> getUserActivityLog(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        // TODO: Implement activity log retrieval
        return List.of();
    }
    
    @Override
    public Page<User> searchUsers(String query, UserRole role, String department, String status, 
                                UUID tenantId, Pageable pageable) {
        // TODO: Implement advanced search with specifications
        return userRepository.findAll(pageable);
    }
    
    @Override
    public UserDashboardData getUserDashboardData(UUID userId) {
        // TODO: Implement dashboard data
        return null;
    }
    
    @Override
    public List<User> bulkUpdateUserStatus(List<UUID> userIds, String newStatus) {
        List<User> users = userRepository.findAllById(userIds);
        users.forEach(user -> {
            user.setStatus(newStatus);
            user.setUpdatedAt(LocalDateTime.now());
        });
        
        return userRepository.saveAll(users);
    }
    
    @Override
    public List<User> bulkUpdateUserRole(List<UUID> userIds, UserRole newRole) {
        List<User> users = userRepository.findAllById(userIds);
        users.forEach(user -> {
            user.setRole(newRole);
            user.setUpdatedAt(LocalDateTime.now());
        });
        
        return userRepository.saveAll(users);
    }
    
    @Override
    public List<User> bulkUpdateUserDepartment(List<UUID> userIds, String newDepartment) {
        List<User> users = userRepository.findAllById(userIds);
        users.forEach(user -> {
            user.setDepartment(newDepartment);
            user.setUpdatedAt(LocalDateTime.now());
        });
        
        return userRepository.saveAll(users);
    }
    
    @Override
    public UserStatistics getUserStatistics(UUID tenantId) {
        // TODO: Implement user statistics
        return null;
    }
    
    @Override
    public UserOnboardingProgress getUserOnboardingProgress(UUID userId) {
        // TODO: Implement onboarding progress
        return null;
    }
    
    @Override
    public User updateOnboardingProgress(UUID userId, String step, boolean completed) {
        // TODO: Implement onboarding progress update
        return findByIdOrThrow(userId);
    }
    
    @Override
    public List<TrainingRecommendation> getUserTrainingRecommendations(UUID userId) {
        // TODO: Implement training recommendations
        return List.of();
    }
    
    @Override
    public User recordTrainingCompletion(UUID userId, String trainingType, String trainingId) {
        // TODO: Implement training completion recording
        return findByIdOrThrow(userId);
    }
}
