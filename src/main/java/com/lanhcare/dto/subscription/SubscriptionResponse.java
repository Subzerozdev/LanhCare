package com.lanhcare.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for Subscription information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {
    private Integer id;
    private Integer planId;
    private String planName;
    private String planDescription;
    private BigDecimal planPrice;
    private List<String> features;
    private String startDate;
    private String endDate;
    private String status;
}
