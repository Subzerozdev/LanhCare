package com.lanhcare.dto.admin.user;

import com.lanhcare.enums.AccountRole;
import com.lanhcare.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for User list response (Admin)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserResponse {
    
    private Integer id;
    private String email;
    private String fullname;
    private AccountRole role;
    private AccountStatus status;
    private Integer transactionCount;
    private BigDecimal totalSpent;
}
