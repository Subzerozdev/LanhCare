package com.lanhcare.dto.admin.serviceplan;

import com.lanhcare.enums.ServicePlanStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for changing Service Plan status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminChangeServicePlanStatusRequest {
    
    @NotNull(message = "Status is required")
    private ServicePlanStatus status;
}
