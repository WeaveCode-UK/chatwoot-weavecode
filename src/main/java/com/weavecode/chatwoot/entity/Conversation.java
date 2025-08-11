package com.weavecode.chatwoot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Conversation entity representing a chat conversation
 * 
 * This entity manages:
 * - Conversation metadata (title, status, type)
 * - Participant relationships
 * - Message associations
 * - Multi-tenant isolation
 * - Timestamps and audit information
 * 
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Entity
@Table(name = "conversations", indexes = {
    @Index(name = "idx_conversations_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_conversations_status", columnList = "status"),
    @Index(name = "idx_conversations_created_at", columnList = "created_at"),
    @Index(name = "idx_conversations_updated_at", columnList = "updated_at")
})
public class Conversation {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConversationStatus status = ConversationStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ConversationType type = ConversationType.DIRECT;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "priority")
    private Integer priority = 1; // 1=Low, 2=Medium, 3=High, 4=Urgent

    @Column(name = "assigned_agent_id")
    private UUID assignedAgentId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "source", length = 100)
    private String source; // web, mobile, email, api, etc.

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Additional conversation metadata

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "sla_due_at")
    private LocalDateTime slaDueAt;

    @Column(name = "tags", length = 500)
    private String tags; // Comma-separated tags

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    // Relationships
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Message> messages = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    @JsonIgnore
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id", insertable = false, updatable = false)
    @JsonIgnore
    private User assignedAgent;

    // Enums
    public enum ConversationStatus {
        OPEN,           // Conversation is active and open
        WAITING,        // Waiting for customer response
        PENDING,        // Pending agent response
        RESOLVED,       // Conversation resolved
        CLOSED,         // Conversation closed
        ARCHIVED        // Conversation archived
    }

    public enum ConversationType {
        DIRECT,         // Direct conversation
        GROUP,          // Group conversation
        SUPPORT,        // Support ticket
        SALES,          // Sales inquiry
        FEEDBACK        // Feedback conversation
    }

    // Constructors
    public Conversation() {}

    public Conversation(String tenantId, String title, ConversationType type) {
        this.tenantId = tenantId;
        this.title = title;
        this.type = type;
        this.status = ConversationStatus.OPEN;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(ConversationStatus status) {
        this.status = status;
        if (status == ConversationStatus.RESOLVED) {
            this.resolvedAt = LocalDateTime.now();
        }
    }

    public ConversationType getType() {
        return type;
    }

    public void setType(ConversationType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public UUID getAssignedAgentId() {
        return assignedAgentId;
    }

    public void setAssignedAgentId(UUID assignedAgentId) {
        this.assignedAgentId = assignedAgentId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public LocalDateTime getSlaDueAt() {
        return slaDueAt;
    }

    public void setSlaDueAt(LocalDateTime slaDueAt) {
        this.slaDueAt = slaDueAt;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
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

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            this.customerId = customer.getId();
        }
    }

    public User getAssignedAgent() {
        return assignedAgent;
    }

    public void setAssignedAgent(User assignedAgent) {
        this.assignedAgent = assignedAgent;
        if (assignedAgent != null) {
            this.assignedAgentId = assignedAgent.getId();
        }
    }

    // Business methods
    public void addMessage(Message message) {
        messages.add(message);
        message.setConversation(this);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        message.setConversation(null);
    }

    public boolean isActive() {
        return status == ConversationStatus.OPEN || 
               status == ConversationStatus.WAITING || 
               status == ConversationStatus.PENDING;
    }

    public boolean isResolved() {
        return status == ConversationStatus.RESOLVED;
    }

    public boolean isClosed() {
        return status == ConversationStatus.CLOSED || 
               status == ConversationStatus.ARCHIVED;
    }

    public void resolve() {
        this.status = ConversationStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
    }

    public void close() {
        this.status = ConversationStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }

    public void archive() {
        this.status = ConversationStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isOverdue() {
        return slaDueAt != null && LocalDateTime.now().isAfter(slaDueAt) && isActive();
    }

    public long getMessageCount() {
        return messages.size();
    }

    public Message getLastMessage() {
        return messages.stream()
            .max((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()))
            .orElse(null);
    }

    public LocalDateTime getLastActivity() {
        Message lastMessage = getLastMessage();
        return lastMessage != null ? lastMessage.getCreatedAt() : updatedAt;
    }

    // Utility methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id=" + id +
                ", tenantId='" + tenantId + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", priority=" + priority +
                ", createdAt=" + createdAt +
                '}';
    }
}
