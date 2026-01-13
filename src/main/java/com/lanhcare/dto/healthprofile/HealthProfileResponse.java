package com.lanhcare.dto.healthprofile;

import com.lanhcare.enums.Gender;
import com.lanhcare.enums.HealthGoal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthProfileResponse {
    private Integer id;
    private LocalDate dateOfBirth;
    private Gender gender;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private BigDecimal bmiValue;
    private BigDecimal tddeValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String bmiStatus;
    private String bmiStatusDescription;
    private String activityLevel;
    private String activityLevelDescription;
    private String healthGoal;
    private String healthGoalDescription;
}
