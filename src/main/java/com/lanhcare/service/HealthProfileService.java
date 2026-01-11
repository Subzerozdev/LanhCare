package com.lanhcare.service;

import com.lanhcare.dto.healthprofile.HealthProfileRequest;
import com.lanhcare.dto.healthprofile.HealthProfileResponse;
import com.lanhcare.entity.UserHealthProfile;

public interface HealthProfileService {
    UserHealthProfile create(HealthProfileRequest request);
    UserHealthProfile update(HealthProfileRequest request);
    void delete(int profileId);
    UserHealthProfile getByAccountId(int accountId);
    UserHealthProfile getById(int id);
    HealthProfileResponse mapToResponse(UserHealthProfile healthProfile);
}
