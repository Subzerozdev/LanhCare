package com.lanhcare.dto.admin.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Admin Subscription list response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSubscriptionResponse {
    
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
    private Integer transactionId;
}
