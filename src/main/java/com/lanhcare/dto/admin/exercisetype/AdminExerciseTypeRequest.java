package com.lanhcare.dto.admin.exercisetype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for creating/updating exercise type (Admin)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminExerciseTypeRequest {
    
    @NotBlank(message = "Activity name is required")
    private String activity;
    
    private String examples;
    
    @NotNull(message = "MET value is required")
    private BigDecimal metValue;
}
