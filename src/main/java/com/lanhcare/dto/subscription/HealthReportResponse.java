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
public class HealthReportResponse {
    private String period;          // "WEEKLY" or "FULL"
    private LocalDate startDate;
    private LocalDate endDate;

    // Summary stats
    private int daysLogged;
    private BigDecimal avgCaloriesIn;
    private BigDecimal avgCaloriesOut;
    private BigDecimal calorieBalance;  // avg in - avg out
    private int avgSteps;
    private int totalMeals;
    private int totalExercises;

    // Health Profile snapshot
    private BigDecimal weightKg;
    private BigDecimal bmiValue;
    private String bmiStatus;
    private String healthGoal;

    // Only for FULL report
    private List<String> healthTips;
    private List<DailyDetail> dailyDetails;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyDetail {
        private LocalDate date;
        private BigDecimal caloriesIn;
        private BigDecimal caloriesOut;
        private int steps;
        private int mealCount;
        private int exerciseCount;
    }
}
