package com.lanhcare.dto.meallog;

import com.lanhcare.enums.MealType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealLogResponse {
    private Integer id;
    private int accountId;
    private MealType mealType;
    private LocalDate mealDate;
    private LocalTime loggedTime;
    private BigDecimal totalCalories;
    private String notes;
    private LocalDateTime createdAt;
    private List<MealFoodResponse> mealFoods = new ArrayList<>();
}
