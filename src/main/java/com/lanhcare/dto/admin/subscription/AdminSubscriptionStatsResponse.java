package com.lanhcare.dto.admin.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for Admin Subscription statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSubscriptionStatsResponse {
    
    private Long totalSubscriptions;
    private Long activeSubscriptions;
    private Long expiredSubscriptions;
    private Long cancelledSubscriptions;
    private Long pendingSubscriptions;
    private Long expiringSoon7Days;
    
    private List<PlanSubscriptionCount> subscriptionsByPlan;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlanSubscriptionCount {
        private Integer servicePlanId;
        private String servicePlanName;
        private Long activeCount;
    }
}
