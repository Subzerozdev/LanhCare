package com.lanhcare.dto.admin.nutrition;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating/updating FoodNutrient
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminFoodNutrientRequest {
    
    @NotNull(message = "Nutrient ID is required")
    private Integer nutrientId;
    
    @NotNull(message = "Value is required")
    @DecimalMin(value = "0.0", message = "Value must be >= 0")
    private BigDecimal value;
}

