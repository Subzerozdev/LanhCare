package com.lanhcare.dto.admin.dietaryrestriction;

import com.lanhcare.enums.Frequency;
import com.lanhcare.enums.LimitType;
import com.lanhcare.enums.RestrictionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating/updating dietary restriction (Admin)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDietaryRestrictionRequest {
    
    @NotNull(message = "User health profile ID is required")
    private Integer userHealthProfileId;
    
    private Integer nutrientId;
    
    private String icdUri;
    
    private String name;
    
    private String description;
    
    private LimitType limitType;
    
    private BigDecimal limitValue;
    
    private String limitUnit;
    
    private Frequency frequency;
    
    @Builder.Default
    private RestrictionStatus status = RestrictionStatus.ACTIVE;
    
    private String sourceOfAdvice;
}
