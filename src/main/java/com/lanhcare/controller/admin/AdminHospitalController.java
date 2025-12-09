package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.hospital.AdminHospitalRequest;
import com.lanhcare.dto.admin.hospital.AdminHospitalResponse;
import com.lanhcare.dto.admin.hospital.AdminSpecialtyRequest;
import com.lanhcare.dto.admin.hospital.AdminSpecialtyResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.HospitalStatus;
import com.lanhcare.entity.SpecialtyStatus;

import java.util.List;
import com.lanhcare.service.admin.AdminHospitalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Hospital Management Controller
 */
@RestController
@RequestMapping("/api/admin/hospitals")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Hospital Management", description = "Admin APIs for managing hospitals and specialties")
public class AdminHospitalController {
    
    private final AdminHospitalService hospitalService;
    
    public AdminHospitalController(AdminHospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }
    
    /**
     * Get all hospitals
     */
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
    
    /**
     * Get hospital by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get hospital detail", description = "Get detailed information about a hospital")
    public ResponseEntity<ApiResponse<AdminHospitalResponse>> getHospitalById(@PathVariable Integer id) {
        AdminHospitalResponse hospital = hospitalService.getHospitalById(id);
        return ResponseEntity.ok(ApiResponse.success("Hospital retrieved successfully", hospital));
    }
    
    /**
     * Create hospital
     */
    @PostMapping
    @Operation(summary = "Create hospital", description = "Create a new hospital")
    public ResponseEntity<ApiResponse<AdminHospitalResponse>> createHospital(
            @Valid @RequestBody AdminHospitalRequest request) {
        
        AdminHospitalResponse hospital = hospitalService.createHospital(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Hospital created successfully", hospital));
    }
    
    /**
     * Update hospital
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update hospital", description = "Update hospital information")
    public ResponseEntity<ApiResponse<AdminHospitalResponse>> updateHospital(
            @PathVariable Integer id,
            @Valid @RequestBody AdminHospitalRequest request) {
        
        AdminHospitalResponse hospital = hospitalService.updateHospital(id, request);
        return ResponseEntity.ok(ApiResponse.success("Hospital updated successfully", hospital));
    }
    
    /**
     * Update hospital status
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update hospital status", description = "Change hospital status")
    public ResponseEntity<ApiResponse<AdminHospitalResponse>> updateHospitalStatus(
            @PathVariable Integer id,
            @RequestParam HospitalStatus status) {
        
        AdminHospitalResponse hospital = hospitalService.updateHospitalStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Hospital status updated successfully", hospital));
    }
    
    /**
     * Delete hospital
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete hospital", description = "Soft delete a hospital")
    public ResponseEntity<ApiResponse<Void>> deleteHospital(@PathVariable Integer id) {
        hospitalService.deleteHospital(id);
        return ResponseEntity.ok(ApiResponse.success("Hospital deleted successfully", null));
    }
    
    // ========== SPECIALTY MANAGEMENT ==========
    
    /**
     * Get all specialties for a hospital
     */
    @GetMapping("/{hospitalId}/specialties")
    @Operation(summary = "Get hospital specialties", description = "Get all specialties for a specific hospital")
    public ResponseEntity<ApiResponse<List<AdminSpecialtyResponse>>> getHospitalSpecialties(
            @PathVariable Integer hospitalId) {
        
        List<AdminSpecialtyResponse> specialties = hospitalService.getHospitalSpecialties(hospitalId);
        return ResponseEntity.ok(ApiResponse.success("Specialties retrieved successfully", specialties));
    }
    
    /**
     * Get specialty by ID
     */
    @GetMapping("/{hospitalId}/specialties/{specialtyId}")
    @Operation(summary = "Get specialty detail", description = "Get detailed information about a specialty")
    public ResponseEntity<ApiResponse<AdminSpecialtyResponse>> getSpecialtyById(
            @PathVariable Integer hospitalId,
            @PathVariable Integer specialtyId) {
        
        AdminSpecialtyResponse specialty = hospitalService.getSpecialtyById(hospitalId, specialtyId);
        return ResponseEntity.ok(ApiResponse.success("Specialty retrieved successfully", specialty));
    }
    
    /**
     * Add specialty to hospital
     */
    @PostMapping("/{hospitalId}/specialties")
    @Operation(summary = "Add specialty", description = "Add a medical specialty to a hospital")
    public ResponseEntity<ApiResponse<AdminSpecialtyResponse>> addSpecialty(
            @PathVariable Integer hospitalId,
            @Valid @RequestBody AdminSpecialtyRequest request) {
        
        AdminSpecialtyResponse specialty = hospitalService.addSpecialty(hospitalId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Specialty added successfully", specialty));
    }
    
    /**
     * Update specialty
     */
    @PutMapping("/{hospitalId}/specialties/{specialtyId}")
    @Operation(summary = "Update specialty", description = "Update specialty information")
    public ResponseEntity<ApiResponse<AdminSpecialtyResponse>> updateSpecialty(
            @PathVariable Integer hospitalId,
            @PathVariable Integer specialtyId,
            @Valid @RequestBody AdminSpecialtyRequest request) {
        
        AdminSpecialtyResponse specialty = hospitalService.updateSpecialty(hospitalId, specialtyId, request);
        return ResponseEntity.ok(ApiResponse.success("Specialty updated successfully", specialty));
    }
    
    /**
     * Update specialty status
     */
    @PatchMapping("/{hospitalId}/specialties/{specialtyId}/status")
    @Operation(summary = "Update specialty status", description = "Change specialty status")
    public ResponseEntity<ApiResponse<AdminSpecialtyResponse>> updateSpecialtyStatus(
            @PathVariable Integer hospitalId,
            @PathVariable Integer specialtyId,
            @RequestParam SpecialtyStatus status) {
        
        AdminSpecialtyResponse specialty = hospitalService.updateSpecialtyStatus(hospitalId, specialtyId, status);
        return ResponseEntity.ok(ApiResponse.success("Specialty status updated successfully", specialty));
    }
    
    /**
     * Delete specialty
     */
    @DeleteMapping("/{hospitalId}/specialties/{specialtyId}")
    @Operation(summary = "Delete specialty", description = "Remove a specialty from a hospital")
    public ResponseEntity<ApiResponse<Void>> deleteSpecialty(
            @PathVariable Integer hospitalId,
            @PathVariable Integer specialtyId) {
        
        hospitalService.deleteSpecialty(hospitalId, specialtyId);
        return ResponseEntity.ok(ApiResponse.success("Specialty deleted successfully", null));
    }
}
