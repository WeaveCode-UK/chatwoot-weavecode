package com.weavecode.chatwoot.security;

import com.weavecode.chatwoot.entity.User;
import com.weavecode.chatwoot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    @Autowired
    private UserService userService;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            // Find user by email (we'll need to implement findByEmail in UserService)
            Optional<User> userOpt = userService.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                logger.warn("User not found with email: {}", email);
                throw new UsernameNotFoundException("User not found with email: " + email);
            }
            
            User user = userOpt.get();
            
            // Check if user is active
            if (!"active".equals(user.getStatus())) {
                logger.warn("User account is not active: {}", email);
                throw new UsernameNotFoundException("User account is not active: " + email);
            }
            
            // Create Spring Security UserDetails
            return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
                
        } catch (Exception e) {
            logger.error("Error loading user by email: {}", email, e);
            throw new UsernameNotFoundException("Error loading user: " + email, e);
        }
    }
    
    /**
     * Load user by email and tenant ID for multi-tenant authentication
     */
    public UserDetails loadUserByUsernameAndTenant(String email, String tenantId) throws UsernameNotFoundException {
        try {
            // Find user by email and tenant ID
            Optional<User> userOpt = userService.findByEmailAndTenantId(email, java.util.UUID.fromString(tenantId));
            
            if (userOpt.isEmpty()) {
                logger.warn("User not found with email: {} and tenant: {}", email, tenantId);
                throw new UsernameNotFoundException("User not found with email: " + email + " and tenant: " + tenantId);
            }
            
            User user = userOpt.get();
            
            // Check if user is active
            if (!"active".equals(user.getStatus())) {
                logger.warn("User account is not active: {} in tenant: {}", email, tenantId);
                throw new UsernameNotFoundException("User account is not active: " + email);
            }
            
            // Create Spring Security UserDetails with tenant context
            return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
                
        } catch (Exception e) {
            logger.error("Error loading user by email and tenant: {} - {}", email, tenantId, e);
            throw new UsernameNotFoundException("Error loading user: " + email, e);
        }
    }
}
