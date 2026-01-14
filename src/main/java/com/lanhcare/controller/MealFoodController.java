package com.lanhcare.controller;

import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.meallog.MealFoodRequest;
import com.lanhcare.dto.meallog.MealFoodResponse;
import com.lanhcare.entity.MealFood;
import com.lanhcare.service.MealFoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meal-foods")
@RequiredArgsConstructor
@Tag(name = "User - Meal Food Detail", description = "APIs for managing individual food items within a meal")
public class MealFoodController {
    private final MealFoodService mealFoodService;

    @PostMapping
    @Operation(summary = "Add food to meal", description = "Add a single food item to an existing meal log")
    public ResponseEntity<ApiResponse<MealFoodResponse>> addFood(
            @RequestBody MealFoodRequest request
    ) {
        MealFood mealFood = mealFoodService.create(request);
        return ResponseEntity.ok(ApiResponse.success("Đã thêm món ăn vào bữa", mealFoodService.mapToResponse(mealFood)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update food quantity", description = "Update quantity or change food item for a specific record")
    public ResponseEntity<ApiResponse<MealFoodResponse>> updateFood(
            @PathVariable int id,
            @RequestBody MealFoodRequest request) {
        MealFood updated = mealFoodService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Đã cập nhật món ăn", mealFoodService.mapToResponse(updated)));
    }

    @GetMapping("/meal/{mealId}")
    @Operation(summary = "Get foods by meal", description = "Retrieve all food items belonging to a specific meal log")
    public ResponseEntity<ApiResponse<List<MealFoodResponse>>> getByMeal(@PathVariable int mealId) {
        List<MealFoodResponse> response = mealFoodService.getByMeal(mealId);
        return ResponseEntity.ok(ApiResponse.success("Danh sách món ăn trong bữa", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get meal food by Id", description = "Retrieve meal food item by Id")
    public ResponseEntity<ApiResponse<MealFoodResponse>> getById(
            @PathVariable Integer id
    ) {
        MealFoodResponse response = mealFoodService.mapToResponse(
                mealFoodService.findById(id));
        return ResponseEntity.ok(ApiResponse.success("Món ăn trong bữa", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove food from meal", description = "Delete a specific food record from a meal log")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable int id
    ) {
        mealFoodService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa món ăn khỏi bữa", null));
    }
}