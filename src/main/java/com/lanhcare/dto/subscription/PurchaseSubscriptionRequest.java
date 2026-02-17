package com.lanhcare.dto.subscription;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for purchasing a subscription
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseSubscriptionRequest {

    @NotNull(message = "Service Plan ID is required")
    private Integer servicePlanId;
}
