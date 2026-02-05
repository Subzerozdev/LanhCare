package com.lanhcare.controller;

import com.lanhcare.dto.admin.nutrition.AdminFoodItemResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.enums.FoodItemStatus;
import com.lanhcare.service.admin.AdminNutritionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/foods")
@Tag(name = "User - Food Management", description = "User APIs for searching food items, types, and nutrients")
public class FoodController {
    private final AdminNutritionService nutritionService;

    public FoodController(AdminNutritionService nutritionService) {
        this.nutritionService = nutritionService;
    }

    @GetMapping("/items")
    @Operation(summary = "Get all food items", description = "Get paginated list of food items with filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminFoodItemResponse>>> getAllFoodItems(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) FoodItemStatus status,
            @RequestParam(required = false) Integer foodTypeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PageResponse<AdminFoodItemResponse> foods = nutritionService.getAllFoodItems(
                search, status, foodTypeId, page, size);

        return ResponseEntity.ok(ApiResponse.success("Food items retrieved successfully", foods));
    }
}
