package com.lanhcare.dto.meallog;

import com.lanhcare.entity.FoodItem;
import com.lanhcare.entity.MealLog;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealFoodResponse {
    private Integer id;
    private Integer quantity;
    private BigDecimal calories;
    private Integer foodItemId;
    private String foodItemName;
    private Integer mealLogId;
}
