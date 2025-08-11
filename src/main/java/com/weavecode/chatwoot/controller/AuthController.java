package com.weavecode.chatwoot.controller;

import com.weavecode.chatwoot.dto.LoginRequest;
import com.weavecode.chatwoot.dto.LoginResponse;
import com.weavecode.chatwoot.dto.RegisterRequest;
import com.weavecode.chatwoot.dto.RegisterResponse;
import com.weavecode.chatwoot.entity.Tenant;
import com.weavecode.chatwoot.entity.User;
import com.weavecode.chatwoot.enums.UserRole;
import com.weavecode.chatwoot.security.JwtTokenProvider;
import com.weavecode.chatwoot.service.TenantService;
import com.weavecode.chatwoot.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TenantService tenantService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            User user = userService.findByEmailAndTenantId(loginRequest.getEmail(), loginRequest.getTenantId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            String jwt = tokenProvider.generateTokenForUser(
                user.getId(), 
                user.getEmail(), 
                user.getTenantId()
            );
            
            LoginResponse response = new LoginResponse();
            response.setToken(jwt);
            response.setTokenType("Bearer");
            response.setUserId(user.getId());
            response.setTenantId(user.getTenantId());
            response.setEmail(user.getEmail());
            response.setRole(user.getRole());
            response.setExpiresIn(3600000); // 1 hour in milliseconds
            
            logger.info("User logged in successfully: {}", user.getEmail());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Login failed for user: {}", loginRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Check if tenant exists
            Tenant tenant = tenantService.findByIdOrThrow(registerRequest.getTenantId());
            
            // Check if user already exists
            if (userService.findByEmailAndTenantId(registerRequest.getEmail(), registerRequest.getTenantId()).isPresent()) {
                return ResponseEntity.badRequest()
                    .body(RegisterResponse.builder()
                        .success(false)
                        .message("User already exists with this email")
                        .build());
            }
            
            // Create new user
            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setRole(UserRole.AGENT);
            user.setTenantId(registerRequest.getTenantId());
            user.setStatus("active");
            
            User savedUser = userService.create(user);
            
            RegisterResponse response = RegisterResponse.builder()
                .success(true)
                .message("User registered successfully")
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .build();
            
            logger.info("User registered successfully: {}", savedUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Registration failed for user: {}", registerRequest.getEmail(), e);
            return ResponseEntity.badRequest()
                .body(RegisterResponse.builder()
                    .success(false)
                    .message("Registration failed: " + e.getMessage())
                    .build());
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                if (tokenProvider.validateToken(token)) {
                    String email = tokenProvider.getUsernameFromToken(token);
                    UUID userId = tokenProvider.getUserIdFromToken(token);
                    UUID tenantId = tokenProvider.getTenantIdFromToken(token);
                    
                    String newToken = tokenProvider.generateTokenForUser(userId, email, tenantId);
                    
                    LoginResponse response = new LoginResponse();
                    response.setToken(newToken);
                    response.setTokenType("Bearer");
                    response.setUserId(userId);
                    response.setTenantId(tenantId);
                    response.setEmail(email);
                    response.setExpiresIn(3600000);
                    
                    return ResponseEntity.ok(response);
                }
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
        } catch (Exception e) {
            logger.error("Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
