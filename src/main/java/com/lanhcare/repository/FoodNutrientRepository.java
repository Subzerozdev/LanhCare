package com.lanhcare.repository;

import com.lanhcare.entity.FoodNutrient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for FoodNutrient entity
 */
@Repository
public interface FoodNutrientRepository extends JpaRepository<FoodNutrient, Integer> {
    
    /**
     * Find all nutrients for a food item
     */
    List<FoodNutrient> findByFoodItemIdOrderByNutrientNameAsc(Integer foodItemId);
    
    /**
     * Find specific nutrient for a food item
     */
    Optional<FoodNutrient> findByFoodItemIdAndNutrientId(Integer foodItemId, Integer nutrientId);
    
    /**
     * Check if nutrient exists for a food item
     */
    boolean existsByFoodItemIdAndNutrientId(Integer foodItemId, Integer nutrientId);
    
    /**
     * Delete all nutrients for a food item
     */
    void deleteByFoodItemId(Integer foodItemId);
    
    /**
     * Count nutrients for a food item
     */
    long countByFoodItemId(Integer foodItemId);
}

