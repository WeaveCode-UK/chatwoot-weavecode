package com.weavecode.chatwoot.controller;

import com.weavecode.chatwoot.dto.NotificationMessage;
import com.weavecode.chatwoot.enums.NotificationType;
import com.weavecode.chatwoot.service.NotificationService;
import com.weavecode.chatwoot.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private SecurityService securityService;

    /**
     * Get unread notifications for current user
     */
    @GetMapping("/unread")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationMessage>> getUnreadNotifications() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            UUID userId = securityService.getCurrentUserId();
            
            List<NotificationMessage> notifications = notificationService.getUnreadNotifications(tenantId, userId);
            
            logger.info("Retrieved {} unread notifications for user {}", notifications.size(), userId);
            return ResponseEntity.ok(notifications);
            
        } catch (Exception e) {
            logger.error("Error getting unread notifications: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all notifications for current user with pagination
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationMessage>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            UUID userId = securityService.getCurrentUserId();
            
            List<NotificationMessage> notifications = notificationService.getNotifications(tenantId, userId, page, size);
            
            logger.info("Retrieved {} notifications for user {} (page: {}, size: {})", 
                    notifications.size(), userId, page, size);
            return ResponseEntity.ok(notifications);
            
        } catch (Exception e) {
            logger.error("Error getting notifications: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Mark notification as read
     */
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable UUID notificationId) {
        try {
            UUID userId = securityService.getCurrentUserId();
            
            notificationService.markNotificationAsRead(notificationId, userId);
            
            logger.info("Notification {} marked as read by user {}", notificationId, userId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error marking notification {} as read: {}", notificationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Mark all notifications as read for current user
     */
    @PutMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> markAllNotificationsAsRead() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            UUID userId = securityService.getCurrentUserId();
            
            notificationService.markAllNotificationsAsRead(tenantId, userId);
            
            logger.info("All notifications marked as read for user {}", userId);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error marking all notifications as read: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete notification
     */
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID notificationId) {
        try {
            UUID userId = securityService.getCurrentUserId();
            
            notificationService.deleteNotification(notificationId, userId);
            
            logger.info("Notification {} deleted by user {}", notificationId, userId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            logger.error("Error deleting notification {}: {}", notificationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete all notifications for current user
     */
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAllNotifications() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            UUID userId = securityService.getCurrentUserId();
            
            notificationService.deleteAllNotifications(tenantId, userId);
            
            logger.info("All notifications deleted for user {}", userId);
            return ResponseEntity.noContent().build();
            
        } catch (Exception e) {
            logger.error("Error deleting all notifications: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get notification count for current user
     */
    @GetMapping("/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getNotificationCount() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            UUID userId = securityService.getCurrentUserId();
            
            long count = notificationService.getNotificationCount(tenantId, userId);
            
            logger.debug("Notification count for user {}: {}", userId, count);
            return ResponseEntity.ok(count);
            
        } catch (Exception e) {
            logger.error("Error getting notification count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get unread notification count for current user
     */
    @GetMapping("/unread/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> getUnreadNotificationCount() {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            UUID userId = securityService.getCurrentUserId();
            
            long count = notificationService.getUnreadNotificationCount(tenantId, userId);
            
            logger.debug("Unread notification count for user {}: {}", userId, count);
            return ResponseEntity.ok(count);
            
        } catch (Exception e) {
            logger.error("Error getting unread notification count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Admin endpoints for sending notifications
    /**
     * Send notification to specific user (Admin only)
     */
    @PostMapping("/send/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendNotificationToUser(
            @PathVariable UUID userId,
            @RequestParam NotificationType type,
            @RequestParam String title,
            @RequestParam String message) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            notificationService.sendNotificationToUser(tenantId, userId, type, title, message);
            
            logger.info("Admin sent notification to user {}: {}", userId, title);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error sending notification to user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Send notification to all users in tenant (Admin only)
     */
    @PostMapping("/send/tenant")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendNotificationToTenant(
            @RequestParam NotificationType type,
            @RequestParam String title,
            @RequestParam String message) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            notificationService.sendNotificationToTenant(tenantId, type, title, message);
            
            logger.info("Admin sent notification to all users in tenant {}: {}", tenantId, title);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error sending notification to tenant: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Send notification to users with specific role (Admin only)
     */
    @PostMapping("/send/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> sendNotificationToRole(
            @PathVariable String role,
            @RequestParam NotificationType type,
            @RequestParam String title,
            @RequestParam String message) {
        try {
            UUID tenantId = securityService.getCurrentTenantId();
            
            notificationService.sendNotificationToRole(tenantId, role, type, title, message);
            
            logger.info("Admin sent notification to users with role {} in tenant {}: {}", role, tenantId, title);
            return ResponseEntity.ok().build();
            
        } catch (Exception e) {
            logger.error("Error sending notification to role {}: {}", role, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
