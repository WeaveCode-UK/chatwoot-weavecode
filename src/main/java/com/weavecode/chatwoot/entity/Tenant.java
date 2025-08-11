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
 * Tenant entity representing a multi-tenant organization
 * 
 * This entity manages:
 * - Tenant identification and configuration
 * - Subscription and billing information
 * - Feature flags and limits
 * - Domain and branding settings
 * - Multi-tenant isolation
 * 
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Entity
@Table(name = "tenants", indexes = {
    @Index(name = "idx_tenants_domain", columnList = "domain"),
    @Index(name = "idx_tenants_status", columnList = "status"),
    @Index(name = "idx_tenants_plan", columnList = "plan"),
    @Index(name = "idx_tenants_created_at", columnList = "created_at")
})
public class Tenant {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "domain", nullable = false, unique = true, length = 255)
    private String domain;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false)
    private SubscriptionPlan plan = SubscriptionPlan.FREE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TenantStatus status = TenantStatus.ACTIVE;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "favicon_url", length = 500)
    private String faviconUrl;

    @Column(name = "primary_color", length = 7)
    private String primaryColor = "#3B82F6";

    @Column(name = "secondary_color", length = 7)
    private String secondaryColor = "#1F2937";

    @Column(name = "timezone", length = 50)
    private String timezone = "UTC";

    @Column(name = "language", length = 10)
    private String language = "en";

    @Column(name = "currency", length = 3)
    private String currency = "GBP";

    @Column(name = "billing_email", length = 255)
    private String billingEmail;

    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "website", length = 500)
    private String website;

    @Column(name = "features", columnDefinition = "jsonb")
    private String features; // JSON string of enabled features

    @Column(name = "limits", columnDefinition = "jsonb")
    private String limits; // JSON string of plan limits

    @Column(name = "settings", columnDefinition = "jsonb")
    private String settings; // JSON string of tenant settings

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // Additional tenant metadata

    @Column(name = "subscription_id")
    private String subscriptionId; // External subscription ID

    @Column(name = "trial_ends_at")
    private LocalDateTime trialEndsAt;

    @Column(name = "subscription_ends_at")
    private LocalDateTime subscriptionEndsAt;

    @Column(name = "max_users")
    private Integer maxUsers = 5;

    @Column(name = "max_conversations")
    private Integer maxConversations = 100;

    @Column(name = "max_storage_gb")
    private Integer maxStorageGb = 1;

    @Column(name = "support_level")
    private String supportLevel = "BASIC";

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
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Customer> customers = new HashSet<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Conversation> conversations = new HashSet<>();

    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Subscription> subscriptions = new HashSet<>();

    // Enums
    public enum SubscriptionPlan {
        FREE,           // Free tier with basic features
        STARTER,        // Starter plan with more features
        PROFESSIONAL,   // Professional plan with advanced features
        ENTERPRISE      // Enterprise plan with all features
    }

    public enum TenantStatus {
        ACTIVE,         // Tenant is active
        SUSPENDED,      // Tenant is suspended
        CANCELLED,      // Tenant is cancelled
        EXPIRED,        // Tenant subscription expired
        PENDING         // Tenant is pending activation
    }

    // Constructors
    public Tenant() {}

    public Tenant(String name, String domain, SubscriptionPlan plan) {
        this.name = name;
        this.domain = domain;
        this.plan = plan;
        this.status = TenantStatus.ACTIVE;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public SubscriptionPlan getPlan() {
        return plan;
    }

    public void setPlan(SubscriptionPlan plan) {
        this.plan = plan;
        updateLimitsBasedOnPlan();
    }

    public TenantStatus getStatus() {
        return status;
    }

    public void setStatus(TenantStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getFaviconUrl() {
        return faviconUrl;
    }

    public void setFaviconUrl(String faviconUrl) {
        this.faviconUrl = faviconUrl;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBillingEmail() {
        return billingEmail;
    }

    public void setBillingEmail(String billingEmail) {
        this.billingEmail = billingEmail;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public String getLimits() {
        return limits;
    }

    public void setLimits(String limits) {
        this.limits = limits;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public LocalDateTime getTrialEndsAt() {
        return trialEndsAt;
    }

    public void setTrialEndsAt(LocalDateTime trialEndsAt) {
        this.trialEndsAt = trialEndsAt;
    }

    public LocalDateTime getSubscriptionEndsAt() {
        return subscriptionEndsAt;
    }

    public void setSubscriptionEndsAt(LocalDateTime subscriptionEndsAt) {
        this.subscriptionEndsAt = subscriptionEndsAt;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }

    public Integer getMaxConversations() {
        return maxConversations;
    }

    public void setMaxConversations(Integer maxConversations) {
        this.maxConversations = maxConversations;
    }

    public Integer getMaxStorageGb() {
        return maxStorageGb;
    }

    public void setMaxStorageGb(Integer maxStorageGb) {
        this.maxStorageGb = maxStorageGb;
    }

    public String getSupportLevel() {
        return supportLevel;
    }

    public void setSupportLevel(String supportLevel) {
        this.supportLevel = supportLevel;
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

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(Set<Customer> customers) {
        this.customers = customers;
    }

    public Set<Conversation> getConversations() {
        return conversations;
    }

    public void setConversations(Set<Conversation> conversations) {
        this.conversations = conversations;
    }

    public Set<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    // Business methods
    public void activate() {
        this.status = TenantStatus.ACTIVE;
    }

    public void suspend() {
        this.status = TenantStatus.SUSPENDED;
    }

    public void cancel() {
        this.status = TenantStatus.CANCELLED;
    }

    public void expire() {
        this.status = TenantStatus.EXPIRED;
    }

    public boolean isActive() {
        return status == TenantStatus.ACTIVE;
    }

    public boolean isSuspended() {
        return status == TenantStatus.SUSPENDED;
    }

    public boolean isCancelled() {
        return status == TenantStatus.CANCELLED;
    }

    public boolean isExpired() {
        return status == TenantStatus.EXPIRED;
    }

    public boolean isTrialActive() {
        return trialEndsAt != null && LocalDateTime.now().isBefore(trialEndsAt);
    }

    public boolean isSubscriptionActive() {
        return subscriptionEndsAt == null || LocalDateTime.now().isBefore(subscriptionEndsAt);
    }

    public boolean canAddUser() {
        return users.size() < maxUsers;
    }

    public boolean canCreateConversation() {
        return conversations.size() < maxConversations;
    }

    public boolean hasFeature(String feature) {
        // Implementation would parse features JSON and check if feature is enabled
        return true; // Simplified for now
    }

    public boolean isWithinLimit(String limitType, int currentValue) {
        switch (limitType.toLowerCase()) {
            case "users":
                return currentValue < maxUsers;
            case "conversations":
                return currentValue < maxConversations;
            default:
                return true;
        }
    }

    private void updateLimitsBasedOnPlan() {
        switch (plan) {
            case FREE:
                maxUsers = 5;
                maxConversations = 100;
                maxStorageGb = 1;
                supportLevel = "BASIC";
                break;
            case STARTER:
                maxUsers = 25;
                maxConversations = 1000;
                maxStorageGb = 10;
                supportLevel = "EMAIL";
                break;
            case PROFESSIONAL:
                maxUsers = 100;
                maxConversations = 10000;
                maxStorageGb = 100;
                supportLevel = "PRIORITY";
                break;
            case ENTERPRISE:
                maxUsers = -1; // Unlimited
                maxConversations = -1; // Unlimited
                maxStorageGb = 1000;
                supportLevel = "DEDICATED";
                break;
        }
    }

    public void upgradePlan(SubscriptionPlan newPlan) {
        this.plan = newPlan;
        updateLimitsBasedOnPlan();
    }

    public void startTrial(int days) {
        this.trialEndsAt = LocalDateTime.now().plusDays(days);
    }

    public void extendSubscription(int months) {
        if (this.subscriptionEndsAt == null) {
            this.subscriptionEndsAt = LocalDateTime.now().plusMonths(months);
        } else {
            this.subscriptionEndsAt = this.subscriptionEndsAt.plusMonths(months);
        }
    }

    // Utility methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tenant tenant = (Tenant) o;
        return id != null && id.equals(tenant.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Tenant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                ", plan=" + plan +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
