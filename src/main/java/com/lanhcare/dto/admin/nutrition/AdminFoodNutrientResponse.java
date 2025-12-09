package com.lanhcare.dto.admin.nutrition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for FoodNutrient response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminFoodNutrientResponse {
    
    private Integer id;
    private Integer foodItemId;
    private String foodItemName;
    private Integer nutrientId;
    private String nutrientName;
    private String nutrientUnit;
    private BigDecimal value;
    
    // Computed: value with unit (e.g., "25.5 mg")
    private String displayValue;
}

