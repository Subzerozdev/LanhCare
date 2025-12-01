package com.lanhcare.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response containing JWT token and user info
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    private String accessToken;
    private String tokenType = "Bearer";
    private Integer userId;
    private String email;
    private String fullname;
    private String role;
    
    public AuthResponse(String accessToken, Integer userId, String email, String fullname, String role) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.email = email;
        this.fullname = fullname;
        this.role = role;
    }
}
