package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.nutrition.*;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.*;
import com.lanhcare.exception.ResourceAlreadyExistsException;
import com.lanhcare.exception.ResourceNotFoundException;
import com.lanhcare.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Nutrition Management Service
 */
@Service
@Transactional
public class AdminNutritionService {
    
    private final FoodItemRepository foodItemRepository;
    private final FoodTypeRepository foodTypeRepository;
    private final NutrientRepository nutrientRepository;
    private final FoodNutrientRepository foodNutrientRepository;
    private final MealLogRepository mealLogRepository;
    
    public AdminNutritionService(FoodItemRepository foodItemRepository,
                                  FoodTypeRepository foodTypeRepository,
                                  NutrientRepository nutrientRepository,
                                  FoodNutrientRepository foodNutrientRepository,
                                  MealLogRepository mealLogRepository) {
        this.foodItemRepository = foodItemRepository;
        this.foodTypeRepository = foodTypeRepository;
        this.nutrientRepository = nutrientRepository;
        this.foodNutrientRepository = foodNutrientRepository;
        this.mealLogRepository = mealLogRepository;
    }
    
    // ========== FOOD ITEM MANAGEMENT ==========
    
    @Transactional(readOnly = true)
    public PageResponse<AdminFoodItemResponse> getAllFoodItems(String search, FoodItemStatus status,
                                                                 Integer foodTypeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<FoodItem> foodPage;
        
        if (search != null && !search.isEmpty() && status != null) {
            foodPage = foodItemRepository.searchFoodItemsByStatus(search, status, pageable);
        } else if (search != null && !search.isEmpty()) {
            foodPage = foodItemRepository.searchFoodItems(search, pageable);
        } else if (foodTypeId != null && status != null) {
            foodPage = foodItemRepository.findByFoodTypeIdAndStatusOrderByIdDesc(foodTypeId, status, pageable);
        } else if (foodTypeId != null) {
            foodPage = foodItemRepository.findByFoodTypeIdOrderByIdDesc(foodTypeId, pageable);
        } else if (status != null) {
            foodPage = foodItemRepository.findByStatusOrderByIdDesc(status, pageable);
        } else {
            foodPage = foodItemRepository.findAllByOrderByIdDesc(pageable);
        }
        
        List<AdminFoodItemResponse> foods = foodPage.getContent().stream()
                .map(this::mapToFoodItemResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminFoodItemResponse>builder()
                .content(foods)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(foodPage.getNumber())
                        .pageSize(foodPage.getSize())
                        .totalElements(foodPage.getTotalElements())
                        .totalPages(foodPage.getTotalPages())
                        .first(foodPage.isFirst())
                        .last(foodPage.isLast())
                        .build())
                .build();
    }
    
    public AdminFoodItemResponse createFoodItem(AdminFoodItemRequest request) {
        FoodType foodType = foodTypeRepository.findById(request.getFoodTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Food type not found"));
        
        FoodItem foodItem = FoodItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .calo(request.getCalo())
                .servingUnit(request.getServingUnit())
                .standardServingSize(request.getStandardServingSize())
                .foodType(foodType)
                .status(request.getStatus())
                .dataSource(request.getDataSource())
                .imageUrl(request.getImageUrl())
                .build();
        
        FoodItem saved = foodItemRepository.save(foodItem);
        return mapToFoodItemResponse(saved);
    }
    
    public AdminFoodItemResponse updateFoodItem(Integer id, AdminFoodItemRequest request) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));
        
        FoodType foodType = foodTypeRepository.findById(request.getFoodTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Food type not found"));
        
        foodItem.setName(request.getName());
        foodItem.setDescription(request.getDescription());
        foodItem.setCalo(request.getCalo());
        foodItem.setServingUnit(request.getServingUnit());
        foodItem.setStandardServingSize(request.getStandardServingSize());
        foodItem.setFoodType(foodType);
        foodItem.setStatus(request.getStatus());
        foodItem.setDataSource(request.getDataSource());
        foodItem.setImageUrl(request.getImageUrl());
        
        FoodItem updated = foodItemRepository.save(foodItem);
        return mapToFoodItemResponse(updated);
    }
    
    public void deleteFoodItem(Integer id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found"));
        
        // Soft delete
        foodItem.setStatus(FoodItemStatus.ARCHIVED);
        foodItemRepository.save(foodItem);
    }
    
    /**
     * Get food item detail by ID (includes nutrients)
     */
    @Transactional(readOnly = true)
    public AdminFoodItemDetailResponse getFoodItemById(Integer id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with ID: " + id));
        return mapToFoodItemDetailResponse(foodItem);
    }
    
    // ========== FOOD NUTRIENT MANAGEMENT ==========
    
    /**
     * Get all nutrients for a food item
     */
    @Transactional(readOnly = true)
    public List<AdminFoodNutrientResponse> getFoodNutrients(Integer foodItemId) {
        // Verify food item exists
        if (!foodItemRepository.existsById(foodItemId)) {
            throw new ResourceNotFoundException("Food item not found with ID: " + foodItemId);
        }
        
        return foodNutrientRepository.findByFoodItemIdOrderByNutrientNameAsc(foodItemId).stream()
                .map(this::mapToFoodNutrientResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Add nutrient to food item
     */
    public AdminFoodNutrientResponse addFoodNutrient(Integer foodItemId, AdminFoodNutrientRequest request) {
        FoodItem foodItem = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with ID: " + foodItemId));
        
        Nutrient nutrient = nutrientRepository.findById(request.getNutrientId())
                .orElseThrow(() -> new ResourceNotFoundException("Nutrient not found with ID: " + request.getNutrientId()));
        
        // Check if already exists
        if (foodNutrientRepository.existsByFoodItemIdAndNutrientId(foodItemId, request.getNutrientId())) {
            throw new ResourceAlreadyExistsException("FoodNutrient", "nutrientId", request.getNutrientId().toString());
        }
        
        FoodNutrient foodNutrient = FoodNutrient.builder()
                .foodItem(foodItem)
                .nutrient(nutrient)
                .value(request.getValue())
                .build();
        
        FoodNutrient saved = foodNutrientRepository.save(foodNutrient);
        return mapToFoodNutrientResponse(saved);
    }
    
    /**
     * Update nutrient value for food item
     */
    public AdminFoodNutrientResponse updateFoodNutrient(Integer foodItemId, Integer nutrientId, 
                                                         AdminFoodNutrientRequest request) {
        FoodNutrient foodNutrient = foodNutrientRepository.findByFoodItemIdAndNutrientId(foodItemId, nutrientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Food nutrient not found for food item " + foodItemId + " and nutrient " + nutrientId));
        
        foodNutrient.setValue(request.getValue());
        
        // If changing to different nutrient
        if (!nutrientId.equals(request.getNutrientId())) {
            Nutrient newNutrient = nutrientRepository.findById(request.getNutrientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Nutrient not found with ID: " + request.getNutrientId()));
            
            // Check if new nutrient already exists for this food
            if (foodNutrientRepository.existsByFoodItemIdAndNutrientId(foodItemId, request.getNutrientId())) {
                throw new ResourceAlreadyExistsException("FoodNutrient", "nutrientId", request.getNutrientId().toString());
            }
            
            foodNutrient.setNutrient(newNutrient);
        }
        
        FoodNutrient updated = foodNutrientRepository.save(foodNutrient);
        return mapToFoodNutrientResponse(updated);
    }
    
    /**
     * Remove nutrient from food item
     */
    public void removeFoodNutrient(Integer foodItemId, Integer nutrientId) {
        FoodNutrient foodNutrient = foodNutrientRepository.findByFoodItemIdAndNutrientId(foodItemId, nutrientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Food nutrient not found for food item " + foodItemId + " and nutrient " + nutrientId));
        
        foodNutrientRepository.delete(foodNutrient);
    }
    
    // ========== FOOD TYPE MANAGEMENT ==========
    
    @Transactional(readOnly = true)
    public List<FoodType> getAllFoodTypes() {
        return foodTypeRepository.findAll().stream()
                .filter(ft -> !ft.getIsDeleted())
                .collect(Collectors.toList());
    }
    
    public FoodType createFoodType(AdminFoodTypeRequest request) {
        FoodType foodType = FoodType.builder()
                .name(request.getName())
                .isDeleted(false)
                .build();
        return foodTypeRepository.save(foodType);
    }
    
    /**
     * Update food type
     */
    public FoodType updateFoodType(Integer id, AdminFoodTypeRequest request) {
        FoodType foodType = foodTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food type not found with ID: " + id));
        
        foodType.setName(request.getName());
        return foodTypeRepository.save(foodType);
    }
    
    public void deleteFoodType(Integer id) {
        FoodType foodType = foodTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food type not found"));
        
        foodType.setIsDeleted(true);
        foodTypeRepository.save(foodType);
    }
    
    // ========== NUTRIENT MANAGEMENT ==========
    
    @Transactional(readOnly = true)
    public List<Nutrient> getAllNutrients() {
        return nutrientRepository.findAll();
    }
    
    public Nutrient createNutrient(AdminNutrientRequest request) {
        if (nutrientRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Nutrient already exists");
        }
        
        Nutrient nutrient = Nutrient.builder()
                .name(request.getName())
                .unit(request.getUnit())
                .build();
        return nutrientRepository.save(nutrient);
    }
    
    /**
     * Update nutrient
     */
    public Nutrient updateNutrient(Integer id, AdminNutrientRequest request) {
        Nutrient nutrient = nutrientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nutrient not found with ID: " + id));
        
        nutrient.setName(request.getName());
        nutrient.setUnit(request.getUnit());
        return nutrientRepository.save(nutrient);
    }
    
    public void deleteNutrient(Integer id) {
        nutrientRepository.deleteById(id);
    }
    
    // ========== Helper Methods ==========
    
    private AdminFoodItemResponse mapToFoodItemResponse(FoodItem foodItem) {
        return AdminFoodItemResponse.builder()
                .id(foodItem.getId())
                .name(foodItem.getName())
                .description(foodItem.getDescription())
                .calo(foodItem.getCalo())
                .servingUnit(foodItem.getServingUnit())
                .standardServingSize(foodItem.getStandardServingSize())
                .status(foodItem.getStatus())
                .foodTypeName(foodItem.getFoodType() != null ? foodItem.getFoodType().getName() : null)
                .imageUrl(foodItem.getImageUrl())
                .nutrientCount(foodItem.getFoodNutrients() != null ? foodItem.getFoodNutrients().size() : 0)
                .build();
    }
    
    private AdminFoodItemDetailResponse mapToFoodItemDetailResponse(FoodItem foodItem) {
        List<AdminFoodNutrientResponse> nutrients = foodNutrientRepository
                .findByFoodItemIdOrderByNutrientNameAsc(foodItem.getId()).stream()
                .map(this::mapToFoodNutrientResponse)
                .collect(Collectors.toList());
        
        long mealLogCount = mealLogRepository.countByFoodItemId(foodItem.getId());
        
        return AdminFoodItemDetailResponse.builder()
                .id(foodItem.getId())
                .name(foodItem.getName())
                .description(foodItem.getDescription())
                .calo(foodItem.getCalo())
                .servingUnit(foodItem.getServingUnit())
                .standardServingSize(foodItem.getStandardServingSize())
                .status(foodItem.getStatus())
                .dataSource(foodItem.getDataSource())
                .imageUrl(foodItem.getImageUrl())
                .foodTypeId(foodItem.getFoodType() != null ? foodItem.getFoodType().getId() : null)
                .foodTypeName(foodItem.getFoodType() != null ? foodItem.getFoodType().getName() : null)
                .nutrients(nutrients)
                .nutrientCount(nutrients.size())
                .mealLogCount(mealLogCount)
                .build();
    }
    
    private AdminFoodNutrientResponse mapToFoodNutrientResponse(FoodNutrient foodNutrient) {
        String displayValue = foodNutrient.getValue() + " " + 
                (foodNutrient.getNutrient().getUnit() != null ? foodNutrient.getNutrient().getUnit() : "");
        
        return AdminFoodNutrientResponse.builder()
                .id(foodNutrient.getId())
                .foodItemId(foodNutrient.getFoodItem().getId())
                .foodItemName(foodNutrient.getFoodItem().getName())
                .nutrientId(foodNutrient.getNutrient().getId())
                .nutrientName(foodNutrient.getNutrient().getName())
                .nutrientUnit(foodNutrient.getNutrient().getUnit())
                .value(foodNutrient.getValue())
                .displayValue(displayValue.trim())
                .build();
    }
}
