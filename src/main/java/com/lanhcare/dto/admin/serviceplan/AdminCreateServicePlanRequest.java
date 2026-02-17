package com.lanhcare.dto.admin.serviceplan;

import com.lanhcare.enums.PeriodUnit;
import com.lanhcare.enums.ServicePlanStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating Service Plan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateServicePlanRequest {
    
    @NotBlank(message = "Service plan name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be greater than or equal to 0")
    private BigDecimal price;
    
    @NotNull(message = "Period value is required")
    @Min(value = 1, message = "Period value must be at least 1")
    private Integer periodValue;
    
    @NotNull(message = "Period unit is required")
    private PeriodUnit periodUnit;
    
    private ServicePlanStatus status;
    
    private String features;  // Comma-separated feature codes, e.g. "DAILY_LOG,AI_CHAT,FORUM_POST"
}
