package com.weavecode.chatwoot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.weavecode.chatwoot.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class NotificationMessage {
    private UUID id;
    private NotificationType type;
    private String title;
    private String message;
    private UUID tenantId;
    private UUID userId;
    private UUID targetId; // ID of the entity this notification is about
    private String targetType; // Type of entity (conversation, message, etc.)
    private Map<String, Object> metadata;
    private boolean read;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime readAt;

    // Constructors
    public NotificationMessage() {}

    public NotificationMessage(NotificationType type, String title, String message, UUID tenantId, UUID userId) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.title = title;
        this.message = message;
        this.tenantId = tenantId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    // Utility methods
    public void markAsRead() {
        this.read = true;
        this.readAt = LocalDateTime.now();
    }

    public void addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new java.util.HashMap<>();
        }
        this.metadata.put(key, value);
    }
}
