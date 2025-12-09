package com.lanhcare.dto.admin.revenue;

import com.lanhcare.entity.TransactionStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating Transaction by Admin
 * Used for manual transaction entry (e.g., offline payments, adjustments)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCreateTransactionRequest {
    
    @NotNull(message = "User ID is required")
    private Integer userId;
    
    @NotNull(message = "Service Plan ID is required")
    private Integer servicePlanId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    private String paymentMethod;
    
    @Builder.Default
    private TransactionStatus status = TransactionStatus.COMPLETED;
    
    private String note;  // Optional note for admin reference
}

