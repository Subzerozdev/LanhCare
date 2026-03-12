package com.lanhcare.dto.admin.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Admin Subscription detail response (includes transaction details)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSubscriptionDetailResponse {
    
    private Integer id;
    
    // User info
    private Integer userId;
    private String userName;
    private String userEmail;
    
    // Plan info
    private Integer servicePlanId;
    private String servicePlanName;
    private BigDecimal servicePlanPrice;
    
    // Subscription info
    private String startDate;
    private String endDate;
    private String status;
    
    // Transaction info
    private TransactionInfo transaction;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransactionInfo {
        private Integer id;
        private BigDecimal amount;
        private String paymentMethod;
        private String status;
        private String transactionDate;
    }
}
