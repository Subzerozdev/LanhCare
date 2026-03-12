package com.lanhcare.dto.admin.useractivity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Admin User Activity overview
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserActivityResponse {
    
    private String loggedDate;
    private Integer stepAmount;
    private BigDecimal totalCaloriesIn;
    private BigDecimal totalCaloriesOut;
    
    private List<MealSummary> meals;
    private List<ExerciseSummary> exercises;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MealSummary {
        private Integer id;
        private String mealType;
        private BigDecimal totalCalories;
        private Integer foodCount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ExerciseSummary {
        private Integer id;
        private String exerciseTypeName;
        private Integer durationMinutes;
        private BigDecimal caloriesOut;
    }
}
