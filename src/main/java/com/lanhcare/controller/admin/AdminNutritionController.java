package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.nutrition.*;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.FoodItemStatus;
import com.lanhcare.entity.FoodType;
import com.lanhcare.entity.Nutrient;
import com.lanhcare.service.admin.AdminNutritionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin Nutrition Management Controller
 */
@RestController
@RequestMapping("/api/admin/nutrition")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Nutrition Management", description = "Admin APIs for managing food items, types, and nutrients")
public class AdminNutritionController {
    
    private final AdminNutritionService nutritionService;
    
    public AdminNutritionController(AdminNutritionService nutritionService) {
        this.nutritionService = nutritionService;
    }
    
    // ========== FOOD ITEMS ==========
    
    @GetMapping("/food-items")
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
    
    @PostMapping("/food-items")
    @Operation(summary = "Create food item", description = "Create a new food item")
    public ResponseEntity<ApiResponse<AdminFoodItemResponse>> createFoodItem(
            @Valid @RequestBody AdminFoodItemRequest request) {
        
        AdminFoodItemResponse food = nutritionService.createFoodItem(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Food item created successfully", food));
    }
    
    @PutMapping("/food-items/{id}")
    @Operation(summary = "Update food item", description = "Update food item information")
    public ResponseEntity<ApiResponse<AdminFoodItemResponse>> updateFoodItem(
            @PathVariable Integer id,
            @Valid @RequestBody AdminFoodItemRequest request) {
        
        AdminFoodItemResponse food = nutritionService.updateFoodItem(id, request);
        return ResponseEntity.ok(ApiResponse.success("Food item updated successfully", food));
    }
    
    @DeleteMapping("/food-items/{id}")
    @Operation(summary = "Delete food item", description = "Soft delete a food item")
    public ResponseEntity<ApiResponse<Void>> deleteFoodItem(@PathVariable Integer id) {
        nutritionService.deleteFoodItem(id);
        return ResponseEntity.ok(ApiResponse.success("Food item deleted successfully", null));
    }
    
    // ========== FOOD TYPES ==========
    
    @GetMapping("/food-types")
    @Operation(summary = "Get all food types", description = "Get list of all food types")
    public ResponseEntity<ApiResponse<List<FoodType>>> getAllFoodTypes() {
        List<FoodType> foodTypes = nutritionService.getAllFoodTypes();
        return ResponseEntity.ok(ApiResponse.success("Food types retrieved successfully", foodTypes));
    }
    
    @PostMapping("/food-types")
    @Operation(summary = "Create food type", description = "Create a new food type")
    public ResponseEntity<ApiResponse<FoodType>> createFoodType(
            @Valid @RequestBody AdminFoodTypeRequest request) {
        
        FoodType foodType = nutritionService.createFoodType(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Food type created successfully", foodType));
    }
    
    @DeleteMapping("/food-types/{id}")
    @Operation(summary = "Delete food type", description = "Soft delete a food type")
    public ResponseEntity<ApiResponse<Void>> deleteFoodType(@PathVariable Integer id) {
        nutritionService.deleteFoodType(id);
        return ResponseEntity.ok(ApiResponse.success("Food type deleted successfully", null));
    }
    
    // ========== NUTRIENTS ==========
    
    @GetMapping("/nutrients")
    @Operation(summary = "Get all nutrients", description = "Get list of all nutrients")
    public ResponseEntity<ApiResponse<List<Nutrient>>> getAllNutrients() {
        List<Nutrient> nutrients = nutritionService.getAllNutrients();
        return ResponseEntity.ok(ApiResponse.success("Nutrients retrieved successfully", nutrients));
    }
    
    @PostMapping("/nutrients")
    @Operation(summary = "Create nutrient", description = "Create a new nutrient")
    public ResponseEntity<ApiResponse<Nutrient>> createNutrient(
            @Valid @RequestBody AdminNutrientRequest request) {
        
        Nutrient nutrient = nutritionService.createNutrient(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Nutrient created successfully", nutrient));
    }
    
    @DeleteMapping("/nutrients/{id}")
    @Operation(summary = "Delete nutrient", description = "Delete a nutrient")
    public ResponseEntity<ApiResponse<Void>> deleteNutrient(@PathVariable Integer id) {
        nutritionService.deleteNutrient(id);
        return ResponseEntity.ok(ApiResponse.success("Nutrient deleted successfully", null));
    }
}
