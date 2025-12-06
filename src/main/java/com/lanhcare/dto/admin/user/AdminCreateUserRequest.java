package com.lanhcare.dto.admin.user;

import com.lanhcare.entity.AccountRole;
import com.lanhcare.entity.AccountStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new user (Admin)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCreateUserRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullname;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotNull(message = "Role is required")
    private AccountRole role;
    
    @Builder.Default
    private AccountStatus status = AccountStatus.ACTIVE;
}
