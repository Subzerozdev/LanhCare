package com.lanhcare.dto.admin.revenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Revenue Statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueStatsResponse {
    
    private BigDecimal totalRevenue;
    private Long totalTransactions;
    private Long completedTransactions;
    private Long pendingTransactions;
    private Long failedTransactions;
    private BigDecimal averageTransactionValue;
    
    private List<MonthlyRevenue> revenueByMonth;
    private List<ServicePlanRevenue> revenueByServicePlan;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyRevenue {
        private String month;
        private BigDecimal revenue;
        private Long transactionCount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ServicePlanRevenue {
        private Integer servicePlanId;
        private String servicePlanName;
        private BigDecimal revenue;
        private Long transactionCount;
    }
}
