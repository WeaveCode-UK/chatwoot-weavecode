package com.weavecode.chatwoot.service.impl;

import com.weavecode.chatwoot.dto.TenantUsageStats;
import com.weavecode.chatwoot.entity.Tenant;
import com.weavecode.chatwoot.enums.PlanType;
import com.weavecode.chatwoot.exception.ValidationException;
import com.weavecode.chatwoot.repository.TenantRepository;
import com.weavecode.chatwoot.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of TenantService with comprehensive business logic
 * Implements multi-tenant operations and business rules
 *
 * @author WeaveCode Team
 * @version 1.0.0
 */
@Service
@Transactional
public class TenantServiceImpl extends BaseServiceImpl<Tenant, UUID> implements TenantService {

    private static final Logger logger = LoggerFactory.getLogger(TenantServiceImpl.class);
    private final TenantRepository tenantRepository;

    public TenantServiceImpl(TenantRepository tenantRepository) {
        super(tenantRepository);
        this.tenantRepository = tenantRepository;
    }

    @Override
    protected UUID getEntityId(Tenant entity) {
        return entity.getId();
    }

    @Override
    protected Tenant mergeEntities(Tenant existing, Tenant updated) {
        // Merge only non-null fields from updated entity
        if (updated.getName() != null) {
            existing.setName(updated.getName());
        }
        if (updated.getSubdomain() != null) {
            existing.setSubdomain(updated.getSubdomain());
        }
        if (updated.getCustomDomain() != null) {
            existing.setCustomDomain(updated.getCustomDomain());
        }
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }
        if (updated.getPlanType() != null) {
            existing.setPlanType(updated.getPlanType());
        }
        if (updated.getBranding() != null) {
            existing.setBranding(updated.getBranding());
        }
        if (updated.getSettings() != null) {
            existing.setSettings(updated.getSettings());
        }
        return existing;
    }

    @Override
    public void validateBusinessRules(Tenant tenant) {
        super.validateBusinessRules(tenant);
        
        // Validate subdomain format
        if (StringUtils.hasText(tenant.getSubdomain())) {
            if (!tenant.getSubdomain().matches("^[a-z0-9-]+$")) {
                throw new ValidationException("Subdomain must contain only lowercase letters, numbers, and hyphens");
            }
            if (tenant.getSubdomain().length() < 3 || tenant.getSubdomain().length() > 63) {
                throw new ValidationException("Subdomain must be between 3 and 63 characters");
            }
        }

        // Validate custom domain format
        if (StringUtils.hasText(tenant.getCustomDomain())) {
            if (!tenant.getCustomDomain().matches("^[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                throw new ValidationException("Invalid custom domain format");
            }
        }

        // Validate plan limits
        validatePlanLimits(tenant);
    }

    private void validatePlanLimits(Tenant tenant) {
        if (tenant.getPlanType() != null) {
            switch (tenant.getPlanType()) {
                case FREE:
                    if (tenant.getMaxUsers() > 5) {
                        throw new ValidationException("Free plan allows maximum 5 users");
                    }
                    if (tenant.getMaxStorageGB() > 1) {
                        throw new ValidationException("Free plan allows maximum 1GB storage");
                    }
                    break;
                case BASIC:
                    if (tenant.getMaxUsers() > 25) {
                        throw new ValidationException("Basic plan allows maximum 25 users");
                    }
                    if (tenant.getMaxStorageGB() > 10) {
                        throw new ValidationException("Basic plan allows maximum 10GB storage");
                    }
                    break;
                case PROFESSIONAL:
                    if (tenant.getMaxUsers() > 100) {
                        throw new ValidationException("Professional plan allows maximum 100 users");
                    }
                    if (tenant.getMaxStorageGB() > 100) {
                        throw new ValidationException("Professional plan allows maximum 100GB storage");
                    }
                    break;
                case ENTERPRISE:
                    // Enterprise plan has no strict limits
                    break;
            }
        }
    }

    @Override
    public Optional<Tenant> findByCustomDomain(String customDomain) {
        logger.debug("Finding tenant by custom domain: {}", customDomain);
        return tenantRepository.findByCustomDomain(customDomain);
    }

    @Override
    public Optional<Tenant> findBySubdomain(String subdomain) {
        logger.debug("Finding tenant by subdomain: {}", subdomain);
        return tenantRepository.findBySubdomain(subdomain);
    }

    @Override
    public List<Tenant> findByPlanType(PlanType planType) {
        logger.debug("Finding tenants by plan type: {}", planType);
        return tenantRepository.findByPlanType(planType);
    }

    @Override
    public List<Tenant> findTenantsWithExpiringTrials(int daysAhead) {
        logger.debug("Finding tenants with expiring trials in {} days", daysAhead);
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(daysAhead);
        return tenantRepository.findByTrialEndDateBeforeAndStatus(expiryDate, "TRIAL");
    }

    @Override
    public List<Tenant> findTenantsWithExpiredSubscriptions() {
        logger.debug("Finding tenants with expired subscriptions");
        return tenantRepository.findBySubscriptionEndDateBeforeAndStatusNot(
            LocalDateTime.now(), "CANCELLED");
    }

    @Override
    public Page<Tenant> findByStatus(String status, Pageable pageable) {
        logger.debug("Finding tenants by status: {} with pagination", status);
        return tenantRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Tenant> findByPlanType(PlanType planType, Pageable pageable) {
        logger.debug("Finding tenants by plan type: {} with pagination", planType);
        return tenantRepository.findByPlanType(planType, pageable);
    }

    @Override
    public List<Tenant> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        logger.debug("Finding tenants created between {} and {}", startDate, endDate);
        return tenantRepository.findByCreatedAtBetween(startDate, endDate);
    }

    @Override
    public List<Tenant> findByCountry(String country) {
        logger.debug("Finding tenants by country: {}", country);
        return tenantRepository.findByCountry(country);
    }

    @Override
    public List<Tenant> findByIndustry(String industry) {
        logger.debug("Finding tenants by industry: {}", industry);
        return tenantRepository.findByIndustry(industry);
    }

    @Override
    public boolean isCustomDomainAvailable(String customDomain) {
        logger.debug("Checking if custom domain is available: {}", customDomain);
        return !tenantRepository.existsByCustomDomain(customDomain);
    }

    @Override
    public boolean isSubdomainAvailable(String subdomain) {
        logger.debug("Checking if subdomain is available: {}", subdomain);
        return !tenantRepository.existsBySubdomain(subdomain);
    }

    @Override
    public Tenant activateSubscription(UUID tenantId, PlanType planType) {
        logger.info("Activating subscription for tenant: {} with plan: {}", tenantId, planType);
        
        Tenant tenant = findByIdOrThrow(tenantId);
        tenant.setPlanType(planType);
        tenant.setStatus("ACTIVE");
        tenant.setSubscriptionStartDate(LocalDateTime.now());
        tenant.setSubscriptionEndDate(LocalDateTime.now().plusYears(1));
        
        // Set plan-specific limits
        setPlanLimits(tenant, planType);
        
        return tenantRepository.save(tenant);
    }

    private void setPlanLimits(Tenant tenant, PlanType planType) {
        switch (planType) {
            case FREE:
                tenant.setMaxUsers(5);
                tenant.setMaxStorageGB(1);
                tenant.setMaxApiCallsPerMonth(1000);
                break;
            case BASIC:
                tenant.setMaxUsers(25);
                tenant.setMaxStorageGB(10);
                tenant.setMaxApiCallsPerMonth(10000);
                break;
            case PROFESSIONAL:
                tenant.setMaxUsers(100);
                tenant.setMaxStorageGB(100);
                tenant.setMaxApiCallsPerMonth(100000);
                break;
            case ENTERPRISE:
                tenant.setMaxUsers(1000);
                tenant.setMaxStorageGB(1000);
                tenant.setMaxApiCallsPerMonth(1000000);
                break;
        }
    }

    @Override
    public Tenant suspendTenant(UUID tenantId, String reason) {
        logger.info("Suspending tenant: {} for reason: {}", tenantId, reason);
        
        Tenant tenant = findByIdOrThrow(tenantId);
        tenant.setStatus("SUSPENDED");
        tenant.setSuspensionReason(reason);
        tenant.setSuspendedAt(LocalDateTime.now());
        
        return tenantRepository.save(tenant);
    }

    @Override
    public Tenant reactivateTenant(UUID tenantId) {
        logger.info("Reactivating tenant: {}", tenantId);
        
        Tenant tenant = findByIdOrThrow(tenantId);
        tenant.setStatus("ACTIVE");
        tenant.setSuspensionReason(null);
        tenant.setSuspendedAt(null);
        
        return tenantRepository.save(tenant);
    }

    @Override
    public Tenant upgradePlan(UUID tenantId, PlanType newPlanType) {
        logger.info("Upgrading tenant: {} to plan: {}", tenantId, newPlanType);
        
        Tenant tenant = findByIdOrThrow(tenantId);
        PlanType currentPlan = tenant.getPlanType();
        
        if (newPlanType.ordinal() <= currentPlan.ordinal()) {
            throw new ValidationException("New plan must be higher than current plan");
        }
        
        return activateSubscription(tenantId, newPlanType);
    }

    @Override
    public Tenant downgradePlan(UUID tenantId, PlanType newPlanType) {
        logger.info("Downgrading tenant: {} to plan: {}", tenantId, newPlanType);
        
        Tenant tenant = findByIdOrThrow(tenantId);
        PlanType currentPlan = tenant.getPlanType();
        
        if (newPlanType.ordinal() >= currentPlan.ordinal()) {
            throw new ValidationException("New plan must be lower than current plan");
        }
        
        // Check if downgrade is allowed (e.g., no active conversations)
        if (hasActiveConversations(tenantId)) {
            throw new ValidationException("Cannot downgrade plan while having active conversations");
        }
        
        return activateSubscription(tenantId, newPlanType);
    }

    private boolean hasActiveConversations(UUID tenantId) {
        // This would typically call a conversation service
        // For now, return false as placeholder
        return false;
    }

    @Override
    public Tenant cancelSubscription(UUID tenantId, String reason) {
        logger.info("Cancelling subscription for tenant: {} for reason: {}", tenantId, reason);
        
        Tenant tenant = findByIdOrThrow(tenantId);
        tenant.setStatus("CANCELLED");
        tenant.setCancellationReason(reason);
        tenant.setCancelledAt(LocalDateTime.now());
        
        return tenantRepository.save(tenant);
    }

    @Override
    public TenantUsageStats getTenantUsageStats(UUID tenantId) {
        logger.debug("Getting usage stats for tenant: {}", tenantId);
        
        Tenant tenant = findByIdOrThrow(tenantId);
        TenantUsageStats stats = new TenantUsageStats(tenantId, tenant.getName());
        
        // This would typically aggregate data from various repositories
        // For now, return basic stats as placeholder
        stats.setTotalUsers(tenant.getMaxUsers());
        stats.setStorageUsedBytes(0L);
        stats.setStorageLimitBytes(tenant.getMaxStorageGB() * 1024L * 1024L * 1024L);
        
        return stats;
    }

    @Override
    public boolean checkPlanLimits(UUID tenantId, String resourceType, int quantity) {
        logger.debug("Checking plan limits for tenant: {} resource: {} quantity: {}", 
                    tenantId, resourceType, quantity);
        
        Tenant tenant = findByIdOrThrow(tenantId);
        
        switch (resourceType) {
            case "USERS":
                return quantity <= tenant.getMaxUsers();
            case "STORAGE":
                return quantity <= tenant.getMaxStorageGB();
            case "API_CALLS":
                return quantity <= tenant.getMaxApiCallsPerMonth();
            default:
                return true;
        }
    }

    @Override
    public Tenant updateBranding(UUID tenantId, String logoUrl, String primaryColour, String secondaryColour) {
        logger.info("Updating branding for tenant: {}", tenantId);
        
        Tenant tenant = findByIdOrThrow(tenantId);
        
        // Update branding in settings JSON
        // This is a simplified implementation - in practice, you'd use a proper JSON library
        String brandingJson = String.format(
            "{\"logoUrl\":\"%s\",\"primaryColour\":\"%s\",\"secondaryColour\":\"%s\"}", 
            logoUrl, primaryColour, secondaryColour);
        
        tenant.setBranding(brandingJson);
        
        return tenantRepository.save(tenant);
    }

    @Override
    public Tenant configureIntegrations(UUID tenantId, String integrationType, String configuration) {
        logger.info("Configuring integration for tenant: {} type: {}", tenantId, integrationType);
        
        Tenant tenant = findByIdOrThrow(tenantId);
        
        // Update integrations in settings JSON
        // This is a simplified implementation
        String integrationsJson = String.format(
            "{\"type\":\"%s\",\"configuration\":\"%s\"}", 
            integrationType, configuration);
        
        tenant.setSettings(integrationsJson);
        
        return tenantRepository.save(tenant);
    }

    // Placeholder implementations for remaining methods
    @Override
    public Object getComplianceStatus(UUID tenantId) {
        // Implementation would return ComplianceStatus object
        return null;
    }

    @Override
    public Tenant updateComplianceSettings(UUID tenantId, boolean gdprEnabled, boolean lgpdEnabled) {
        // Implementation would update compliance settings
        return findByIdOrThrow(tenantId);
    }

    @Override
    public Page<Tenant> searchTenants(String query, String status, PlanType planType, 
                                     String country, String industry, Pageable pageable) {
        // Implementation would use specifications for complex search
        return tenantRepository.findAll(pageable);
    }

    @Override
    public Object getTenantAnalytics(UUID tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        // Implementation would return TenantAnalytics object
        return null;
    }

    @Override
    public void recordTenantActivity(UUID tenantId, String activityType, String details) {
        logger.info("Recording tenant activity: {} {} {}", tenantId, activityType, details);
        // Implementation would log activity to audit system
    }

    @Override
    public Object getTenantDashboardData(UUID tenantId) {
        // Implementation would return TenantDashboardData object
        return null;
    }
}
