package com.lanhcare.controller;

import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.meallog.MealLogRequest;
import com.lanhcare.dto.meallog.MealLogResponse;
import com.lanhcare.service.MealLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meal-logs")
@RequiredArgsConstructor
@Tag(name = "User - Meal Log", description = "APIs for tracking daily meals and food intake")
public class MealLogController {

    private final MealLogService mealLogService;

    @PostMapping
    @Operation(summary = "Create meal log", description = "Record a new meal (Breakfast, Lunch, etc.)")
    public ResponseEntity<ApiResponse<MealLogResponse>> create(
            @RequestBody MealLogRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Meal logged successfully",
                        mealLogService.mapToResponse(mealLogService.create(request))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get meal log by ID")
    public ResponseEntity<ApiResponse<MealLogResponse>> getById(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Meal log retrieved",
                        mealLogService.mapToResponse(mealLogService.getById(id))));
    }

    @GetMapping("/daily-log/{dailyLogId}")
    @Operation(summary = "Get all meals for a specific day")
    public ResponseEntity<ApiResponse<List<MealLogResponse>>> getByDailyLog(
            @PathVariable Integer dailyLogId
    ) {
        return ResponseEntity.ok(ApiResponse.success("Meals retrieved", mealLogService.getByDailyLogId(dailyLogId)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update meal log info")
    public ResponseEntity<ApiResponse<MealLogResponse>> update(
            @PathVariable Integer id,
            @RequestBody MealLogRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Meal log updated",
                        mealLogService.mapToResponse(mealLogService.update(id, request))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete meal log")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id
    ) {
        mealLogService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Meal log deleted", null));
    }
}