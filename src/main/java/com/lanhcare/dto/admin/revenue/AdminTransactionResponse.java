package com.lanhcare.dto.admin.revenue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Transaction response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTransactionResponse {
    
    private Integer id;
    private Integer userId;
    private String userEmail;
    private String userName;
    private String servicePlanName;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String transactionDate;
}
