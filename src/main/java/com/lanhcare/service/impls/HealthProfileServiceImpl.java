package com.lanhcare.service.impls;

import com.lanhcare.dto.healthprofile.HealthProfileRequest;
import com.lanhcare.dto.healthprofile.HealthProfileResponse;
import com.lanhcare.entity.Account;
import com.lanhcare.entity.UserHealthProfile;
import com.lanhcare.exception.exps.AuthenticationException;
import com.lanhcare.exception.exps.HealthProfileException;
import com.lanhcare.repository.AccountRepository;
import com.lanhcare.repository.UserHealthProfileRepository;
import com.lanhcare.service.HealthProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

        UserHealthProfile profile = UserHealthProfile.builder()
                .account(account)
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .heightCm(request.getHeightCm())
                .weightKg(request.getWeightKg())
                .activityLevel(request.getActivityLevel())
                .healthGoals(request.getHealthGoal())
                .build();

        profile.setBmiValue(profile.calculateBMI());
        profile.setBmiStatus(profile.getStatusByBMI());
        profile.setTddeValue(profile.calculateTDDE());

        return healthProfileRepository.save(profile);
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
        Optional.ofNullable(request.getHealthGoal()).ifPresent(existingProfile::setHealthGoals);

        existingProfile.setBmiValue(existingProfile.calculateBMI());
        existingProfile.setBmiStatus(existingProfile.getStatusByBMI());
        existingProfile.setTddeValue(existingProfile.calculateTDDE());

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
                .bmiValue(healthProfile.getBmiValue())
                .bmiStatus(healthProfile.getBmiStatus().getName())
                .bmiStatusDescription(healthProfile.getBmiStatus().getDescription())
                .tddeValue(healthProfile.getTddeValue())
                .activityLevel(healthProfile.getActivityLevel().getDisplayName())
                .activityLevelDescription(healthProfile.getActivityLevel().getDescription())
                .healthGoal(healthProfile.getHealthGoals().getDisplayName())
                .healthGoalDescription(healthProfile.getHealthGoals().getDescription())
                .createdAt(healthProfile.getCreatedAt())
                .updatedAt(healthProfile.getUpdatedAt())
                .build();
    }
}
