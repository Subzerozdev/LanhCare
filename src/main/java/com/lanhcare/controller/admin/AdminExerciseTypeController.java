package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.exercisetype.AdminExerciseTypeRequest;
import com.lanhcare.dto.admin.exercisetype.AdminExerciseTypeResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.service.admin.AdminExerciseTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Exercise Type Management Controller
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin/exercise-types")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Exercise Type Management", description = "Admin APIs for managing exercise types and MET values")
public class AdminExerciseTypeController {
    
    private final AdminExerciseTypeService exerciseTypeService;
    
    public AdminExerciseTypeController(AdminExerciseTypeService exerciseTypeService) {
        this.exerciseTypeService = exerciseTypeService;
    }
    
    /**
     * Get all exercise types with pagination and filters
     */
    @GetMapping
    @Operation(summary = "Get all exercise types", description = "Get paginated list of exercise types with optional filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminExerciseTypeResponse>>> getAllExerciseTypes(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean includeDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminExerciseTypeResponse> exerciseTypes = exerciseTypeService.getAllExerciseTypes(
                search, includeDeleted, page, size);
        
        return ResponseEntity.ok(ApiResponse.success("Exercise types retrieved successfully", exerciseTypes));
    }
    
    /**
     * Get exercise type detail by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get exercise type detail", description = "Get detailed information about an exercise type")
    public ResponseEntity<ApiResponse<AdminExerciseTypeResponse>> getExerciseTypeDetail(@PathVariable Integer id) {
        AdminExerciseTypeResponse exerciseType = exerciseTypeService.getExerciseTypeDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Exercise type retrieved successfully", exerciseType));
    }
    
    /**
     * Create new exercise type
     */
    @PostMapping
    @Operation(summary = "Create exercise type", description = "Create a new exercise type with MET value")
    public ResponseEntity<ApiResponse<AdminExerciseTypeResponse>> createExerciseType(
            @Valid @RequestBody AdminExerciseTypeRequest request) {
        
        AdminExerciseTypeResponse exerciseType = exerciseTypeService.createExerciseType(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Exercise type created successfully", exerciseType));
    }
    
    /**
     * Update exercise type
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update exercise type", description = "Update exercise type information")
    public ResponseEntity<ApiResponse<AdminExerciseTypeResponse>> updateExerciseType(
            @PathVariable Integer id,
            @Valid @RequestBody AdminExerciseTypeRequest request) {
        
        AdminExerciseTypeResponse exerciseType = exerciseTypeService.updateExerciseType(id, request);
        return ResponseEntity.ok(ApiResponse.success("Exercise type updated successfully", exerciseType));
    }
    
    /**
     * Delete exercise type (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete exercise type", description = "Soft delete an exercise type (sets deleted to true)")
    public ResponseEntity<ApiResponse<Void>> deleteExerciseType(@PathVariable Integer id) {
        exerciseTypeService.deleteExerciseType(id);
        return ResponseEntity.ok(ApiResponse.success("Exercise type deleted successfully", null));
    }
    
    /**
     * Restore deleted exercise type
     */
    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restore exercise type", description = "Restore a soft-deleted exercise type")
    public ResponseEntity<ApiResponse<AdminExerciseTypeResponse>> restoreExerciseType(@PathVariable Integer id) {
        AdminExerciseTypeResponse exerciseType = exerciseTypeService.restoreExerciseType(id);
        return ResponseEntity.ok(ApiResponse.success("Exercise type restored successfully", exerciseType));
    }
}
