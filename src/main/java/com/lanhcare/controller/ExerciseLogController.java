package com.lanhcare.controller;

import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.exerciselog.ExerciseLogRequest;
import com.lanhcare.dto.exerciselog.ExerciseLogResponse;
import com.lanhcare.service.ExerciseLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercise-logs")
@RequiredArgsConstructor
@Tag(name = "User - Exercise Log", description = "APIs for logging physical activities")
public class ExerciseLogController {
    private final ExerciseLogService exerciseLogService;

    @PostMapping
    @Operation(summary = "Add exercise activity", description = "Log a new physical activity and recalculate daily calories out")
    public ResponseEntity<ApiResponse<ExerciseLogResponse>> add(
            @RequestBody ExerciseLogRequest request
    ) {
        ExerciseLogResponse response = exerciseLogService.mapToResponse(
                exerciseLogService.addExercise(request));
        return ResponseEntity.ok(ApiResponse.success("Activity logged successfully", response));
    }

    @GetMapping("/daily-log/{dailyLogId}")
    @Operation(summary = "Get activities by daily log", description = "Retrieve all exercises for a specific day")
    public ResponseEntity<ApiResponse<List<ExerciseLogResponse>>> getByDailyLog(
            @PathVariable Integer dailyLogId
    ) {
        List<ExerciseLogResponse> response = exerciseLogService.getByDailyLogId(dailyLogId);
        return ResponseEntity.ok(ApiResponse.success("Activities retrieved successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete exercise log", description = "Remove an activity and update daily totals")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Integer id
    ) {
        exerciseLogService.deleteExercise(id);
        return ResponseEntity.ok(ApiResponse.success("Activity deleted successfully", null));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update exercise log", description = "Update duration or exercise type, then recalculate all related calories")
    public ResponseEntity<ApiResponse<ExerciseLogResponse>> update(
            @PathVariable Integer id,
            @RequestBody ExerciseLogRequest request
    ) {
        ExerciseLogResponse response = exerciseLogService.mapToResponse(
                exerciseLogService.updateExercise(id, request));
        return ResponseEntity.ok(ApiResponse.success("Activity updated successfully", response));
    }
}