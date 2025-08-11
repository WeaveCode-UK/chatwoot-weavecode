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
 * Automation entity representing AI-powered chatbots and workflows
 * Implements enterprise-grade automation patterns and audit trails
 * 
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Entity
@Table(name = "automations")
public class Automation {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    @NotNull(message = "Tenant ID is required")
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
    
    @NotBlank(message = "Automation name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;
    
    @NotNull(message = "Automation type is required")
    @Size(max = 50, message = "Type cannot exceed 50 characters")
    @Column(name = "type", nullable = false)
    private String type = "chatbot"; // chatbot, workflow, trigger, scheduled
    
    @NotNull(message = "Automation status is required")
    @Size(max = 20, message = "Status cannot exceed 20 characters")
    @Column(name = "status", nullable = false)
    private String status = "draft"; // draft, active, paused, archived
    
    @Column(name = "priority", length = 20, default = "medium")
    private String priority = "medium"; // low, medium, high, urgent
    
    @Column(name = "category", length = 100)
    private String category; // customer_support, sales, onboarding, feedback
    
    @Column(name = "tags", length = 500)
    private String tags; // Comma-separated tags for categorisation
    
    @Column(name = "triggers", columnDefinition = "jsonb")
    private String triggers; // JSON string for automation triggers
    
    @Column(name = "conditions", columnDefinition = "jsonb")
    private String conditions; // JSON string for automation conditions
    
    @Column(name = "actions", columnDefinition = "jsonb")
    private String actions; // JSON string for automation actions
    
    @Column(name = "ai_config", columnDefinition = "jsonb")
    private String aiConfig; // JSON string for AI configuration
    
    @Column(name = "workflow_steps", columnDefinition = "jsonb")
    private String workflowSteps; // JSON string for workflow steps
    
    @Column(name = "response_templates", columnDefinition = "jsonb")
    private String responseTemplates; // JSON string for response templates
    
    @Column(name = "fallback_responses", columnDefinition = "jsonb")
    private String fallbackResponses; // JSON string for fallback responses
    
    @Column(name = "learning_data", columnDefinition = "jsonb")
    private String learningData; // JSON string for AI learning data
    
    @Column(name = "performance_metrics", columnDefinition = "jsonb")
    private String performanceMetrics; // JSON string for performance metrics
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // JSON string for additional automation data
    
    @Column(name = "is_ai_enabled")
    private Boolean isAiEnabled = true;
    
    @Column(name = "ai_model", length = 100)
    private String aiModel = "gpt-4"; // gpt-3.5-turbo, gpt-4, claude, custom
    
    @Column(name = "ai_temperature")
    private Double aiTemperature = 0.7; // AI response creativity (0.0-1.0)
    
    @Column(name = "ai_max_tokens")
    private Integer aiMaxTokens = 1000; // Maximum AI response length
    
    @Column(name = "ai_context_window")
    private Integer aiContextWindow = 4000; // AI context window size
    
    @Column(name = "ai_system_prompt", columnDefinition = "text")
    private String aiSystemPrompt; // AI system prompt for context
    
    @Column(name = "ai_user_prompt", columnDefinition = "text")
    private String aiUserPrompt; // AI user prompt template
    
    @Column(name = "ai_examples", columnDefinition = "jsonb")
    private String aiExamples; // JSON string for AI few-shot examples
    
    @Column(name = "ai_constraints", columnDefinition = "jsonb")
    private String aiConstraints; // JSON string for AI response constraints
    
    @Column(name = "sentiment_analysis")
    private Boolean sentimentAnalysis = true; // Enable sentiment analysis
    
    @Column(name = "intent_detection")
    private Boolean intentDetection = true; // Enable intent detection
    
    @Column(name = "entity_extraction")
    private Boolean entityExtraction = true; // Enable entity extraction
    
    @Column(name = "language_detection")
    private Boolean languageDetection = true; // Enable language detection
    
    @Column(name = "multilingual_support")
    private Boolean multilingualSupport = false; // Enable multilingual support
    
    @Column(name = "supported_languages", length = 500)
    private String supportedLanguages; // Comma-separated supported languages
    
    @Column(name = "conversation_memory")
    private Boolean conversationMemory = true; // Enable conversation memory
    
    @Column(name = "memory_duration_hours")
    private Integer memoryDurationHours = 24; // Conversation memory duration
    
    @Column(name = "max_conversation_length")
    private Integer maxConversationLength = 50; // Maximum conversation messages
    
    @Column(name = "escalation_threshold")
    private Integer escalationThreshold = 3; // Failed attempts before escalation
    
    @Column(name = "escalation_action", length = 100)
    private String escalationAction = "human_agent"; // Action on escalation
    
    @Column(name = "escalation_message", length = 500)
    private String escalationMessage = "I'm transferring you to a human agent.";
    
    @Column(name = "business_hours_only")
    private Boolean businessHoursOnly = false; // Only active during business hours
    
    @Column(name = "business_hours", columnDefinition = "jsonb")
    private String businessHours; // JSON string for business hours configuration
    
    @Column(name = "timezone", length = 50, default = "UTC")
    private String timezone = "UTC";
    
    @Column(name = "holiday_calendar", columnDefinition = "jsonb")
    private String holidayCalendar; // JSON string for holiday calendar
    
    @Column(name = "out_of_hours_message", length = 500)
    private String outOfHoursMessage = "We're currently closed. We'll respond during business hours.";
    
    @Column(name = "scheduling_enabled")
    private Boolean schedulingEnabled = false; // Enable appointment scheduling
    
    @Column(name = "scheduling_config", columnDefinition = "jsonb")
    private String schedulingConfig; // JSON string for scheduling configuration
    
    @Column(name = "integration_webhooks", columnDefinition = "jsonb")
    private String integrationWebhooks; // JSON string for webhook integrations
    
    @Column(name = "api_endpoints", columnDefinition = "jsonb")
    private String apiEndpoints; // JSON string for API endpoints
    
    @Column(name = "external_services", columnDefinition = "jsonb")
    private String externalServices; // JSON string for external service integrations
    
    @Column(name = "testing_enabled")
    private Boolean testingEnabled = true; // Enable testing mode
    
    @Column(name = "testing_config", columnDefinition = "jsonb")
    private String testingConfig; // JSON string for testing configuration
    
    @Column(name = "version", length = 20, default = "1.0.0")
    private String version = "1.0.0";
    
    @Column(name = "is_latest_version")
    private Boolean isLatestVersion = true;
    
    @Column(name = "parent_version_id")
    private UUID parentVersionId;
    
    @Column(name = "change_log", columnDefinition = "text")
    private String changeLog; // Version change log
    
    @Column(name = "approval_required")
    private Boolean approvalRequired = false; // Require approval for changes
    
    @Column(name = "approved_by")
    private UUID approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "approval_notes", length = 1000)
    private String approvalNotes;
    
    @Column(name = "deployment_date")
    private LocalDateTime deploymentDate;
    
    @Column(name = "deployed_by")
    private UUID deployedBy;
    
    @Column(name = "deployment_notes", length = 1000)
    private String deploymentNotes;
    
    @Column(name = "rollback_enabled")
    private Boolean rollbackEnabled = true; // Enable rollback functionality
    
    @Column(name = "rollback_version_id")
    private UUID rollbackVersionId;
    
    @Column(name = "last_rollback_date")
    private LocalDateTime lastRollbackDate;
    
    @Column(name = "rollback_reason", length = 500)
    private String rollbackReason;
    
    @Column(name = "is_archived")
    private Boolean isArchived = false;
    
    @Column(name = "archived_at")
    private LocalDateTime archivedAt;
    
    @Column(name = "archived_by")
    private UUID archivedBy;
    
    @Column(name = "archive_reason", length = 500)
    private String archiveReason;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public Automation() {}
    
    public Automation(UUID tenantId, String name, String type) {
        this.tenantId = tenantId;
        this.name = name;
        this.type = type;
        this.status = "draft";
        this.priority = "medium";
        this.isAiEnabled = true;
        this.aiModel = "gpt-4";
        this.aiTemperature = 0.7;
        this.aiMaxTokens = 1000;
        this.aiContextWindow = 4000;
        this.sentimentAnalysis = true;
        this.intentDetection = true;
        this.entityExtraction = true;
        this.languageDetection = true;
        this.conversationMemory = true;
        this.memoryDurationHours = 24;
        this.maxConversationLength = 50;
        this.escalationThreshold = 3;
        this.escalationAction = "human_agent";
        this.businessHoursOnly = false;
        this.timezone = "UTC";
        this.schedulingEnabled = false;
        this.testingEnabled = true;
        this.version = "1.0.0";
        this.isLatestVersion = true;
        this.approvalRequired = false;
        this.rollbackEnabled = true;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getTenantId() {
        return tenantId;
    }
    
    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        updateStatusTimestamps(status);
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public String getTriggers() {
        return triggers;
    }
    
    public void setTriggers(String triggers) {
        this.triggers = triggers;
    }
    
    public String getConditions() {
        return conditions;
    }
    
    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
    
    public String getActions() {
        return actions;
    }
    
    public void setActions(String actions) {
        this.actions = actions;
    }
    
    public String getAiConfig() {
        return aiConfig;
    }
    
    public void setAiConfig(String aiConfig) {
        this.aiConfig = aiConfig;
    }
    
    public String getWorkflowSteps() {
        return workflowSteps;
    }
    
    public void setWorkflowSteps(String workflowSteps) {
        this.workflowSteps = workflowSteps;
    }
    
    public String getResponseTemplates() {
        return responseTemplates;
    }
    
    public void setResponseTemplates(String responseTemplates) {
        this.responseTemplates = responseTemplates;
    }
    
    public String getFallbackResponses() {
        return fallbackResponses;
    }
    
    public void setFallbackResponses(String fallbackResponses) {
        this.fallbackResponses = fallbackResponses;
    }
    
    public String getLearningData() {
        return learningData;
    }
    
    public void setLearningData(String learningData) {
        this.learningData = learningData;
    }
    
    public String getPerformanceMetrics() {
        return performanceMetrics;
    }
    
    public void setPerformanceMetrics(String performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public Boolean getIsAiEnabled() {
        return isAiEnabled;
    }
    
    public void setIsAiEnabled(Boolean isAiEnabled) {
        this.isAiEnabled = isAiEnabled;
    }
    
    public String getAiModel() {
        return aiModel;
    }
    
    public void setAiModel(String aiModel) {
        this.aiModel = aiModel;
    }
    
    public Double getAiTemperature() {
        return aiTemperature;
    }
    
    public void setAiTemperature(Double aiTemperature) {
        if (aiTemperature != null && (aiTemperature < 0.0 || aiTemperature > 1.0)) {
            throw new IllegalArgumentException("AI temperature must be between 0.0 and 1.0");
        }
        this.aiTemperature = aiTemperature;
    }
    
    public Integer getAiMaxTokens() {
        return aiMaxTokens;
    }
    
    public void setAiMaxTokens(Integer aiMaxTokens) {
        if (aiMaxTokens != null && aiMaxTokens < 1) {
            throw new IllegalArgumentException("AI max tokens must be positive");
        }
        this.aiMaxTokens = aiMaxTokens;
    }
    
    public Integer getAiContextWindow() {
        return aiContextWindow;
    }
    
    public void setAiContextWindow(Integer aiContextWindow) {
        if (aiContextWindow != null && aiContextWindow < 1) {
            throw new IllegalArgumentException("AI context window must be positive");
        }
        this.aiContextWindow = aiContextWindow;
    }
    
    public String getAiSystemPrompt() {
        return aiSystemPrompt;
    }
    
    public void setAiSystemPrompt(String aiSystemPrompt) {
        this.aiSystemPrompt = aiSystemPrompt;
    }
    
    public String getAiUserPrompt() {
        return aiUserPrompt;
    }
    
    public void setAiUserPrompt(String aiUserPrompt) {
        this.aiUserPrompt = aiUserPrompt;
    }
    
    public String getAiExamples() {
        return aiExamples;
    }
    
    public void setAiExamples(String aiExamples) {
        this.aiExamples = aiExamples;
    }
    
    public String getAiConstraints() {
        return aiConstraints;
    }
    
    public void setAiConstraints(String aiConstraints) {
        this.aiConstraints = aiConstraints;
    }
    
    public Boolean getSentimentAnalysis() {
        return sentimentAnalysis;
    }
    
    public void setSentimentAnalysis(Boolean sentimentAnalysis) {
        this.sentimentAnalysis = sentimentAnalysis;
    }
    
    public Boolean getIntentDetection() {
        return intentDetection;
    }
    
    public void setIntentDetection(Boolean intentDetection) {
        this.intentDetection = intentDetection;
    }
    
    public Boolean getEntityExtraction() {
        return entityExtraction;
    }
    
    public void setEntityExtraction(Boolean entityExtraction) {
        this.entityExtraction = entityExtraction;
    }
    
    public Boolean getLanguageDetection() {
        return languageDetection;
    }
    
    public void setLanguageDetection(Boolean languageDetection) {
        this.languageDetection = languageDetection;
    }
    
    public Boolean getMultilingualSupport() {
        return multilingualSupport;
    }
    
    public void setMultilingualSupport(Boolean multilingualSupport) {
        this.multilingualSupport = multilingualSupport;
    }
    
    public String getSupportedLanguages() {
        return supportedLanguages;
    }
    
    public void setSupportedLanguages(String supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }
    
    public Boolean getConversationMemory() {
        return conversationMemory;
    }
    
    public void setConversationMemory(Boolean conversationMemory) {
        this.conversationMemory = conversationMemory;
    }
    
    public Integer getMemoryDurationHours() {
        return memoryDurationHours;
    }
    
    public void setMemoryDurationHours(Integer memoryDurationHours) {
        if (memoryDurationHours != null && memoryDurationHours < 1) {
            throw new IllegalArgumentException("Memory duration must be positive");
        }
        this.memoryDurationHours = memoryDurationHours;
    }
    
    public Integer getMaxConversationLength() {
        return maxConversationLength;
    }
    
    public void setMaxConversationLength(Integer maxConversationLength) {
        if (maxConversationLength != null && maxConversationLength < 1) {
            throw new IllegalArgumentException("Max conversation length must be positive");
        }
        this.maxConversationLength = maxConversationLength;
    }
    
    public Integer getEscalationThreshold() {
        return escalationThreshold;
    }
    
    public void setEscalationThreshold(Integer escalationThreshold) {
        if (escalationThreshold != null && escalationThreshold < 1) {
            throw new IllegalArgumentException("Escalation threshold must be positive");
        }
        this.escalationThreshold = escalationThreshold;
    }
    
    public String getEscalationAction() {
        return escalationAction;
    }
    
    public void setEscalationAction(String escalationAction) {
        this.escalationAction = escalationAction;
    }
    
    public String getEscalationMessage() {
        return escalationMessage;
    }
    
    public void setEscalationMessage(String escalationMessage) {
        this.escalationMessage = escalationMessage;
    }
    
    public Boolean getBusinessHoursOnly() {
        return businessHoursOnly;
    }
    
    public void setBusinessHoursOnly(Boolean businessHoursOnly) {
        this.businessHoursOnly = businessHoursOnly;
    }
    
    public String getBusinessHours() {
        return businessHours;
    }
    
    public void setBusinessHours(String businessHours) {
        this.businessHours = businessHours;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public String getHolidayCalendar() {
        return holidayCalendar;
    }
    
    public void setHolidayCalendar(String holidayCalendar) {
        this.holidayCalendar = holidayCalendar;
    }
    
    public String getOutOfHoursMessage() {
        return outOfHoursMessage;
    }
    
    public void setOutOfHoursMessage(String outOfHoursMessage) {
        this.outOfHoursMessage = outOfHoursMessage;
    }
    
    public Boolean getSchedulingEnabled() {
        return schedulingEnabled;
    }
    
    public void setSchedulingEnabled(Boolean schedulingEnabled) {
        this.schedulingEnabled = schedulingEnabled;
    }
    
    public String getSchedulingConfig() {
        return schedulingConfig;
    }
    
    public void setSchedulingConfig(String schedulingConfig) {
        this.schedulingConfig = schedulingConfig;
    }
    
    public String getIntegrationWebhooks() {
        return integrationWebhooks;
    }
    
    public void setIntegrationWebhooks(String integrationWebhooks) {
        this.integrationWebhooks = integrationWebhooks;
    }
    
    public String getApiEndpoints() {
        return apiEndpoints;
    }
    
    public void setApiEndpoints(String apiEndpoints) {
        this.apiEndpoints = apiEndpoints;
    }
    
    public String getExternalServices() {
        return externalServices;
    }
    
    public void setExternalServices(String externalServices) {
        this.externalServices = externalServices;
    }
    
    public Boolean getTestingEnabled() {
        return testingEnabled;
    }
    
    public void setTestingEnabled(Boolean testingEnabled) {
        this.testingEnabled = testingEnabled;
    }
    
    public String getTestingConfig() {
        return testingConfig;
    }
    
    public void setTestingConfig(String testingConfig) {
        this.testingConfig = testingConfig;
    }
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public Boolean getIsLatestVersion() {
        return isLatestVersion;
    }
    
    public void setIsLatestVersion(Boolean isLatestVersion) {
        this.isLatestVersion = isLatestVersion;
    }
    
    public UUID getParentVersionId() {
        return parentVersionId;
    }
    
    public void setParentVersionId(UUID parentVersionId) {
        this.parentVersionId = parentVersionId;
    }
    
    public String getChangeLog() {
        return changeLog;
    }
    
    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }
    
    public Boolean getApprovalRequired() {
        return approvalRequired;
    }
    
    public void setApprovalRequired(Boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }
    
    public UUID getApprovedBy() {
        return approvedBy;
    }
    
    public void setApprovedBy(UUID approvedBy) {
        this.approvedBy = approvedBy;
    }
    
    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }
    
    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }
    
    public String getApprovalNotes() {
        return approvalNotes;
    }
    
    public void setApprovalNotes(String approvalNotes) {
        this.approvalNotes = approvalNotes;
    }
    
    public LocalDateTime getDeploymentDate() {
        return deploymentDate;
    }
    
    public void setDeploymentDate(LocalDateTime deploymentDate) {
        this.deploymentDate = deploymentDate;
    }
    
    public UUID getDeployedBy() {
        return deployedBy;
    }
    
    public void setDeployedBy(UUID deployedBy) {
        this.deployedBy = deployedBy;
    }
    
    public String getDeploymentNotes() {
        return deploymentNotes;
    }
    
    public void setDeploymentNotes(String deploymentNotes) {
        this.deploymentNotes = deploymentNotes;
    }
    
    public Boolean getRollbackEnabled() {
        return rollbackEnabled;
    }
    
    public void setRollbackEnabled(Boolean rollbackEnabled) {
        this.rollbackEnabled = rollbackEnabled;
    }
    
    public UUID getRollbackVersionId() {
        return rollbackVersionId;
    }
    
    public void setRollbackVersionId(UUID rollbackVersionId) {
        this.rollbackVersionId = rollbackVersionId;
    }
    
    public LocalDateTime getLastRollbackDate() {
        return lastRollbackDate;
    }
    
    public void setLastRollbackDate(LocalDateTime lastRollbackDate) {
        this.lastRollbackDate = lastRollbackDate;
    }
    
    public String getRollbackReason() {
        return rollbackReason;
    }
    
    public void setRollbackReason(String rollbackReason) {
        this.rollbackReason = rollbackReason;
    }
    
    public Boolean getIsArchived() {
        return isArchived;
    }
    
    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }
    
    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }
    
    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }
    
    public UUID getArchivedBy() {
        return archivedBy;
    }
    
    public void setArchivedBy(UUID archivedBy) {
        this.archivedBy = archivedBy;
    }
    
    public String getArchiveReason() {
        return archiveReason;
    }
    
    public void setArchiveReason(String archiveReason) {
        this.archiveReason = archiveReason;
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
     * Update status and related timestamps
     */
    private void updateStatusTimestamps(String newStatus) {
        LocalDateTime now = LocalDateTime.now();
        
        switch (newStatus) {
            case "active":
                if (this.deploymentDate == null) {
                    this.deploymentDate = now;
                }
                break;
            case "archived":
                if (this.archivedAt == null) {
                    this.archivedAt = now;
                }
                break;
        }
    }
    
    /**
     * Activate automation
     */
    public void activate() {
        this.status = "active";
        this.deploymentDate = LocalDateTime.now();
    }
    
    /**
     * Pause automation
     */
    public void pause() {
        this.status = "paused";
    }
    
    /**
     * Resume automation
     */
    public void resume() {
        this.status = "active";
    }
    
    /**
     * Archive automation with reason
     */
    public void archive(UUID archivedBy, String reason) {
        this.status = "archived";
        this.isArchived = true;
        this.archivedAt = LocalDateTime.now();
        this.archivedBy = archivedBy;
        this.archiveReason = reason;
    }
    
    /**
     * Unarchive automation
     */
    public void unarchive() {
        this.isArchived = false;
        this.archivedAt = null;
        this.archivedBy = null;
        this.archiveReason = null;
    }
    
    /**
     * Approve automation
     */
    public void approve(UUID approvedBy, String notes) {
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
        this.approvalNotes = notes;
    }
    
    /**
     * Deploy automation
     */
    public void deploy(UUID deployedBy, String notes) {
        this.deployedBy = deployedBy;
        this.deploymentDate = LocalDateTime.now();
        this.deploymentNotes = notes;
        this.status = "active";
    }
    
    /**
     * Rollback automation
     */
    public void rollback(UUID rollbackVersionId, String reason) {
        this.rollbackVersionId = rollbackVersionId;
        this.lastRollbackDate = LocalDateTime.now();
        this.rollbackReason = reason;
        this.status = "paused";
    }
    
    /**
     * Add tag to automation (with deduplication)
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
     * Remove tag from automation
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
     * Check if automation has specific tag
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
     * Check if automation is active
     */
    public boolean isActive() {
        return "active".equals(this.status);
    }
    
    /**
     * Check if automation is paused
     */
    public boolean isPaused() {
        return "paused".equals(this.status);
    }
    
    /**
     * Check if automation is archived
     */
    public boolean isArchived() {
        return "archived".equals(this.status);
    }
    
    /**
     * Check if automation is in draft mode
     */
    public boolean isDraft() {
        return "draft".equals(this.status);
    }
    
    /**
     * Check if automation can be activated
     */
    public boolean canBeActivated() {
        return !this.isArchived && 
               (this.isDraft() || this.isPaused()) &&
               (!this.approvalRequired || this.approvedBy != null);
    }
    
    /**
     * Check if automation can be deployed
     */
    public boolean canBeDeployed() {
        return this.isActive() && 
               this.deployedBy != null &&
               this.deploymentDate != null;
    }
    
    /**
     * Check if automation can be rolled back
     */
    public boolean canBeRolledBack() {
        return this.rollbackEnabled && 
               this.rollbackVersionId != null &&
               this.isActive();
    }
    
    /**
     * Check if automation is within business hours
     */
    public boolean isWithinBusinessHours() {
        if (!this.businessHoursOnly) {
            return true;
        }
        
        // TODO: Implement business hours logic
        return true;
    }
    
    /**
     * Check if automation supports specific language
     */
    public boolean supportsLanguage(String language) {
        if (!this.multilingualSupport) {
            return "en".equals(language);
        }
        
        if (this.supportedLanguages == null || this.supportedLanguages.isEmpty()) {
            return true; // Default to supporting all languages
        }
        
        String[] languages = this.supportedLanguages.split(",");
        for (String supportedLanguage : languages) {
            if (supportedLanguage.trim().equalsIgnoreCase(language)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get automation age in days
     */
    public long getAgeInDays() {
        if (createdAt == null) {
            return 0;
        }
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toDays();
    }
    
    /**
     * Get time since last deployment in hours
     */
    public long getTimeSinceLastDeployment() {
        if (deploymentDate == null) {
            return 0;
        }
        return java.time.Duration.between(deploymentDate, LocalDateTime.now()).toHours();
    }
    
    @Override
    public String toString() {
        return "Automation{" +
                "id=" + id +
                ", tenantId=" + tenantId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", isAiEnabled=" + isAiEnabled +
                ", aiModel='" + aiModel + '\'' +
                ", isArchived=" + isArchived +
                '}';
    }
}
