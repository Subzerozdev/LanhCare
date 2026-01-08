package com.lanhcare.dto.admin.serviceplan;

import com.lanhcare.enums.PeriodUnit;
import com.lanhcare.enums.ServicePlanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Detailed Response DTO for Service Plan in Admin panel
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminServicePlanDetailResponse {
    private Integer id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer periodValue;
    private PeriodUnit periodUnit;
    private ServicePlanStatus status;
    private Long transactionCount;
    private Long activeSubscriptions;
    private BigDecimal totalRevenue;
}
