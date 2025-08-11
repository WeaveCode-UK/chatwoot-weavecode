package com.weavecode.chatwoot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Message entity representing individual messages in conversations
 * Implements enterprise-grade security patterns and audit trails
 * 
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    @NotNull(message = "Conversation ID is required")
    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;
    
    @NotNull(message = "Sender ID is required")
    @Column(name = "sender_id", nullable = false)
    private UUID senderId;
    
    @Column(name = "sender_type", length = 20, nullable = false)
    private String senderType = "user"; // user, agent, bot, system
    
    @NotBlank(message = "Message content is required")
    @Size(max = 4000, message = "Message content cannot exceed 4000 characters")
    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;
    
    @Column(name = "content_type", length = 50, default = "text")
    private String contentType = "text"; // text, image, file, audio, video, location
    
    @Column(name = "attachments", columnDefinition = "jsonb")
    private String attachments; // JSON string for file attachments
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // JSON string for additional message data
    
    @Column(name = "message_type", length = 20, default = "incoming")
    private String messageType = "incoming"; // incoming, outgoing, activity
    
    @Column(name = "delivery_status", length = 20, default = "sent")
    private String deliveryStatus = "sent"; // sent, delivered, read, failed
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "read_by")
    private UUID readBy;
    
    @Column(name = "failed_reason", length = 500)
    private String failedReason;
    
    @Column(name = "retry_count")
    private Integer retryCount = 0;
    
    @Column(name = "max_retries")
    private Integer maxRetries = 3;
    
    @Column(name = "is_edited")
    private Boolean isEdited = false;
    
    @Column(name = "edited_at")
    private LocalDateTime editedAt;
    
    @Column(name = "edited_by")
    private UUID editedBy;
    
    @Column(name = "original_content", columnDefinition = "text")
    private String originalContent;
    
    @Column(name = "edit_reason", length = 500)
    private String editReason;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @Column(name = "deleted_by")
    private UUID deletedBy;
    
    @Column(name = "delete_reason", length = 500)
    private String deleteReason;
    
    @Column(name = "parent_message_id")
    private UUID parentMessageId; // For threaded conversations
    
    @Column(name = "thread_id")
    private UUID threadId; // For grouping related messages
    
    @Column(name = "priority", length = 20)
    private String priority = "normal"; // low, normal, high, urgent
    
    @Column(name = "tags", length = 500)
    private String tags; // Comma-separated tags for categorisation
    
    @Column(name = "sentiment_score")
    private Double sentimentScore; // AI-generated sentiment analysis (-1 to 1)
    
    @Column(name = "intent", length = 100)
    private String intent; // AI-detected intent
    
    @Column(name = "confidence_score")
    private Double confidenceScore; // AI confidence in intent detection
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public Message() {}
    
    public Message(UUID conversationId, UUID senderId, String content) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.content = content;
        this.senderType = "user";
        this.contentType = "text";
        this.messageType = "incoming";
        this.deliveryStatus = "sent";
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(UUID conversationId) {
        this.conversationId = conversationId;
    }
    
    public UUID getSenderId() {
        return senderId;
    }
    
    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }
    
    public String getSenderType() {
        return senderType;
    }
    
    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public String getAttachments() {
        return attachments;
    }
    
    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public String getMessageType() {
        return messageType;
    }
    
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    
    public String getDeliveryStatus() {
        return deliveryStatus;
    }
    
    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
        updateDeliveryTimestamps(deliveryStatus);
    }
    
    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }
    
    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }
    
    public LocalDateTime getReadAt() {
        return readAt;
    }
    
    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
    
    public UUID getReadBy() {
        return readBy;
    }
    
    public void setReadBy(UUID readBy) {
        this.readBy = readBy;
    }
    
    public String getFailedReason() {
        return failedReason;
    }
    
    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public Integer getMaxRetries() {
        return maxRetries;
    }
    
    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }
    
    public Boolean getIsEdited() {
        return isEdited;
    }
    
    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
    }
    
    public LocalDateTime getEditedAt() {
        return editedAt;
    }
    
    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }
    
    public UUID getEditedBy() {
        return editedBy;
    }
    
    public void setEditedBy(UUID editedBy) {
        this.editedBy = editedBy;
    }
    
    public String getOriginalContent() {
        return originalContent;
    }
    
    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }
    
    public String getEditReason() {
        return editReason;
    }
    
    public void setEditReason(String editReason) {
        this.editReason = editReason;
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    public UUID getDeletedBy() {
        return deletedBy;
    }
    
    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }
    
    public String getDeleteReason() {
        return deleteReason;
    }
    
    public void setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
    }
    
    public UUID getParentMessageId() {
        return parentMessageId;
    }
    
    public void setParentMessageId(UUID parentMessageId) {
        this.parentMessageId = parentMessageId;
    }
    
    public UUID getThreadId() {
        return threadId;
    }
    
    public void setThreadId(UUID threadId) {
        this.threadId = threadId;
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public Double getSentimentScore() {
        return sentimentScore;
    }
    
    public void setSentimentScore(Double sentimentScore) {
        if (sentimentScore != null && (sentimentScore < -1.0 || sentimentScore > 1.0)) {
            throw new IllegalArgumentException("Sentiment score must be between -1.0 and 1.0");
        }
        this.sentimentScore = sentimentScore;
    }
    
    public String getIntent() {
        return intent;
    }
    
    public void setIntent(String intent) {
        this.intent = intent;
    }
    
    public Double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(Double confidenceScore) {
        if (confidenceScore != null && (confidenceScore < 0.0 || confidenceScore > 1.0)) {
            throw new IllegalArgumentException("Confidence score must be between 0.0 and 1.0");
        }
        this.confidenceScore = confidenceScore;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Business Logic Methods with Security Considerations
    
    /**
     * Update delivery status and related timestamps
     */
    private void updateDeliveryTimestamps(String newStatus) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (newStatus) {
            case "delivered":
                if (this.deliveredAt == null) {
                    this.deliveredAt = now;
                }
                break;
            case "read":
                if (this.readAt == null) {
                    this.readAt = now;
                }
                break;
            case "failed":
                this.retryCount++;
                break;
        }
    }
    
    /**
     * Mark message as delivered
     */
    public void markAsDelivered() {
        this.deliveryStatus = "delivered";
        this.deliveredAt = LocalDateTime.now();
    }
    
    /**
     * Mark message as read
     */
    public void markAsRead(UUID readBy) {
        this.deliveryStatus = "read";
        this.readAt = LocalDateTime.now();
        this.readBy = readBy;
    }
    
    /**
     * Mark message as failed
     */
    public void markAsFailed(String reason) {
        this.deliveryStatus = "failed";
        this.failedReason = reason;
        this.retryCount++;
    }
    
    /**
     * Check if message can be retried
     */
    public boolean canRetry() {
        return this.retryCount < this.maxRetries;
    }
    
    /**
     * Edit message with proper audit trail
     */
    public void edit(String newContent, UUID editedBy, String reason) {
        if (this.isDeleted) {
            throw new IllegalStateException("Cannot edit deleted message");
        }
        
        this.originalContent = this.content;
        this.content = newContent;
        this.isEdited = true;
        this.editedAt = LocalDateTime.now();
        this.editedBy = editedBy;
        this.editReason = reason;
    }
    
    /**
     * Delete message with proper audit trail
     */
    public void delete(UUID deletedBy, String reason) {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
        this.deleteReason = reason;
    }
    
    /**
     * Restore deleted message
     */
    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
        this.deleteReason = null;
    }
    
    /**
     * Add tag to message (with deduplication)
     */
    public void addTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return;
        }
        
        String cleanTag = tag.trim();
        if (this.tags == null || this.tags.isEmpty()) {
            this.tags = cleanTag;
        } else {
            String[] existingTags = this.tags.split(",");
            for (String existingTag : existingTags) {
                if (existingTag.trim().equals(cleanTag)) {
                    return; // Tag already exists
                }
            }
            this.tags = this.tags + "," + cleanTag;
        }
    }
    
    /**
     * Remove tag from message
     */
    public void removeTag(String tag) {
        if (tag == null || this.tags == null || this.tags.isEmpty()) {
            return;
        }
        
        String cleanTag = tag.trim();
        String[] existingTags = this.tags.split(",");
        StringBuilder newTags = new StringBuilder();
        
        for (String existingTag : existingTags) {
            if (!existingTag.trim().equals(cleanTag)) {
                if (newTags.length() > 0) {
                    newTags.append(",");
                }
                newTags.append(existingTag.trim());
            }
        }
        
        this.tags = newTags.toString();
    }
    
    /**
     * Check if message has specific tag
     */
    public boolean hasTag(String tag) {
        if (tag == null || this.tags == null || this.tags.isEmpty()) {
            return false;
        }
        
        String cleanTag = tag.trim();
        String[] existingTags = this.tags.split(",");
        
        for (String existingTag : existingTags) {
            if (existingTag.trim().equals(cleanTag)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get message age in minutes
     */
    public long getAgeInMinutes() {
        if (createdAt == null) {
            return 0;
        }
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toMinutes();
    }
    
    /**
     * Check if message is from agent
     */
    public boolean isFromAgent() {
        return "agent".equals(this.senderType);
    }
    
    /**
     * Check if message is from bot
     */
    public boolean isFromBot() {
        return "bot".equals(this.senderType);
    }
    
    /**
     * Check if message is from system
     */
    public boolean isFromSystem() {
        return "system".equals(this.senderType);
    }
    
    /**
     * Check if message is from user
     */
    public boolean isFromUser() {
        return "user".equals(this.senderType);
    }
    
    /**
     * Check if message is incoming
     */
    public boolean isIncoming() {
        return "incoming".equals(this.messageType);
    }
    
    /**
     * Check if message is outgoing
     */
    public boolean isOutgoing() {
        return "outgoing".equals(this.messageType);
    }
    
    /**
     * Check if message is activity
     */
    public boolean isActivity() {
        return "activity".equals(this.messageType);
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", conversationId=" + conversationId +
                ", senderId=" + senderId +
                ", senderType='" + senderType + '\'' +
                ", contentType='" + contentType + '\'' +
                ", messageType='" + messageType + '\'' +
                ", deliveryStatus='" + deliveryStatus + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
