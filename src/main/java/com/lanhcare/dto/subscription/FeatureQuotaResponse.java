package com.lanhcare.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureQuotaResponse {
    private String featureCode;
    private int used;
    private int limit;      // -1 means unlimited
    private int remaining;   // -1 means unlimited
    private boolean allowed;
}
