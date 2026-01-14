package com.lanhcare.dto.meallog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealFoodRequest {
    private Integer quantity;
    private Integer foodItemId;
    private Integer mealId;
}
