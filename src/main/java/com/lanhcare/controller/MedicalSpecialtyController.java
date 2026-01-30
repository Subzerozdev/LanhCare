package com.lanhcare.controller;

import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.medical.MedicalSpecialtyResponse;
import com.lanhcare.service.MedicalSpecialtyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medical-specialties")
@RequiredArgsConstructor
@Tag(name = "Medical Specialty", description = "APIs for managing medical specialties")
public class MedicalSpecialtyController {

    private final MedicalSpecialtyService specialtyService;

    @GetMapping("/{id}")
    @Operation(summary = "Get specialty by ID")
    public ResponseEntity<ApiResponse<MedicalSpecialtyResponse>> getById(@PathVariable int id) {
        MedicalSpecialtyResponse response = specialtyService.mapToResponse(
                specialtyService.getById(id));
        return ResponseEntity.ok(ApiResponse.success("Specialty retrieved successfully", response));
    }

    @GetMapping("/icd11s/{icdUri}")
    @Operation(summary = "Get specialties by ICD11 URI", description = "Retrieve a list of medical specialties associated with a specific ICD11 code")
    public ResponseEntity<ApiResponse<List<MedicalSpecialtyResponse>>> getByICD11(
            @PathVariable String icdUri
    ) {
        List<MedicalSpecialtyResponse> responses = specialtyService.getByICD11(icdUri)
                .stream().map(specialtyService::mapToResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Specialties for ICD11 retrieved successfully", responses));
    }

    @GetMapping("/hospitals/{id}")
    @Operation(summary = "Get specialties by hospital", description = "Retrieve a list of medical specialties associated with a specific hospital")
    public ResponseEntity<ApiResponse<List<MedicalSpecialtyResponse>>> getByHospital(
            @PathVariable Integer id
    ) {
        List<MedicalSpecialtyResponse> responses = specialtyService.getByHospital(id)
                .stream().map(specialtyService::mapToResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Specialties for hospital retrieved successfully", responses));
    }
}