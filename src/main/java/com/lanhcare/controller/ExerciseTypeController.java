package com.lanhcare.controller;

import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.exercisetype.ExerciseTypeRequest;
import com.lanhcare.dto.exercisetype.ExerciseTypeResponse;
import com.lanhcare.service.ExerciseTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercise-types")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin - Exercise Type", description = "APIs for managing exercise categories and MET values")
public class ExerciseTypeController {
    private final ExerciseTypeService exerciseTypeService;

    @PostMapping
    @Operation(summary = "Create exercise type", description = "Add a new exercise category with its MET value (Admin only)")
    public ResponseEntity<ApiResponse<ExerciseTypeResponse>> create(
            @RequestBody ExerciseTypeRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Exercise type created", exerciseTypeService.mapToResponse(
                exerciseTypeService.create(request))));
    }

    @GetMapping
    @Operation(summary = "Get all exercise types with pagination")
    public ResponseEntity<ApiResponse<Page<ExerciseTypeResponse>>> getAll(
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success("Success", exerciseTypeService.getAll(pageable)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search exercise by activity name with pagination")
    public ResponseEntity<ApiResponse<Page<ExerciseTypeResponse>>> search(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success("Search results", exerciseTypeService.searchByActivity(keyword, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exercise type by Id", description = "Retrieve exercise by Id")
    public ResponseEntity<ApiResponse<ExerciseTypeResponse>> getById(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(ApiResponse.success("Success", exerciseTypeService.mapToResponse(
                exerciseTypeService.getById(id))));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update exercise type", description = "Modify an existing exercise type's data")
    public ResponseEntity<ApiResponse<ExerciseTypeResponse>> update(
            @PathVariable Integer id,
            @RequestBody ExerciseTypeRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Exercise type updated", exerciseTypeService.mapToResponse(
                exerciseTypeService.update(id, request))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete exercise type", description = "Remove an exercise type from the system")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id
    ) {
        exerciseTypeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Exercise type deleted", null));
    }
}