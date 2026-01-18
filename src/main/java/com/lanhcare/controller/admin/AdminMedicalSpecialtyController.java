package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.medicalspecialty.AdminMedicalSpecialtyRequest;
import com.lanhcare.dto.admin.medicalspecialty.AdminMedicalSpecialtyResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.enums.SpecialtyStatus;
import com.lanhcare.service.admin.AdminMedicalSpecialtyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Medical Specialty Management Controller
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin/medical-specialties")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Medical Specialty Management", description = "Admin APIs for managing medical specialties independently")
public class AdminMedicalSpecialtyController {
    
    private final AdminMedicalSpecialtyService specialtyService;
    
    public AdminMedicalSpecialtyController(AdminMedicalSpecialtyService specialtyService) {
        this.specialtyService = specialtyService;
    }
    
    /**
     * Get all medical specialties with pagination and filters
     */
    @GetMapping
    @Operation(summary = "Get all medical specialties", description = "Get paginated list of medical specialties with optional filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminMedicalSpecialtyResponse>>> getAllSpecialties(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) SpecialtyStatus status,
            @RequestParam(required = false) Integer hospitalId,
            @RequestParam(required = false) String icdUri,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminMedicalSpecialtyResponse> specialties = specialtyService.getAllSpecialties(
                search, status, hospitalId, icdUri, page, size);
        
        return ResponseEntity.ok(ApiResponse.success("Medical specialties retrieved successfully", specialties));
    }
    
    /**
     * Get specialty detail by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get specialty detail", description = "Get detailed information about a medical specialty")
    public ResponseEntity<ApiResponse<AdminMedicalSpecialtyResponse>> getSpecialtyDetail(@PathVariable Integer id) {
        AdminMedicalSpecialtyResponse specialty = specialtyService.getSpecialtyDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Medical specialty retrieved successfully", specialty));
    }
    
    /**
     * Create new medical specialty
     */
    @PostMapping
    @Operation(summary = "Create medical specialty", description = "Create a new medical specialty")
    public ResponseEntity<ApiResponse<AdminMedicalSpecialtyResponse>> createSpecialty(
            @Valid @RequestBody AdminMedicalSpecialtyRequest request) {
        
        AdminMedicalSpecialtyResponse specialty = specialtyService.createSpecialty(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Medical specialty created successfully", specialty));
    }
    
    /**
     * Update medical specialty
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update medical specialty", description = "Update medical specialty information")
    public ResponseEntity<ApiResponse<AdminMedicalSpecialtyResponse>> updateSpecialty(
            @PathVariable Integer id,
            @Valid @RequestBody AdminMedicalSpecialtyRequest request) {
        
        AdminMedicalSpecialtyResponse specialty = specialtyService.updateSpecialty(id, request);
        return ResponseEntity.ok(ApiResponse.success("Medical specialty updated successfully", specialty));
    }
    
    /**
     * Update specialty status
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update specialty status", description = "Change medical specialty status")
    public ResponseEntity<ApiResponse<AdminMedicalSpecialtyResponse>> updateSpecialtyStatus(
            @PathVariable Integer id,
            @RequestParam SpecialtyStatus status) {
        
        AdminMedicalSpecialtyResponse specialty = specialtyService.updateSpecialtyStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Medical specialty status updated successfully", specialty));
    }
    
    /**
     * Delete medical specialty (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete medical specialty", description = "Soft delete a medical specialty (sets status to INACTIVE)")
    public ResponseEntity<ApiResponse<Void>> deleteSpecialty(@PathVariable Integer id) {
        specialtyService.deleteSpecialty(id);
        return ResponseEntity.ok(ApiResponse.success("Medical specialty deleted successfully", null));
    }
}
