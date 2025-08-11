package com.weavecode.chatwoot.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Customer entity representing end users across all tenants
 * Implements enterprise-grade security patterns and audit trails
 * 
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    
    @NotNull(message = "Tenant ID is required")
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;
    
    @NotBlank(message = "Customer name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    @Column(name = "email", unique = true)
    private String email;
    
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Column(name = "phone_number", length = 20)
    private String phoneNumber;
    
    @Size(max = 100, message = "Company name cannot exceed 100 characters")
    @Column(name = "company_name", length = 100)
    private String companyName;
    
    @Size(max = 100, message = "Job title cannot exceed 100 characters")
    @Column(name = "job_title", length = 100)
    private String jobTitle;
    
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    @Column(name = "address", length = 500)
    private String address;
    
    @Size(max = 100, message = "City cannot exceed 100 characters")
    @Column(name = "city", length = 100)
    private String city;
    
    @Size(max = 100, message = "State/Province cannot exceed 100 characters")
    @Column(name = "state_province", length = 100)
    private String stateProvince;
    
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    @Column(name = "postal_code", length = 20)
    private String postalCode;
    
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    @Column(name = "country", length = 100)
    private String country;
    
    @Size(max = 50, message = "Language cannot exceed 50 characters")
    @Column(name = "language", length = 50, default = "en")
    private String language = "en";
    
    @Size(max = 50, message = "Timezone cannot exceed 50 characters")
    @Column(name = "timezone", length = 50, default = "UTC")
    private String timezone = "UTC";
    
    @Column(name = "date_of_birth")
    private LocalDateTime dateOfBirth;
    
    @Size(max = 20, message = "Gender cannot exceed 20 characters")
    @Column(name = "gender", length = 20)
    private String gender;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    @Column(name = "social_media", columnDefinition = "jsonb")
    private String socialMedia; // JSON string for social media profiles
    
    @Column(name = "preferences", columnDefinition = "jsonb")
    private String preferences; // JSON string for customer preferences
    
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // JSON string for additional customer data
    
    @Column(name = "tags", length = 500)
    private String tags; // Comma-separated tags for categorisation
    
    @Column(name = "source", length = 50, default = "web")
    private String source = "web"; // web, whatsapp, instagram, facebook, telegram, api
    
    @Column(name = "source_id", length = 100)
    private String sourceId; // External source identifier
    
    @Column(name = "first_contact_at")
    private LocalDateTime firstContactAt;
    
    @Column(name = "last_contact_at")
    private LocalDateTime lastContactAt;
    
    @Column(name = "total_conversations")
    private Integer totalConversations = 0;
    
    @Column(name = "total_messages")
    private Integer totalMessages = 0;
    
    @Column(name = "average_response_time")
    private Long averageResponseTime; // Average response time in minutes
    
    @Column(name = "customer_satisfaction_score")
    private Double customerSatisfactionScore; // Average satisfaction rating (1-5)
    
    @Column(name = "lifetime_value")
    private Double lifetimeValue; // Customer lifetime value in currency
    
    @Column(name = "currency", length = 3, default = "GBP")
    private String currency = "GBP";
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;
    
    @Column(name = "verification_method", length = 50)
    private String verificationMethod; // email, phone, document, manual
    
    @Column(name = "is_blocked")
    private Boolean isBlocked = false;
    
    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;
    
    @Column(name = "blocked_by")
    private UUID blockedBy;
    
    @Column(name = "block_reason", length = 500)
    private String blockReason;
    
    @Column(name = "is_archived")
    private Boolean isArchived = false;
    
    @Column(name = "archived_at")
    private LocalDateTime archivedAt;
    
    @Column(name = "archived_by")
    private UUID archivedBy;
    
    @Column(name = "archive_reason", length = 500)
    private String archiveReason;
    
    @Column(name = "gdpr_consent")
    private Boolean gdprConsent = false;
    
    @Column(name = "gdpr_consent_at")
    private LocalDateTime gdprConsentAt;
    
    @Column(name = "gdpr_consent_version", length = 20)
    private String gdprConsentVersion;
    
    @Column(name = "marketing_consent")
    private Boolean marketingConsent = false;
    
    @Column(name = "marketing_consent_at")
    private LocalDateTime marketingConsentAt;
    
    @Column(name = "data_retention_until")
    private LocalDateTime dataRetentionUntil;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public Customer() {}
    
    public Customer(UUID tenantId, String name) {
        this.tenantId = tenantId;
        this.name = name;
        this.firstContactAt = LocalDateTime.now();
        this.lastContactAt = LocalDateTime.now();
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getJobTitle() {
        return jobTitle;
    }
    
    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getStateProvince() {
        return stateProvince;
    }
    
    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    
    public String getSocialMedia() {
        return socialMedia;
    }
    
    public void setSocialMedia(String socialMedia) {
        this.socialMedia = socialMedia;
    }
    
    public String getPreferences() {
        return preferences;
    }
    
    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    
    public LocalDateTime getFirstContactAt() {
        return firstContactAt;
    }
    
    public void setFirstContactAt(LocalDateTime firstContactAt) {
        this.firstContactAt = firstContactAt;
    }
    
    public LocalDateTime getLastContactAt() {
        return lastContactAt;
    }
    
    public void setLastContactAt(LocalDateTime lastContactAt) {
        this.lastContactAt = lastContactAt;
    }
    
    public Integer getTotalConversations() {
        return totalConversations;
    }
    
    public void setTotalConversations(Integer totalConversations) {
        this.totalConversations = totalConversations;
    }
    
    public Integer getTotalMessages() {
        return totalMessages;
    }
    
    public void setTotalMessages(Integer totalMessages) {
        this.totalMessages = totalMessages;
    }
    
    public Long getAverageResponseTime() {
        return averageResponseTime;
    }
    
    public void setAverageResponseTime(Long averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }
    
    public Double getCustomerSatisfactionScore() {
        return customerSatisfactionScore;
    }
    
    public void setCustomerSatisfactionScore(Double customerSatisfactionScore) {
        if (customerSatisfactionScore != null && (customerSatisfactionScore < 1.0 || customerSatisfactionScore > 5.0)) {
            throw new IllegalArgumentException("Customer satisfaction score must be between 1.0 and 5.0");
        }
        this.customerSatisfactionScore = customerSatisfactionScore;
    }
    
    public Double getLifetimeValue() {
        return lifetimeValue;
    }
    
    public void setLifetimeValue(Double lifetimeValue) {
        this.lifetimeValue = lifetimeValue;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }
    
    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
    
    public String getVerificationMethod() {
        return verificationMethod;
    }
    
    public void setVerificationMethod(String verificationMethod) {
        this.verificationMethod = verificationMethod;
    }
    
    public Boolean getIsBlocked() {
        return isBlocked;
    }
    
    public void setIsBlocked(Boolean isBlocked) {
        this.isBlocked = isBlocked;
    }
    
    public LocalDateTime getBlockedAt() {
        return blockedAt;
    }
    
    public void setBlockedAt(LocalDateTime blockedAt) {
        this.blockedAt = blockedAt;
    }
    
    public UUID getBlockedBy() {
        return blockedBy;
    }
    
    public void setBlockedBy(UUID blockedBy) {
        this.blockedBy = blockedBy;
    }
    
    public String getBlockReason() {
        return blockReason;
    }
    
    public void setBlockReason(String blockReason) {
        this.blockReason = blockReason;
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
    
    public Boolean getGdprConsent() {
        return gdprConsent;
    }
    
    public void setGdprConsent(Boolean gdprConsent) {
        this.gdprConsent = gdprConsent;
    }
    
    public LocalDateTime getGdprConsentAt() {
        return gdprConsentAt;
    }
    
    public void setGdprConsentAt(LocalDateTime gdprConsentAt) {
        this.gdprConsentAt = gdprConsentAt;
    }
    
    public String getGdprConsentVersion() {
        return gdprConsentVersion;
    }
    
    public void setGdprConsentVersion(String gdprConsentVersion) {
        this.gdprConsentVersion = gdprConsentVersion;
    }
    
    public Boolean getMarketingConsent() {
        return marketingConsent;
    }
    
    public void setMarketingConsent(Boolean marketingConsent) {
        this.marketingConsent = marketingConsent;
    }
    
    public LocalDateTime getMarketingConsentAt() {
        return marketingConsentAt;
    }
    
    public void setMarketingConsentAt(LocalDateTime marketingConsentAt) {
        this.marketingConsentAt = marketingConsentAt;
    }
    
    public LocalDateTime getDataRetentionUntil() {
        return dataRetentionUntil;
    }
    
    public void setDataRetentionUntil(LocalDateTime dataRetentionUntil) {
        this.dataRetentionUntil = dataRetentionUntil;
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
     * Update last contact timestamp
     */
    public void updateLastContact() {
        this.lastContactAt = LocalDateTime.now();
    }
    
    /**
     * Increment conversation count
     */
    public void incrementConversationCount() {
        this.totalConversations++;
        updateLastContact();
    }
    
    /**
     * Increment message count
     */
    public void incrementMessageCount() {
        this.totalMessages++;
        updateLastContact();
    }
    
    /**
     * Update average response time
     */
    public void updateAverageResponseTime(Long newResponseTime) {
        if (this.averageResponseTime == null) {
            this.averageResponseTime = newResponseTime;
        } else {
            // Simple moving average
            this.averageResponseTime = (this.averageResponseTime + newResponseTime) / 2;
        }
    }
    
    /**
     * Update customer satisfaction score
     */
    public void updateSatisfactionScore(Double newRating) {
        if (newRating == null || newRating < 1.0 || newRating > 5.0) {
            return;
        }
        
        if (this.customerSatisfactionScore == null) {
            this.customerSatisfactionScore = newRating;
        } else {
            // Simple moving average
            this.customerSatisfactionScore = (this.customerSatisfactionScore + newRating) / 2;
        }
    }
    
    /**
     * Verify customer
     */
    public void verify(String method) {
        this.isVerified = true;
        this.verifiedAt = LocalDateTime.now();
        this.verificationMethod = method;
    }
    
    /**
     * Block customer with reason
     */
    public void block(UUID blockedBy, String reason) {
        this.isBlocked = true;
        this.blockedAt = LocalDateTime.now();
        this.blockedBy = blockedBy;
        this.blockReason = reason;
    }
    
    /**
     * Unblock customer
     */
    public void unblock() {
        this.isBlocked = false;
        this.blockedAt = null;
        this.blockedBy = null;
        this.blockReason = null;
    }
    
    /**
     * Archive customer with reason
     */
    public void archive(UUID archivedBy, String reason) {
        this.isArchived = true;
        this.archivedAt = LocalDateTime.now();
        this.archivedBy = archivedBy;
        this.archiveReason = reason;
    }
    
    /**
     * Unarchive customer
     */
    public void unarchive() {
        this.isArchived = false;
        this.archivedAt = null;
        this.archivedBy = null;
        this.archiveReason = null;
    }
    
    /**
     * Give GDPR consent
     */
    public void giveGDPRConsent(String version) {
        this.gdprConsent = true;
        this.gdprConsentAt = LocalDateTime.now();
        this.gdprConsentVersion = version;
    }
    
    /**
     * Withdraw GDPR consent
     */
    public void withdrawGDPRConsent() {
        this.gdprConsent = false;
        this.gdprConsentAt = null;
        this.gdprConsentVersion = null;
    }
    
    /**
     * Give marketing consent
     */
    public void giveMarketingConsent() {
        this.marketingConsent = true;
        this.marketingConsentAt = LocalDateTime.now();
    }
    
    /**
     * Withdraw marketing consent
     */
    public void withdrawMarketingConsent() {
        this.marketingConsent = false;
        this.marketingConsentAt = null;
    }
    
    /**
     * Add tag to customer (with deduplication)
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
     * Remove tag from customer
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
     * Check if customer has specific tag
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
     * Get customer age in years
     */
    public Integer getAge() {
        if (dateOfBirth == null) {
            return null;
        }
        return java.time.Period.between(dateOfBirth.toLocalDate(), LocalDateTime.now().toLocalDate()).getYears();
    }
    
    /**
     * Check if customer is active (not blocked or archived)
     */
    public boolean isActive() {
        return !this.isBlocked && !this.isArchived;
    }
    
    /**
     * Check if customer can be contacted
     */
    public boolean canBeContacted() {
        return isActive() && (this.email != null || this.phoneNumber != null);
    }
    
    /**
     * Check if customer has GDPR consent
     */
    public boolean hasGDPRConsent() {
        return this.gdprConsent != null && this.gdprConsent;
    }
    
    /**
     * Check if customer has marketing consent
     */
    public boolean hasMarketingConsent() {
        return this.marketingConsent != null && this.marketingConsent;
    }
    
    /**
     * Get customer lifetime value in specified currency
     */
    public Double getLifetimeValueInCurrency(String targetCurrency) {
        if (this.lifetimeValue == null) {
            return null;
        }
        
        if (this.currency.equals(targetCurrency)) {
            return this.lifetimeValue;
        }
        
        // TODO: Implement currency conversion logic
        return this.lifetimeValue;
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", tenantId=" + tenantId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", companyName='" + companyName + '\'' +
                ", isVerified=" + isVerified +
                ", isBlocked=" + isBlocked +
                ", isArchived=" + isArchived +
                '}';
    }
}
