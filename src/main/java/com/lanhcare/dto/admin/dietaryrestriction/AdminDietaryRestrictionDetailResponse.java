package com.lanhcare.dto.admin.dietaryrestriction;

import com.lanhcare.enums.Frequency;
import com.lanhcare.enums.LimitType;
import com.lanhcare.enums.RestrictionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for dietary restriction detail response (Admin)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDietaryRestrictionDetailResponse {
    
    private Integer id;
    private String name;
    private String description;
    private LimitType limitType;
    private BigDecimal limitValue;
    private String limitUnit;
    private Frequency frequency;
    private RestrictionStatus status;
    private String sourceOfAdvice;
    
    // User Health Profile info
    private Integer userHealthProfileId;
    private Integer accountId;
    private String accountEmail;
    private String accountFullname;
    
    // Nutrient info (if linked)
    private Integer nutrientId;
    private String nutrientName;
    private String nutrientUnit;
    
    // ICD Code info (if linked)
    private String icdUri;
    private String icdCode;
    private String icdTitle;
    private String icdDefinition;
}
