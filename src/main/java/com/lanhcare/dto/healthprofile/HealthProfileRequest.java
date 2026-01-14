package com.lanhcare.dto.healthprofile;

import com.lanhcare.enums.healthprofile.ActivityLevel;
import com.lanhcare.enums.healthprofile.Gender;
import com.lanhcare.enums.healthprofile.HealthGoal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthProfileRequest {
    private Integer accountId;
    private LocalDate dateOfBirth;
    private Gender gender;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private ActivityLevel activityLevel;
    private HealthGoal healthGoal;
}
