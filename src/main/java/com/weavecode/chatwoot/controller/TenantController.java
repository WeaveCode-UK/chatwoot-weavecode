package com.weavecode.chatwoot.controller;

import com.weavecode.chatwoot.dto.TenantUsageStats;
import com.weavecode.chatwoot.entity.Tenant;
import com.weavecode.chatwoot.enums.PlanType;
import com.weavecode.chatwoot.service.TenantService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tenants")
@CrossOrigin(origins = "*")
public class TenantController {
    
    private static final Logger logger = LoggerFactory.getLogger(TenantController.class);
    
    @Autowired
    private TenantService tenantService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Tenant>> getAllTenants(Pageable pageable) {
        try {
            Page<Tenant> tenants = tenantService.findAll(pageable);
            return ResponseEntity.ok(tenants);
        } catch (Exception e) {
            logger.error("Error fetching tenants", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#id)")
    public ResponseEntity<Tenant> getTenantById(@PathVariable UUID id) {
        try {
            return tenantService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching tenant with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tenant> createTenant(@Valid @RequestBody Tenant tenant) {
        try {
            Tenant createdTenant = tenantService.create(tenant);
            logger.info("Tenant created successfully: {}", createdTenant.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTenant);
        } catch (Exception e) {
            logger.error("Error creating tenant", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#id)")
    public ResponseEntity<Tenant> updateTenant(@PathVariable UUID id, @Valid @RequestBody Tenant tenant) {
        try {
            tenant.setId(id);
            Tenant updatedTenant = tenantService.update(id, tenant);
            logger.info("Tenant updated successfully: {}", id);
            return ResponseEntity.ok(updatedTenant);
        } catch (Exception e) {
            logger.error("Error updating tenant with id: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#id)")
    public ResponseEntity<Tenant> partialUpdateTenant(@PathVariable UUID id, @RequestBody Tenant tenant) {
        try {
            Tenant updatedTenant = tenantService.partialUpdate(id, tenant);
            logger.info("Tenant partially updated successfully: {}", id);
            return ResponseEntity.ok(updatedTenant);
        } catch (Exception e) {
            logger.error("Error partially updating tenant with id: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTenant(@PathVariable UUID id) {
        try {
            tenantService.deleteById(id);
            logger.info("Tenant deleted successfully: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting tenant with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/{id}/activate-subscription")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tenant> activateSubscription(@PathVariable UUID id, @RequestParam PlanType planType) {
        try {
            Tenant tenant = tenantService.activateSubscription(id, planType);
            logger.info("Subscription activated for tenant: {} with plan: {}", id, planType);
            return ResponseEntity.ok(tenant);
        } catch (Exception e) {
            logger.error("Error activating subscription for tenant: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/suspend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tenant> suspendTenant(@PathVariable UUID id, @RequestParam String reason) {
        try {
            Tenant tenant = tenantService.suspendTenant(id, reason);
            logger.info("Tenant suspended: {} with reason: {}", id, reason);
            return ResponseEntity.ok(tenant);
        } catch (Exception e) {
            logger.error("Error suspending tenant: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/{id}/upgrade-plan")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Tenant> upgradePlan(@PathVariable UUID id, @RequestParam PlanType newPlanType) {
        try {
            Tenant tenant = tenantService.upgradePlan(id, newPlanType);
            logger.info("Plan upgraded for tenant: {} to: {}", id, newPlanType);
            return ResponseEntity.ok(tenant);
        } catch (Exception e) {
            logger.error("Error upgrading plan for tenant: {}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}/usage-stats")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isTenantOwner(#id)")
    public ResponseEntity<TenantUsageStats> getTenantUsageStats(@PathVariable UUID id) {
        try {
            TenantUsageStats stats = tenantService.getTenantUsageStats(id);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error fetching usage stats for tenant: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/by-domain/{customDomain}")
    public ResponseEntity<Tenant> getTenantByCustomDomain(@PathVariable String customDomain) {
        try {
            return tenantService.findByCustomDomain(customDomain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error fetching tenant by domain: {}", customDomain, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/by-plan/{planType}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Iterable<Tenant>> getTenantsByPlanType(@PathVariable PlanType planType) {
        try {
            Iterable<Tenant> tenants = tenantService.findByPlanType(planType);
            return ResponseEntity.ok(tenants);
        } catch (Exception e) {
            logger.error("Error fetching tenants by plan type: {}", planType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
