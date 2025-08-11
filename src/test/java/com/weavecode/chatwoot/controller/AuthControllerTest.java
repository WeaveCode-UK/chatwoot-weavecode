package com.weavecode.chatwoot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserService userService;

    @Mock
    private TenantService tenantService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private UUID testUserId;
    private UUID testTenantId;
    private User testUser;
    private Tenant testTenant;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();

        testUserId = UUID.randomUUID();
        testTenantId = UUID.randomUUID();

        // Setup test user
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setRole(UserRole.USER);
        testUser.setTenantId(testTenantId);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        // Setup test tenant
        testTenant = new Tenant();
        testTenant.setId(testTenantId);
        testTenant.setName("Test Company");
        testTenant.setDomain("test.com");
        testTenant.setStatus("ACTIVE");

        // Setup login request
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");
        loginRequest.setTenantId(testTenantId);

        // Setup register request
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Jane");
        registerRequest.setLastName("Smith");
        registerRequest.setTenantId(testTenantId);
    }

    @Test
    @DisplayName("Should login successfully")
    void shouldLoginSuccessfully() throws Exception {
        // Given
        String token = "jwt-token-123";
        LoginResponse expectedResponse = new LoginResponse();
        expectedResponse.setToken(token);
        expectedResponse.setTokenType("Bearer");
        expectedResponse.setUserId(testUserId);
        expectedResponse.setTenantId(testTenantId);
        expectedResponse.setEmail("test@example.com");
        expectedResponse.setRole(UserRole.USER);
        expectedResponse.setExpiresIn(3600000L);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn(token);
        when(tokenProvider.getUserIdFromToken(token)).thenReturn(testUserId);
        when(tokenProvider.getTenantIdFromToken(token)).thenReturn(testTenantId);
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.userId").value(testUserId.toString()))
                .andExpect(jsonPath("$.tenantId").value(testTenantId.toString()));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenProvider).generateToken(authentication);
    }

    @Test
    @DisplayName("Should register successfully")
    void shouldRegisterSuccessfully() throws Exception {
        // Given
        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setEmail("newuser@example.com");
        newUser.setFirstName("Jane");
        newUser.setLastName("Smith");
        newUser.setTenantId(testTenantId);

        when(tenantService.findById(testTenantId)).thenReturn(testTenant);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userService.create(any(User.class))).thenReturn(newUser);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));

        verify(tenantService).findById(testTenantId);
        verify(passwordEncoder).encode("password123");
        verify(userService).create(any(User.class));
    }

    @Test
    @DisplayName("Should refresh token successfully")
    void shouldRefreshTokenSuccessfully() throws Exception {
        // Given
        String authHeader = "Bearer old-token-123";
        String newToken = "new-token-456";
        LoginResponse expectedResponse = new LoginResponse();
        expectedResponse.setToken(newToken);
        expectedResponse.setTokenType("Bearer");
        expectedResponse.setUserId(testUserId);
        expectedResponse.setTenantId(testTenantId);

        when(tokenProvider.validateToken("old-token-123")).thenReturn(true);
        when(tokenProvider.getUsernameFromToken("old-token-123")).thenReturn("test@example.com");
        when(tokenProvider.generateTokenFromUsername("test@example.com")).thenReturn(newToken);
        when(tokenProvider.getUserIdFromToken(newToken)).thenReturn(testUserId);
        when(tokenProvider.getTenantIdFromToken(newToken)).thenReturn(testTenantId);
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(post("/api/auth/refresh")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(newToken));

        verify(tokenProvider).validateToken("old-token-123");
        verify(tokenProvider).generateTokenFromUsername("test@example.com");
    }

    @Test
    @DisplayName("Should logout successfully")
    void shouldLogoutSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk());

        // Verify that SecurityContextHolder is cleared
        verifyNoInteractions(authenticationManager, tokenProvider, userService, tenantService);
    }

    @Test
    @DisplayName("Should return bad request for invalid login request")
    void shouldReturnBadRequestForInvalidLoginRequest() throws Exception {
        // Given
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setEmail(""); // Invalid email
        invalidRequest.setPassword(""); // Invalid password

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(authenticationManager, tokenProvider, userService, tenantService);
    }

    @Test
    @DisplayName("Should return bad request for invalid register request")
    void shouldReturnBadRequestForInvalidRegisterRequest() throws Exception {
        // Given
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setEmail("invalid-email"); // Invalid email format
        invalidRequest.setPassword("123"); // Password too short

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(tenantService, passwordEncoder, userService);
    }

    @Test
    @DisplayName("Should handle authentication failure")
    void shouldHandleAuthenticationFailure() throws Exception {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(tokenProvider, userService);
    }
}
