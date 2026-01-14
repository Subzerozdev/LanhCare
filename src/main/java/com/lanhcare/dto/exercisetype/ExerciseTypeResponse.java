package com.lanhcare.dto.exercisetype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseTypeResponse {
    private Integer id;
    private String activity;
    private String examples;
    private BigDecimal metValue;
}