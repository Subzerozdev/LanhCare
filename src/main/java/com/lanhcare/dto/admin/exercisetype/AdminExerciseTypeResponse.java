package com.lanhcare.dto.admin.exercisetype;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for exercise type response (Admin)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminExerciseTypeResponse {
    
    private Integer id;
    private String activity;
    private String examples;
    private BigDecimal metValue;
    private Boolean deleted;
}
