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
    
    /**
     * Get food item detail by ID (includes nutrients)
     */
    @GetMapping("/food-items/{id}")
    @Operation(summary = "Get food item detail", description = "Get detailed information about a food item including nutrients")
    public ResponseEntity<ApiResponse<AdminFoodItemDetailResponse>> getFoodItemById(@PathVariable Integer id) {
        AdminFoodItemDetailResponse food = nutritionService.getFoodItemById(id);
        return ResponseEntity.ok(ApiResponse.success("Food item retrieved successfully", food));
    }
    
    // ========== FOOD ITEM NUTRIENTS ==========
    
    /**
     * Get all nutrients for a food item
     */
    @GetMapping("/food-items/{foodItemId}/nutrients")
    @Operation(summary = "Get food item nutrients", description = "Get all nutrients for a specific food item")
    public ResponseEntity<ApiResponse<List<AdminFoodNutrientResponse>>> getFoodNutrients(
            @PathVariable Integer foodItemId) {
        List<AdminFoodNutrientResponse> nutrients = nutritionService.getFoodNutrients(foodItemId);
        return ResponseEntity.ok(ApiResponse.success("Food nutrients retrieved successfully", nutrients));
    }
    
    /**
     * Add nutrient to food item
     */
    @PostMapping("/food-items/{foodItemId}/nutrients")
    @Operation(summary = "Add nutrient to food item", description = "Add a nutrient value to a food item")
    public ResponseEntity<ApiResponse<AdminFoodNutrientResponse>> addFoodNutrient(
            @PathVariable Integer foodItemId,
            @Valid @RequestBody AdminFoodNutrientRequest request) {
        
        AdminFoodNutrientResponse nutrient = nutritionService.addFoodNutrient(foodItemId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Food nutrient added successfully", nutrient));
    }
    
    /**
     * Update nutrient value for food item
     */
    @PutMapping("/food-items/{foodItemId}/nutrients/{nutrientId}")
    @Operation(summary = "Update food nutrient", description = "Update nutrient value for a food item")
    public ResponseEntity<ApiResponse<AdminFoodNutrientResponse>> updateFoodNutrient(
            @PathVariable Integer foodItemId,
            @PathVariable Integer nutrientId,
            @Valid @RequestBody AdminFoodNutrientRequest request) {
        
        AdminFoodNutrientResponse nutrient = nutritionService.updateFoodNutrient(foodItemId, nutrientId, request);
        return ResponseEntity.ok(ApiResponse.success("Food nutrient updated successfully", nutrient));
    }
    
    /**
     * Remove nutrient from food item
     */
    @DeleteMapping("/food-items/{foodItemId}/nutrients/{nutrientId}")
    @Operation(summary = "Remove food nutrient", description = "Remove a nutrient from a food item")
    public ResponseEntity<ApiResponse<Void>> removeFoodNutrient(
            @PathVariable Integer foodItemId,
            @PathVariable Integer nutrientId) {
        
        nutritionService.removeFoodNutrient(foodItemId, nutrientId);
        return ResponseEntity.ok(ApiResponse.success("Food nutrient removed successfully", null));
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
    
    @PutMapping("/food-types/{id}")
    @Operation(summary = "Update food type", description = "Update food type name")
    public ResponseEntity<ApiResponse<FoodType>> updateFoodType(
            @PathVariable Integer id,
            @Valid @RequestBody AdminFoodTypeRequest request) {
        
        FoodType foodType = nutritionService.updateFoodType(id, request);
        return ResponseEntity.ok(ApiResponse.success("Food type updated successfully", foodType));
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
    
    @PutMapping("/nutrients/{id}")
    @Operation(summary = "Update nutrient", description = "Update nutrient name and unit")
    public ResponseEntity<ApiResponse<Nutrient>> updateNutrient(
            @PathVariable Integer id,
            @Valid @RequestBody AdminNutrientRequest request) {
        
        Nutrient nutrient = nutritionService.updateNutrient(id, request);
        return ResponseEntity.ok(ApiResponse.success("Nutrient updated successfully", nutrient));
    }
    
    @DeleteMapping("/nutrients/{id}")
    @Operation(summary = "Delete nutrient", description = "Delete a nutrient")
    public ResponseEntity<ApiResponse<Void>> deleteNutrient(@PathVariable Integer id) {
        nutritionService.deleteNutrient(id);
        return ResponseEntity.ok(ApiResponse.success("Nutrient deleted successfully", null));
    }
}
