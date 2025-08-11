package com.weavecode.chatwoot.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weavecode.chatwoot.dto.NotificationMessage;
import com.weavecode.chatwoot.enums.NotificationType;
import com.weavecode.chatwoot.service.NotificationService;
import com.weavecode.chatwoot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class NotificationServiceImpl implements NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private static final String NOTIFICATION_KEY_PREFIX = "notification:";
    private static final String USER_NOTIFICATIONS_KEY_PREFIX = "user_notifications:";
    private static final String UNREAD_COUNT_KEY_PREFIX = "unread_count:";
    private static final int NOTIFICATION_TTL_DAYS = 30;

    @Override
    public void sendNotificationToUser(UUID tenantId, UUID userId, NotificationType type, String title, String message) {
        try {
            NotificationMessage notification = new NotificationMessage(type, title, message, tenantId, userId);
            
            // Store in Redis
            storeNotification(notification);
            
            // Send via WebSocket
            sendWebSocketNotification(userId, notification);
            
            // Update unread count
            incrementUnreadCount(tenantId, userId);
            
            logger.info("Notification sent to user {}: {}", userId, title);
            
        } catch (Exception e) {
            logger.error("Error sending notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    @Override
    public void sendNotificationToTenant(UUID tenantId, NotificationType type, String title, String message) {
        try {
            // Get all users in tenant
            List<UUID> userIds = userService.getAllUserIdsInTenant(tenantId);
            
            for (UUID userId : userIds) {
                sendNotificationToUser(tenantId, userId, type, title, message);
            }
            
            logger.info("Notification sent to all users in tenant {}: {}", tenantId, title);
            
        } catch (Exception e) {
            logger.error("Error sending notification to tenant {}: {}", tenantId, e.getMessage(), e);
        }
    }

    @Override
    public void sendNotificationToRole(UUID tenantId, String role, NotificationType type, String title, String message) {
        try {
            // Get users with specific role in tenant
            List<UUID> userIds = userService.getUserIdsByRoleInTenant(tenantId, role);
            
            for (UUID userId : userIds) {
                sendNotificationToUser(tenantId, userId, type, title, message);
            }
            
            logger.info("Notification sent to users with role {} in tenant {}: {}", role, tenantId, title);
            
        } catch (Exception e) {
            logger.error("Error sending notification to role {} in tenant {}: {}", role, tenantId, e.getMessage(), e);
        }
    }

    @Override
    public void sendNotificationToUsers(UUID tenantId, List<UUID> userIds, NotificationType type, String title, String message) {
        try {
            for (UUID userId : userIds) {
                sendNotificationToUser(tenantId, userId, type, title, message);
            }
            
            logger.info("Notification sent to {} users in tenant {}: {}", userIds.size(), tenantId, title);
            
        } catch (Exception e) {
            logger.error("Error sending notification to users in tenant {}: {}", tenantId, e.getMessage(), e);
        }
    }

    @Override
    public void markNotificationAsRead(UUID notificationId, UUID userId) {
        try {
            String key = NOTIFICATION_KEY_PREFIX + notificationId;
            NotificationMessage notification = (NotificationMessage) redisTemplate.opsForValue().get(key);
            
            if (notification != null && notification.getUserId().equals(userId)) {
                notification.markAsRead();
                redisTemplate.opsForValue().set(key, notification, NOTIFICATION_TTL_DAYS, TimeUnit.DAYS);
                
                // Decrement unread count
                String unreadKey = UNREAD_COUNT_KEY_PREFIX + notification.getTenantId() + ":" + userId;
                redisTemplate.opsForValue().decrement(unreadKey);
                
                logger.info("Notification {} marked as read by user {}", notificationId, userId);
            }
            
        } catch (Exception e) {
            logger.error("Error marking notification {} as read: {}", notificationId, e.getMessage(), e);
        }
    }

    @Override
    public void markAllNotificationsAsRead(UUID tenantId, UUID userId) {
        try {
            String userNotificationsKey = USER_NOTIFICATIONS_KEY_PREFIX + tenantId + ":" + userId;
            List<Object> notifications = redisTemplate.opsForList().range(userNotificationsKey, 0, -1);
            
            if (notifications != null) {
                for (Object obj : notifications) {
                    if (obj instanceof NotificationMessage) {
                        NotificationMessage notification = (NotificationMessage) obj;
                        if (!notification.isRead()) {
                            markNotificationAsRead(notification.getId(), userId);
                        }
                    }
                }
            }
            
            // Reset unread count
            String unreadKey = UNREAD_COUNT_KEY_PREFIX + tenantId + ":" + userId;
            redisTemplate.delete(unreadKey);
            
            logger.info("All notifications marked as read for user {} in tenant {}", userId, tenantId);
            
        } catch (Exception e) {
            logger.error("Error marking all notifications as read for user {}: {}", userId, e.getMessage(), e);
        }
    }

    @Override
    public List<NotificationMessage> getUnreadNotifications(UUID tenantId, UUID userId) {
        try {
            String userNotificationsKey = USER_NOTIFICATIONS_KEY_PREFIX + tenantId + ":" + userId;
            List<Object> notifications = redisTemplate.opsForList().range(userNotificationsKey, 0, -1);
            
            if (notifications != null) {
                return notifications.stream()
                        .filter(obj -> obj instanceof NotificationMessage)
                        .map(obj -> (NotificationMessage) obj)
                        .filter(notification -> !notification.isRead())
                        .sorted((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()))
                        .toList();
            }
            
        } catch (Exception e) {
            logger.error("Error getting unread notifications for user {}: {}", userId, e.getMessage(), e);
        }
        
        return List.of();
    }

    @Override
    public List<NotificationMessage> getNotifications(UUID tenantId, UUID userId, int page, int size) {
        try {
            String userNotificationsKey = USER_NOTIFICATIONS_KEY_PREFIX + tenantId + ":" + userId;
            int start = page * size;
            int end = start + size - 1;
            
            List<Object> notifications = redisTemplate.opsForList().range(userNotificationsKey, start, end);
            
            if (notifications != null) {
                return notifications.stream()
                        .filter(obj -> obj instanceof NotificationMessage)
                        .map(obj -> (NotificationMessage) obj)
                        .sorted((n1, n2) -> n2.getCreatedAt().compareTo(n1.getCreatedAt()))
                        .toList();
            }
            
        } catch (Exception e) {
            logger.error("Error getting notifications for user {}: {}", userId, e.getMessage(), e);
        }
        
        return List.of();
    }

    @Override
    public void deleteNotification(UUID notificationId, UUID userId) {
        try {
            String key = NOTIFICATION_KEY_PREFIX + notificationId;
            NotificationMessage notification = (NotificationMessage) redisTemplate.opsForValue().get(key);
            
            if (notification != null && notification.getUserId().equals(userId)) {
                // Remove from Redis
                redisTemplate.delete(key);
                
                // Remove from user notifications list
                String userNotificationsKey = USER_NOTIFICATIONS_KEY_PREFIX + notification.getTenantId() + ":" + userId;
                redisTemplate.opsForList().remove(userNotificationsKey, 0, notification);
                
                // Update unread count if notification was unread
                if (!notification.isRead()) {
                    String unreadKey = UNREAD_COUNT_KEY_PREFIX + notification.getTenantId() + ":" + userId;
                    redisTemplate.opsForValue().decrement(unreadKey);
                }
                
                logger.info("Notification {} deleted by user {}", notificationId, userId);
            }
            
        } catch (Exception e) {
            logger.error("Error deleting notification {}: {}", notificationId, e.getMessage(), e);
        }
    }

    @Override
    public void deleteAllNotifications(UUID tenantId, UUID userId) {
        try {
            String userNotificationsKey = USER_NOTIFICATIONS_KEY_PREFIX + tenantId + ":" + userId;
            List<Object> notifications = redisTemplate.opsForList().range(userNotificationsKey, 0, -1);
            
            if (notifications != null) {
                for (Object obj : notifications) {
                    if (obj instanceof NotificationMessage) {
                        NotificationMessage notification = (NotificationMessage) obj;
                        redisTemplate.delete(NOTIFICATION_KEY_PREFIX + notification.getId());
                    }
                }
                
                // Clear user notifications list
                redisTemplate.delete(userNotificationsKey);
                
                // Clear unread count
                String unreadKey = UNREAD_COUNT_KEY_PREFIX + tenantId + ":" + userId;
                redisTemplate.delete(unreadKey);
                
                logger.info("All notifications deleted for user {} in tenant {}", userId, tenantId);
            }
            
        } catch (Exception e) {
            logger.error("Error deleting all notifications for user {}: {}", userId, e.getMessage(), e);
        }
    }

    @Override
    public long getNotificationCount(UUID tenantId, UUID userId) {
        try {
            String userNotificationsKey = USER_NOTIFICATIONS_KEY_PREFIX + tenantId + ":" + userId;
            Long count = redisTemplate.opsForList().size(userNotificationsKey);
            return count != null ? count : 0;
            
        } catch (Exception e) {
            logger.error("Error getting notification count for user {}: {}", userId, e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public long getUnreadNotificationCount(UUID tenantId, UUID userId) {
        try {
            String unreadKey = UNREAD_COUNT_KEY_PREFIX + tenantId + ":" + userId;
            Object count = redisTemplate.opsForValue().get(unreadKey);
            return count != null ? Long.parseLong(count.toString()) : 0;
            
        } catch (Exception e) {
            logger.error("Error getting unread notification count for user {}: {}", userId, e.getMessage(), e);
            return 0;
        }
    }

    // Private helper methods
    private void storeNotification(NotificationMessage notification) {
        try {
            // Store notification details
            String notificationKey = NOTIFICATION_KEY_PREFIX + notification.getId();
            redisTemplate.opsForValue().set(notificationKey, notification, NOTIFICATION_TTL_DAYS, TimeUnit.DAYS);
            
            // Add to user notifications list
            String userNotificationsKey = USER_NOTIFICATIONS_KEY_PREFIX + notification.getTenantId() + ":" + notification.getUserId();
            redisTemplate.opsForList().leftPush(userNotificationsKey, notification);
            
            // Set TTL for user notifications list
            redisTemplate.expire(userNotificationsKey, NOTIFICATION_TTL_DAYS, TimeUnit.DAYS);
            
        } catch (Exception e) {
            logger.error("Error storing notification: {}", e.getMessage(), e);
        }
    }

    private void sendWebSocketNotification(UUID userId, NotificationMessage notification) {
        try {
            String destination = "/user/" + userId + "/queue/notifications";
            messagingTemplate.convertAndSendToUser(userId.toString(), "/queue/notifications", notification);
            
        } catch (Exception e) {
            logger.error("Error sending WebSocket notification: {}", e.getMessage(), e);
        }
    }

    private void incrementUnreadCount(UUID tenantId, UUID userId) {
        try {
            String unreadKey = UNREAD_COUNT_KEY_PREFIX + tenantId + ":" + userId;
            redisTemplate.opsForValue().increment(unreadKey);
            redisTemplate.expire(unreadKey, NOTIFICATION_TTL_DAYS, TimeUnit.DAYS);
            
        } catch (Exception e) {
            logger.error("Error incrementing unread count: {}", e.getMessage(), e);
        }
    }
}
