package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "daily_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "meal_date")
    private LocalDate loggedDate;

    @Column(name = "step_amount")
    private Integer stepAmount;

    @Column(name = "total_calories_in")
    private BigDecimal totalCaloriesIn;

    @Column(name = "total_calories_out")
    private BigDecimal totalCaloriesOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MealLog> mealLogs = new ArrayList<>();

    @OneToMany(mappedBy = "dailyLog", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExerciseLog> exerciseLogs = new ArrayList<>();

    public void calculateCaloriesIn() {
        totalCaloriesIn = mealLogs.stream()
                .map(MealLog::getTotalCalories)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /// Tdde daily = BMR + EAT + NEAT + TEF
    public void calculateCaloriesOut(){
        BigDecimal weightKg = account.getHealthProfile().getWeightKg();
        totalCaloriesOut = BigDecimal.ZERO
                .add(account.getHealthProfile().calculateBMR())
                .add(calculateTotalEAT())
                .add(calculateNEAT(weightKg))
                .add(calculateTEF());
    }

    private BigDecimal calculateTotalEAT(){
        return exerciseLogs.stream()
                .map(ExerciseLog::getCaloriesOut)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /// weighKg x steps x 0.0006125
    public BigDecimal calculateNEAT(BigDecimal weightKg){
        return weightKg
                .multiply(BigDecimal.valueOf(stepAmount == null ? 0 : stepAmount))
                .multiply(BigDecimal.valueOf(0.0006125));
    }

    /// 10% of Calories In
    public BigDecimal calculateTEF(){
        return totalCaloriesIn.divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP);
    }
}
