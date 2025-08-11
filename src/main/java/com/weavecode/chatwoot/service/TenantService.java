package com.weavecode.chatwoot.service;

import com.weavecode.chatwoot.entity.Tenant;
import com.weavecode.chatwoot.enums.PlanType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for Tenant entity business logic
 * Implements multi-tenant operations and business rules
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
public interface TenantService extends BaseService<Tenant, UUID> {

    /**
     * Find tenant by custom domain
     */
    Optional<Tenant> findByCustomDomain(String customDomain);

    /**
     * Find tenant by subdomain
     */
    Optional<Tenant> findBySubdomain(String subdomain);

    /**
     * Find tenants by plan type
     */
    List<Tenant> findByPlanType(PlanType planType);

    /**
     * Find tenants with expiring trials
     */
    List<Tenant> findTenantsWithExpiringTrials(int daysAhead);

    /**
     * Find tenants with expired subscriptions
     */
    List<Tenant> findTenantsWithExpiredSubscriptions();

    /**
     * Find tenants by status with pagination
     */
    Page<Tenant> findByStatus(String status, Pageable pageable);

    /**
     * Find tenants by plan type with pagination
     */
    Page<Tenant> findByPlanType(PlanType planType, Pageable pageable);

    /**
     * Find tenants created within date range
     */
    List<Tenant> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find tenants by country
     */
    List<Tenant> findByCountry(String country);

    /**
     * Find tenants by industry
     */
    List<Tenant> findByIndustry(String industry);

    /**
     * Check if custom domain is available
     */
    boolean isCustomDomainAvailable(String customDomain);

    /**
     * Check if subdomain is available
     */
    boolean isSubdomainAvailable(String subdomain);

    /**
     * Activate tenant subscription
     */
    Tenant activateSubscription(UUID tenantId, PlanType planType);

    /**
     * Suspend tenant for non-payment
     */
    Tenant suspendTenant(UUID tenantId, String reason);

    /**
     * Reactivate suspended tenant
     */
    Tenant reactivateTenant(UUID tenantId);

    /**
     * Upgrade tenant plan
     */
    Tenant upgradePlan(UUID tenantId, PlanType newPlanType);

    /**
     * Downgrade tenant plan
     */
    Tenant downgradePlan(UUID tenantId, PlanType newPlanType);

    /**
     * Cancel tenant subscription
     */
    Tenant cancelSubscription(UUID tenantId, String reason);

    /**
     * Get tenant usage statistics
     */
    TenantUsageStats getTenantUsageStats(UUID tenantId);

    /**
     * Check tenant plan limits
     */
    boolean checkPlanLimits(UUID tenantId, String resourceType, int quantity);

    /**
     * Update tenant branding
     */
    Tenant updateBranding(UUID tenantId, String logoUrl, String primaryColour, String secondaryColour);

    /**
     * Configure tenant integrations
     */
    Tenant configureIntegrations(UUID tenantId, String integrationType, String configuration);

    /**
     * Get tenant compliance status
     */
    ComplianceStatus getComplianceStatus(UUID tenantId);

    /**
     * Update tenant compliance settings
     */
    Tenant updateComplianceSettings(UUID tenantId, boolean gdprEnabled, boolean lgpdEnabled);

    /**
     * Search tenants with advanced criteria
     */
    Page<Tenant> searchTenants(String query, String status, PlanType planType, 
                               String country, String industry, Pageable pageable);

    /**
     * Get tenant analytics
     */
    TenantAnalytics getTenantAnalytics(UUID tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Record tenant activity
     */
    void recordTenantActivity(UUID tenantId, String activityType, String details);

    /**
     * Get tenant dashboard data
     */
    TenantDashboardData getTenantDashboardData(UUID tenantId);
}
