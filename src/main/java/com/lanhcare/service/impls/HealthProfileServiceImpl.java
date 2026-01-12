package com.lanhcare.service.impls;

import com.lanhcare.dto.healthprofile.HealthProfileRequest;
import com.lanhcare.dto.healthprofile.HealthProfileResponse;
import com.lanhcare.entity.Account;
import com.lanhcare.entity.UserHealthProfile;
import com.lanhcare.enums.BMIStatus;
import com.lanhcare.exception.exps.AuthenticationException;
import com.lanhcare.exception.exps.HealthProfileException;
import com.lanhcare.repository.AccountRepository;
import com.lanhcare.repository.UserHealthProfileRepository;
import com.lanhcare.service.HealthProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HealthProfileServiceImpl implements HealthProfileService {
    private final UserHealthProfileRepository healthProfileRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public UserHealthProfile create(HealthProfileRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AuthenticationException("Account not found with ID: " + request.getAccountId()));

        BigDecimal bmiValue = calculateBMI(request.getWeightKg(), request.getHeightCm());
        BMIStatus status = getStatusByBMI(bmiValue);

        UserHealthProfile profile = UserHealthProfile.builder()
                .account(account)
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .heightCm(request.getHeightCm())
                .weightKg(request.getWeightKg())
                .activityLevel(request.getActivityLevel())
                .bmiValue(bmiValue)
                .bmiStatus(status)
                .healthGoals(request.getHealthGoals())
                .build();

        return healthProfileRepository.save(profile);
    }

    private BigDecimal calculateBMI(BigDecimal weightKg, BigDecimal heightCm) {
        if (weightKg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new HealthProfileException("Weight Kg should be greater than 0");
        }
        if (heightCm.compareTo(BigDecimal.ZERO) <= 0) {
            throw new HealthProfileException("Height Cm should be greater than 0");
        }

        return weightKg.divide(heightCm.multiply(heightCm), 2, RoundingMode.HALF_UP);
    }

    private BMIStatus getStatusByBMI(BigDecimal bmiValue) {
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

    private boolean isInBMIStatus(BigDecimal bmiValue, BMIStatus status) {
        if(status.equals(BMIStatus.UNDEFINED)) {
            return false;
        }

        return bmiValue.compareTo(status.getMinBmi()) >= 0
                && bmiValue.compareTo(status.getMaxBmi()) <= 0;
    }

    @Override
    @Transactional
    public UserHealthProfile update(HealthProfileRequest request) {
        UserHealthProfile existingProfile = healthProfileRepository.findByAccountId(request.getAccountId())
                .orElseThrow(() -> new AuthenticationException("Health profile not found for account: " + request.getAccountId()));

        Optional.ofNullable(request.getDateOfBirth()).ifPresent(existingProfile::setDateOfBirth);
        Optional.ofNullable(request.getGender()).ifPresent(existingProfile::setGender);
        Optional.ofNullable(request.getHeightCm()).ifPresent(existingProfile::setHeightCm);
        Optional.ofNullable(request.getWeightKg()).ifPresent(existingProfile::setWeightKg);
        Optional.ofNullable(request.getActivityLevel()).ifPresent(existingProfile::setActivityLevel);
        Optional.ofNullable(request.getHealthGoals()).ifPresent(existingProfile::setHealthGoals);

        BigDecimal bmiValue = calculateBMI(existingProfile.getWeightKg(), existingProfile.getHeightCm());
        BMIStatus status = getStatusByBMI(bmiValue);
        existingProfile.setBmiValue(bmiValue);
        existingProfile.setBmiStatus(status);


        return healthProfileRepository.save(existingProfile);
    }

    @Override
    @Transactional
    public void delete(int profileId) {
        UserHealthProfile profile = getById(profileId);
        healthProfileRepository.delete(profile);
    }

    @Override
    public UserHealthProfile getByAccountId(int accountId) {
        return healthProfileRepository.findByAccountId(accountId)
                .orElseThrow(() -> new HealthProfileException("No health profile found for account: " + accountId));
    }

    @Override
    public UserHealthProfile getById(int id) {
        return healthProfileRepository.findById(id)
                .orElseThrow(() -> new HealthProfileException("Profile not found with ID: " + id));
    }

    @Override
    public HealthProfileResponse mapToResponse(UserHealthProfile healthProfile) {
        if (healthProfile == null) return null;

        return HealthProfileResponse.builder()
                .id(healthProfile.getId())
                .dateOfBirth(healthProfile.getDateOfBirth())
                .gender(healthProfile.getGender())
                .heightCm(healthProfile.getHeightCm())
                .weightKg(healthProfile.getWeightKg())
                .activityLevel(healthProfile.getActivityLevel())
                .bmiValue(healthProfile.getBmiValue())
                .bmiStatus(healthProfile.getBmiStatus().getName())
                .bmiStatusDescription(healthProfile.getBmiStatus().getDescription())
                .healthGoals(healthProfile.getHealthGoals())
                .createdAt(healthProfile.getCreatedAt())
                .updatedAt(healthProfile.getUpdatedAt())
                .build();
    }
}
