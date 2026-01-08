package com.lanhcare.dto.admin.serviceplan;

import com.lanhcare.enums.PeriodUnit;
import com.lanhcare.enums.ServicePlanStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for updating Service Plan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateServicePlanRequest {
    
    private String name;
    private String description;
    
    @DecimalMin(value = "0.0", message = "Price must be greater than or equal to 0")
    private BigDecimal price;
    
    @Min(value = 1, message = "Period value must be at least 1")
    private Integer periodValue;
    
    private PeriodUnit periodUnit;
    private ServicePlanStatus status;
}
