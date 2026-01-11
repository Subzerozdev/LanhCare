package com.lanhcare.dto.healthprofile;

import com.lanhcare.enums.ActivityLevel;
import com.lanhcare.enums.Gender;
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
    private ActivityLevel activityLevel;
    private BigDecimal bmiValue;
    private String healthGoals;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
