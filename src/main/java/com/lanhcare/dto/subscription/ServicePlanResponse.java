package com.lanhcare.dto.subscription;

import com.lanhcare.enums.PeriodUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * User-facing response DTO for Service Plan (features as List for mobile app)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePlanResponse {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer periodValue;
    private PeriodUnit periodUnit;
    private List<String> features;
}
