package com.lanhcare.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardProResponse {

    private int streakDays;                     // consecutive days with logs
    private BigDecimal goalProgress;            // % towards health goal (0-100)
    private String healthGoal;

    private List<DayTrend> weeklyCalorieTrend;  // last 7 days
    private List<DayTrend> weeklyStepsTrend;

    private List<ExerciseRank> topExercises;    // top 3 most frequent
    private NutritionBreakdown nutritionBreakdown;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayTrend {
        private LocalDate date;
        private BigDecimal caloriesIn;
        private BigDecimal caloriesOut;
        private int steps;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseRank {
        private String activity;
        private int count;
        private BigDecimal totalCalories;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionBreakdown {
        private int breakfastCount;
        private int lunchCount;
        private int dinnerCount;
        private int snackCount;
    }
}
