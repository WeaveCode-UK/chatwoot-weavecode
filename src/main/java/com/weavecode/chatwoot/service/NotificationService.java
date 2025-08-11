package com.weavecode.chatwoot.service;

import com.weavecode.chatwoot.dto.NotificationMessage;
import com.weavecode.chatwoot.enums.NotificationType;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    
    /**
     * Send notification to a specific user
     */
    void sendNotificationToUser(UUID tenantId, UUID userId, NotificationType type, String title, String message);
    
    /**
     * Send notification to all users in a tenant
     */
    void sendNotificationToTenant(UUID tenantId, NotificationType type, String title, String message);
    
    /**
     * Send notification to users with specific role in a tenant
     */
    void sendNotificationToRole(UUID tenantId, String role, NotificationType type, String title, String message);
    
    /**
     * Send notification to multiple specific users
     */
    void sendNotificationToUsers(UUID tenantId, List<UUID> userIds, NotificationType type, String title, String message);
    
    /**
     * Mark notification as read
     */
    void markNotificationAsRead(UUID notificationId, UUID userId);
    
    /**
     * Mark all notifications as read for a user
     */
    void markAllNotificationsAsRead(UUID tenantId, UUID userId);
    
    /**
     * Get unread notifications for a user
     */
    List<NotificationMessage> getUnreadNotifications(UUID tenantId, UUID userId);
    
    /**
     * Get all notifications for a user with pagination
     */
    List<NotificationMessage> getNotifications(UUID tenantId, UUID userId, int page, int size);
    
    /**
     * Delete notification
     */
    void deleteNotification(UUID notificationId, UUID userId);
    
    /**
     * Delete all notifications for a user
     */
    void deleteAllNotifications(UUID tenantId, UUID userId);
    
    /**
     * Get notification count for a user
     */
    long getNotificationCount(UUID tenantId, UUID userId);
    
    /**
     * Get unread notification count for a user
     */
    long getUnreadNotificationCount(UUID tenantId, UUID userId);
}
