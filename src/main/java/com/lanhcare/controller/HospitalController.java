package com.lanhcare.controller;

import com.lanhcare.dto.admin.hospital.AdminHospitalResponse;
import com.lanhcare.dto.admin.hospital.AdminSpecialtyResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.enums.HospitalStatus;
import com.lanhcare.service.HospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/hospitals")
@Tag(name = "User - Hospitals Searching", description = "Hospital APIs for searching hospitals and specialties")
@RequiredArgsConstructor
public class HospitalController {
    private final HospitalService hospitalService;

    @GetMapping
    @Operation(summary = "Get all hospitals", description = "Get paginated list of hospitals with optional filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminHospitalResponse>>> getAllHospitals(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) HospitalStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<AdminHospitalResponse> hospitals = hospitalService.getAllHospitals(
                search, status, page, size);

        return ResponseEntity.ok(ApiResponse.success("Hospitals retrieved successfully", hospitals));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hospital detail", description = "Get detailed information about a hospital")
    public ResponseEntity<ApiResponse<AdminHospitalResponse>> getHospitalById(@PathVariable Integer id) {
        AdminHospitalResponse hospital = hospitalService.getHospitalById(id);
        return ResponseEntity.ok(ApiResponse.success("Hospital retrieved successfully", hospital));
    }

    @GetMapping("/{hospitalId}/specialties")
    @Operation(summary = "Get hospital specialties", description = "Get all specialties for a specific hospital")
    public ResponseEntity<ApiResponse<List<AdminSpecialtyResponse>>> getHospitalSpecialties(
            @PathVariable Integer hospitalId) {

        List<AdminSpecialtyResponse> specialties = hospitalService.getHospitalSpecialties(hospitalId);
        return ResponseEntity.ok(ApiResponse.success("Specialties retrieved successfully", specialties));
    }

    @GetMapping("/specialties/{specialityId}")
    @Operation(summary = "Get specialties hospitals", description = "Get all hospitals for a specific speciality")
    public ResponseEntity<ApiResponse<List<AdminHospitalResponse>>> getSpecialtyHospital(
            @PathVariable Integer specialityId) {

        List<AdminHospitalResponse> specialties = hospitalService.getHospitalBySpecialty(specialityId);
        return ResponseEntity.ok(ApiResponse.success("Hospitals retrieved successfully", specialties));
    }
}
