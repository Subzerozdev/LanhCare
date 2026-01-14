package com.lanhcare.dto.exerciselog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseLogResponse {
    private Integer id;
    private Integer exerciseId;
    private Integer dailyLogId;
    private BigDecimal duration;
    private String activity;
    private BigDecimal metValue;
    private BigDecimal caloriesOut;
    private LocalDate dailyLogDate;
}