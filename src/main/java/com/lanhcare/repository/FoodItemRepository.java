package com.lanhcare.repository;

import com.lanhcare.entity.FoodItem;
import com.lanhcare.entity.FoodItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for FoodItem entity
 */
@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Integer> {
    
    /**
     * Find all food items by status
     */
    List<FoodItem> findByStatus(FoodItemStatus status);
    
    /**
     * Find all approved food items (active status)
     */
    default List<FoodItem> findAllApproved() {
        return findByStatus(FoodItemStatus.ACTIVE);
    }
    
    /**
     * Find food items by type
     */
    List<FoodItem> findByFoodTypeId(Integer foodTypeId);
    
    /**
     * Search food items by name (case-insensitive)
     */
    List<FoodItem> findByNameContainingIgnoreCase(String name);
}
