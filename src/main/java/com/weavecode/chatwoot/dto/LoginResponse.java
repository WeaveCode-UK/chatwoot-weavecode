package com.weavecode.chatwoot.dto;

import com.weavecode.chatwoot.enums.UserRole;
import lombok.Data;

import java.util.UUID;

@Data
public class LoginResponse {
    
    private String token;
    private String tokenType;
    private UUID userId;
    private UUID tenantId;
    private String email;
    private UserRole role;
    private long expiresIn;
}
