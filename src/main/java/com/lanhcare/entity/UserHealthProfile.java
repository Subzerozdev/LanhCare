package com.lanhcare.entity;

import com.lanhcare.enums.ActivityLevel;
import com.lanhcare.enums.BMIStatus;
import com.lanhcare.enums.Gender;
import com.lanhcare.enums.HealthGoal;
import com.lanhcare.exception.exps.HealthProfileException;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "UserHealthProfile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserHealthProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @Column(name = "height_cm", precision = 5, scale = 2)
    private BigDecimal heightCm;

    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", length = 50)
    private ActivityLevel activityLevel;

    @Column(name = "bmi_value", precision = 5, scale = 2)
    private BigDecimal bmiValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "bmi_status")
    private BMIStatus bmiStatus;

    @Column(name = "tdde_value", scale = 2)
    private BigDecimal tddeValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "health_goals")
    private HealthGoal healthGoals;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "userHealthProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DietaryRestriction> dietaryRestrictions = new ArrayList<>();

    public BigDecimal calculateBMI() {
        if (weightKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new HealthProfileException("Weight Kg should be greater than 0");
        }
        if (heightCm.compareTo(BigDecimal.ZERO) <= 0) {
            throw new HealthProfileException("Height Cm should be greater than 0");
        }

        BigDecimal heightM = heightCm.divide(BigDecimal.valueOf(100));
        return weightKg.divide(heightM.multiply(heightM), 2, RoundingMode.HALF_UP);
    }

    public BMIStatus getStatusByBMI() {
        // Out of BMI Status Range
        if (bmiValue.compareTo(BigDecimal.valueOf(204.0)) >= 0) {
            return BMIStatus.OBESE_II;
        }

        // Loop for checking Status
        for (BMIStatus status : BMIStatus.values()) {
            if (isInBMIStatus(bmiValue, status)) {
                return status;
            }
        }

        return BMIStatus.UNDEFINED;
    }

    public boolean isInBMIStatus(BigDecimal bmiValue, BMIStatus status) {
        if (status.equals(BMIStatus.UNDEFINED)) {
            return false;
        }

        return bmiValue.compareTo(status.getMinBmi()) >= 0
                && bmiValue.compareTo(status.getMaxBmi()) <= 0;
    }

    public BigDecimal calculateTDDE(){
        if(activityLevel.getRFactor() == null){
            return BigDecimal.ZERO;
        }

        return calculateBMR().multiply(activityLevel.getRFactor());
    }

    public BigDecimal calculateBMR() {
        if (dateOfBirth == null ||
                weightKg.compareTo(BigDecimal.ZERO) <= 0 ||
                heightCm.compareTo(BigDecimal.ZERO) <= 0)
        {
            return BigDecimal.ZERO;
        }

        long age = ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());

        BigDecimal commonBMR = weightKg.multiply(BigDecimal.TEN)
                .add(BigDecimal.valueOf(6.25).multiply(heightCm))
                .subtract(BigDecimal.valueOf(5).multiply(BigDecimal.valueOf(age)));

        return switch (gender) {
            case MALE -> commonBMR.add(BigDecimal.valueOf(5));
            case FEMALE -> commonBMR.subtract(BigDecimal.valueOf(161));
        };
    }
}
