package com.lanhcare.dto.account;

import com.lanhcare.entity.AccountRole;
import com.lanhcare.entity.AccountStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating account information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountRequest {
    
    @Email(message = "Email must be valid")
    private String email;
    
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullname;
    
    private AccountRole role;
    private AccountStatus status;
}
