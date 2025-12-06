package com.lanhcare.dto.admin.nutrition;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Nutrient request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminNutrientRequest {
    
    @NotBlank(message = "Nutrient name is required")
    private String name;
    
    @NotBlank(message = "Unit is required")
    private String unit;
}
