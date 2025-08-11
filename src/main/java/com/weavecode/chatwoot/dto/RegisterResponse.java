package com.weavecode.chatwoot.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RegisterResponse {
    
    private boolean success;
    private String message;
    private UUID userId;
    private String email;
}
