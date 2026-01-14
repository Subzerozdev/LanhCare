package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "exercise_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "duration_minute")
    private BigDecimal duration;

    @Column(name = "calories_out")
    private BigDecimal caloriesOut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_log_id", nullable = false)
    private DailyLog dailyLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_type_id", nullable = false)
    private ExerciseType exerciseType;

    /// ((MET x 3.5 x weightKg) / 200) x duration (minutes)
    public void calculateEAT() {
        caloriesOut = exerciseType.getMetValue()
                .multiply(BigDecimal.valueOf(3.5))
                .multiply(dailyLog.getAccount().getHealthProfile().getWeightKg())
                .divide(BigDecimal.valueOf(200), 2, RoundingMode.HALF_UP)
                .multiply(duration);
    }
}
