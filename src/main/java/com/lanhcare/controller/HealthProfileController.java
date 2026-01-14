package com.lanhcare.controller;

import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.healthprofile.HealthProfileRequest;
import com.lanhcare.dto.healthprofile.HealthProfileResponse;
import com.lanhcare.entity.UserHealthProfile;
import com.lanhcare.security.JwtTokenProvider;
import com.lanhcare.service.AccountService;
import com.lanhcare.service.HealthProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-profiles")
@RequiredArgsConstructor
@Tag(name = "User - Health Profile", description = "APIs for managing user health profiles")
public class HealthProfileController {
    private final HealthProfileService healthProfileService;
    private final AccountService accountService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping
    @Operation(summary = "Create health profile", description = "Create a new health profile for an account")
    public ResponseEntity<ApiResponse<HealthProfileResponse>> create(
            @RequestBody HealthProfileRequest request,
            @RequestHeader("Authorization") String token
    ) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        int accountId = accountService.getAccountByEmail(email).getId();
        request.setAccountId(accountId);

        UserHealthProfile profile = healthProfileService.create(request);
        HealthProfileResponse response = healthProfileService.mapToResponse(profile);
        return ResponseEntity.ok(ApiResponse.success("Health profile created successfully", response));
    }

    @PutMapping
    @Operation(summary = "Update health profile", description = "Update existing health profile using accountId from request")
    public ResponseEntity<ApiResponse<HealthProfileResponse>> update(
            @RequestBody HealthProfileRequest request,
            @RequestHeader("Authorization") String token
            ) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        int accountId = accountService.getAccountByEmail(email).getId();
        request.setAccountId(accountId);

        UserHealthProfile profile = healthProfileService.update(request);
        HealthProfileResponse response = healthProfileService.mapToResponse(profile);
        return ResponseEntity.ok(ApiResponse.success("Health profile updated successfully", response));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get profile by Account ID", description = "Retrieve health profile details for a specific account")
    public ResponseEntity<ApiResponse<HealthProfileResponse>> getByAccountId(
            @PathVariable int accountId
    ) {
        UserHealthProfile profile = healthProfileService.getByAccountId(accountId);
        HealthProfileResponse response = healthProfileService.mapToResponse(profile);
        return ResponseEntity.ok(ApiResponse.success("Health profile retrieved successfully", response));
    }

    @GetMapping("/account")
    @Operation(summary = "Get personal profile", description = "Retrieve health profile details for a specific account")
    public ResponseEntity<ApiResponse<HealthProfileResponse>> getByAccountId(
            @RequestHeader("Authorization") String token
    ) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        int accountId = accountService.getAccountByEmail(email).getId();
        UserHealthProfile profile = healthProfileService.getByAccountId(accountId);
        HealthProfileResponse response = healthProfileService.mapToResponse(profile);
        return ResponseEntity.ok(ApiResponse.success("Health profile retrieved successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get profile by ID", description = "Retrieve health profile details by its primary ID")
    public ResponseEntity<ApiResponse<HealthProfileResponse>> getById(
            @PathVariable int id
    ) {
        UserHealthProfile profile = healthProfileService.getById(id);
        HealthProfileResponse response = healthProfileService.mapToResponse(profile);
        return ResponseEntity.ok(ApiResponse.success("Health profile retrieved successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete health profile", description = "Remove a health profile by its ID")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable int id
    ) {
        healthProfileService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Health profile deleted successfully", null));
    }
}
