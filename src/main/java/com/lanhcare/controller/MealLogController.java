package com.lanhcare.controller;

import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.meallog.MealLogRequest;
import com.lanhcare.dto.meallog.MealLogResponse;
import com.lanhcare.entity.MealLog;
import com.lanhcare.security.JwtTokenProvider;
import com.lanhcare.service.AccountService;
import com.lanhcare.service.MealLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/meal-logs")
@RequiredArgsConstructor
@Tag(name = "User - Meal Log", description = "APIs for managing daily meal logs")
public class MealLogController {

    private final MealLogService mealLogService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create meal log", description = "Log a new meal with its associated food items")
    public ResponseEntity<ApiResponse<MealLogResponse>> create(
            @RequestBody MealLogRequest request,
            @RequestHeader("Authorization") String token
    ) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        int accountId = accountService.getAccountByEmail(email).getId();
        request.setAccountId(accountId);

        MealLog mealLog = mealLogService.create(request);
        MealLogResponse response = mealLogService.mapToResponse(mealLog);
        return ResponseEntity.ok(ApiResponse.success("Bữa ăn đã được ghi nhận thành công", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update meal log", description = "Update a meal with its associated food items")
    public ResponseEntity<ApiResponse<MealLogResponse>> update(
            @RequestBody MealLogRequest request,
            @PathVariable Integer id
    ) {
        request.setMealLogId(id);
        MealLog mealLog = mealLogService.update(request);
        MealLogResponse response = mealLogService.mapToResponse(mealLog);
        return ResponseEntity.ok(ApiResponse.success("Bữa ăn đã được cập nhập thành công", response));
    }

    @GetMapping("/account")
    @Operation(summary = "Get meal logs by Account", description = "Retrieve paginated meal logs for a specific account")
    public ResponseEntity<ApiResponse<Page<MealLogResponse>>> getByAccount(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Map<String, String> params,
            @RequestHeader("Authorization") String token
    ) {
        String email = jwtTokenProvider.getEmailFromToken(token);
        int accountId = accountService.getAccountByEmail(email).getId();

        Pageable pageable = PageRequest.of(page, size);
        Page<MealLogResponse> response = mealLogService.getByAccountId(accountId, pageable, params);
        return ResponseEntity.ok(ApiResponse.success("Danh sách bữa ăn được tải thành công", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get meal log by ID", description = "Retrieve details of a specific meal log")
    public ResponseEntity<ApiResponse<MealLogResponse>> getById(@PathVariable int id) {
        MealLog mealLog = mealLogService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Thông tin bữa ăn", mealLogService.mapToResponse(mealLog)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete meal log", description = "Remove a meal log and all its associated food records")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable int id) {
        mealLogService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa nhật ký bữa ăn", null));
    }
}