package com.weavecode.chatwoot.controller;

import com.weavecode.chatwoot.dto.UserPerformanceMetrics;
import com.weavecode.chatwoot.entity.User;
import com.weavecode.chatwoot.enums.UserRole;
import com.weavecode.chatwoot.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#tenantId)")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam UUID tenantId,
            Pageable pageable) {
        try {
            // This would need to be implemented in UserService to filter by tenant
            Page<User> users = userService.findAll(pageable);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error fetching users for tenant: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#id) or @securityService.isCurrentUser(#id)")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        try {
            return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching user with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#user.tenantId)")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        try {
            User createdUser = userService.create(user);
            logger.info("User created successfully: {}", createdUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            logger.error("Error creating user", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#id) or @securityService.isCurrentUser(#id)")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @Valid @RequestBody User user) {
        try {
            user.setId(id);
            User updatedUser = userService.update(id, user);
            logger.info("User updated successfully: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Error updating user with id: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#id) or @securityService.isCurrentUser(#id)")
    public ResponseEntity<User> partialUpdateUser(@PathVariable UUID id, @RequestBody User user) {
        try {
            User updatedUser = userService.partialUpdate(id, user);
            logger.info("User partially updated successfully: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            logger.error("Error partially updating user with id: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#id)")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        try {
            userService.deleteById(id);
            logger.info("User deleted successfully: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting user with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/{id}/change-password")
    @PreAuthorize("@securityService.isCurrentUser(#id)")
    public ResponseEntity<User> changePassword(
            @PathVariable UUID id,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        try {
            User user = userService.changePassword(id, currentPassword, newPassword);
            logger.info("Password changed successfully for user: {}", id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error changing password for user: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#id)")
    public ResponseEntity<User> activateUser(@PathVariable UUID id) {
        try {
            User user = userService.activateUser(id);
            logger.info("User activated successfully: {}", id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error activating user: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/update-role")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#id)")
    public ResponseEntity<User> updateUserRole(@PathVariable UUID id, @RequestParam UserRole newRole) {
        try {
            User user = userService.updateUserRole(id, newRole);
            logger.info("Role updated for user: {} to: {}", id, newRole);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error updating role for user: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}/performance")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#id) or @securityService.isCurrentUser(#id)")
    public ResponseEntity<UserPerformanceMetrics> getUserPerformanceMetrics(
            @PathVariable UUID id,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        try {
            UserPerformanceMetrics metrics = userService.getUserPerformanceMetrics(id, startDate, endDate);
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            logger.error("Error fetching performance metrics for user: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/bulk-update-status")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#tenantId)")
    public ResponseEntity<List<User>> bulkUpdateUserStatus(
            @RequestParam List<UUID> userIds,
            @RequestParam String newStatus,
            @RequestParam UUID tenantId) {
        try {
            List<User> users = userService.bulkUpdateUserStatus(userIds, newStatus);
            logger.info("Bulk status update completed for {} users", userIds.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error in bulk status update", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#tenantId)")
    public ResponseEntity<List<User>> getUsersByRole(
            @PathVariable UserRole role,
            @RequestParam UUID tenantId) {
        try {
            List<User> users = userService.findByRoleAndTenantId(role, tenantId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error fetching users by role: {} for tenant: {}", role, tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/by-tenant/{tenantId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#tenantId)")
    public ResponseEntity<Page<User>> getUsersByTenant(
            @PathVariable UUID tenantId,
            Pageable pageable) {
        try {
            // This would need to be implemented in UserService to filter by tenant
            Page<User> users = userService.findAll(pageable);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Error fetching users for tenant: {}", tenantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
