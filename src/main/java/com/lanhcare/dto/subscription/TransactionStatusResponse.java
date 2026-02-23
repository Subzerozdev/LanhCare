package com.lanhcare.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for transaction status polling (used by mobile app after VNPay payment)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionStatusResponse {
    private Integer transactionId;
    private String status;          // PENDING, COMPLETED, FAILED
    private String planName;
    private Integer planId;
    private Integer subscriptionId; // null if not yet active
    private String message;         // Human-readable status message
}
