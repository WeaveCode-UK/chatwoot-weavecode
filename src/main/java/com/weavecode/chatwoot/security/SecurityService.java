package com.weavecode.chatwoot.security;

import com.weavecode.chatwoot.entity.Tenant;
import com.weavecode.chatwoot.entity.User;
import com.weavecode.chatwoot.service.TenantService;
import com.weavecode.chatwoot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SecurityService {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TenantService tenantService;
    
    /**
     * Check if the current user is a tenant owner
     */
    public boolean isTenantOwner(UUID tenantId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }
            
            String email = authentication.getName();
            Optional<User> userOpt = userService.findByEmailAndTenantId(email, tenantId);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return user.getRole().name().equals("ADMIN") || user.getRole().name().equals("OWNER");
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Error checking tenant ownership for tenant: {}", tenantId, e);
            return false;
        }
    }
    
    /**
     * Check if the current user is the specified user
     */
    public boolean isCurrentUser(UUID userId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }
            
            String email = authentication.getName();
            Optional<User> userOpt = userService.findByEmail(email);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                return user.getId().equals(userId);
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Error checking if user is current user: {}", userId, e);
            return false;
        }
    }
    
    /**
     * Get the current authenticated user
     */
    public Optional<User> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.empty();
            }
            
            String email = authentication.getName();
            return userService.findByEmail(email);
        } catch (Exception e) {
            logger.error("Error getting current user", e);
            return Optional.empty();
        }
    }
    
    /**
     * Get the current user's tenant ID
     */
    public Optional<UUID> getCurrentUserTenantId() {
        try {
            Optional<User> currentUser = getCurrentUser();
            return currentUser.map(User::getTenantId);
        } catch (Exception e) {
            logger.error("Error getting current user tenant ID", e);
            return Optional.empty();
        }
    }
    
    /**
     * Check if the current user has access to a specific tenant
     */
    public boolean hasAccessToTenant(UUID tenantId) {
        try {
            Optional<UUID> currentTenantId = getCurrentUserTenantId();
            return currentTenantId.isPresent() && currentTenantId.get().equals(tenantId);
        } catch (Exception e) {
            logger.error("Error checking tenant access for tenant: {}", tenantId, e);
            return false;
        }
    }
    
    /**
     * Check if the current user has a specific role
     */
    public boolean hasRole(String role) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }
            
            return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role));
        } catch (Exception e) {
            logger.error("Error checking user role: {}", role, e);
            return false;
        }
    }
    
    /**
     * Check if the current user is a super admin
     */
    public boolean isSuperAdmin() {
        return hasRole("SUPER_ADMIN");
    }
    
    /**
     * Check if the current user is a tenant admin
     */
    public boolean isTenantAdmin() {
        return hasRole("ADMIN") || hasRole("OWNER");
    }
}
