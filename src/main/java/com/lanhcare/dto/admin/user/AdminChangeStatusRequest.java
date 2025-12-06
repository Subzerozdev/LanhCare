package com.lanhcare.dto.admin.user;

import com.lanhcare.entity.AccountStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for changing user status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminChangeStatusRequest {
    
    @NotNull(message = "Status is required")
    private AccountStatus status;
}
