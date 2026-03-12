package com.lanhcare.dto.admin.subscription;

import com.lanhcare.enums.SubscriptionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Admin updating subscription status or extending
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUpdateSubscriptionRequest {
    
    @NotNull(message = "Status is required")
    private SubscriptionStatus status;
    
    /**
     * Number of days to extend (optional, only for extension)
     */
    private Integer extendDays;
}
