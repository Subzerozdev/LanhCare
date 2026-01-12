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
    private int quantity;
    private int foodItemId;
    private int mealId;
}
