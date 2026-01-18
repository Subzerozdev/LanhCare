package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.dietaryrestriction.AdminDietaryRestrictionDetailResponse;
import com.lanhcare.dto.admin.dietaryrestriction.AdminDietaryRestrictionRequest;
import com.lanhcare.dto.admin.dietaryrestriction.AdminDietaryRestrictionResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.enums.RestrictionStatus;
import com.lanhcare.service.admin.AdminDietaryRestrictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Dietary Restriction Management Controller
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin/dietary-restrictions")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Dietary Restriction Management", description = "Admin APIs for managing dietary restrictions")
public class AdminDietaryRestrictionController {
    
    private final AdminDietaryRestrictionService restrictionService;
    
    public AdminDietaryRestrictionController(AdminDietaryRestrictionService restrictionService) {
        this.restrictionService = restrictionService;
    }
    
    /**
     * Get all dietary restrictions with pagination and filters
     */
    @GetMapping
    @Operation(summary = "Get all dietary restrictions", description = "Get paginated list of dietary restrictions with optional filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminDietaryRestrictionResponse>>> getAllRestrictions(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) RestrictionStatus status,
            @RequestParam(required = false) Integer userHealthProfileId,
            @RequestParam(required = false) Integer nutrientId,
            @RequestParam(required = false) String icdUri,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminDietaryRestrictionResponse> restrictions = restrictionService.getAllRestrictions(
                search, status, userHealthProfileId, nutrientId, icdUri, page, size);
        
        return ResponseEntity.ok(ApiResponse.success("Dietary restrictions retrieved successfully", restrictions));
    }
    
    /**
     * Get restriction detail by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get restriction detail", description = "Get detailed information about a dietary restriction")
    public ResponseEntity<ApiResponse<AdminDietaryRestrictionDetailResponse>> getRestrictionDetail(@PathVariable Integer id) {
        AdminDietaryRestrictionDetailResponse restriction = restrictionService.getRestrictionDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Dietary restriction retrieved successfully", restriction));
    }
    
    /**
     * Create new dietary restriction
     */
    @PostMapping
    @Operation(summary = "Create dietary restriction", description = "Create a new dietary restriction")
    public ResponseEntity<ApiResponse<AdminDietaryRestrictionResponse>> createRestriction(
            @Valid @RequestBody AdminDietaryRestrictionRequest request) {
        
        AdminDietaryRestrictionResponse restriction = restrictionService.createRestriction(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Dietary restriction created successfully", restriction));
    }
    
    /**
     * Update dietary restriction
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update dietary restriction", description = "Update dietary restriction information")
    public ResponseEntity<ApiResponse<AdminDietaryRestrictionResponse>> updateRestriction(
            @PathVariable Integer id,
            @Valid @RequestBody AdminDietaryRestrictionRequest request) {
        
        AdminDietaryRestrictionResponse restriction = restrictionService.updateRestriction(id, request);
        return ResponseEntity.ok(ApiResponse.success("Dietary restriction updated successfully", restriction));
    }
    
    /**
     * Update restriction status
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update restriction status", description = "Change dietary restriction status")
    public ResponseEntity<ApiResponse<AdminDietaryRestrictionResponse>> updateRestrictionStatus(
            @PathVariable Integer id,
            @RequestParam RestrictionStatus status) {
        
        AdminDietaryRestrictionResponse restriction = restrictionService.updateRestrictionStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Dietary restriction status updated successfully", restriction));
    }
    
    /**
     * Delete dietary restriction (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete dietary restriction", description = "Soft delete a dietary restriction (sets status to INACTIVE)")
    public ResponseEntity<ApiResponse<Void>> deleteRestriction(@PathVariable Integer id) {
        restrictionService.deleteRestriction(id);
        return ResponseEntity.ok(ApiResponse.success("Dietary restriction deleted successfully", null));
    }
}
