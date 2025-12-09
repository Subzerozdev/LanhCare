package com.lanhcare.dto.admin.revenue;

import com.lanhcare.entity.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating Transaction status
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUpdateTransactionStatusRequest {
    
    @NotNull(message = "Status is required")
    private TransactionStatus status;
    
    private String note;  // Optional note for status change (e.g., reason for cancellation)
}

