package com.lanhcare.dto.exerciselog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseLogRequest {
    private BigDecimal duration;
    private Integer exerciseTypeId;
    private Integer dailyLogId;
}