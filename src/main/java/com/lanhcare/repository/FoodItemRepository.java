package com.lanhcare.repository;

import com.lanhcare.entity.FoodItem;
import com.lanhcare.entity.FoodItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    // ========== ADMIN QUERIES ==========
    
    /**
     * Find all food items with pagination
     */
    Page<FoodItem> findAllByOrderByIdDesc(Pageable pageable);
    
    /**
     * Find food items by status with pagination
     */
    Page<FoodItem> findByStatusOrderByIdDesc(FoodItemStatus status, Pageable pageable);
    
    /**
     * Find food items by food type with pagination
     */
    Page<FoodItem> findByFoodTypeIdOrderByIdDesc(Integer foodTypeId, Pageable pageable);
    
    /**
     * Find food items by food type and status
     */
    Page<FoodItem> findByFoodTypeIdAndStatusOrderByIdDesc(Integer foodTypeId, 
                                                           FoodItemStatus status, 
                                                           Pageable pageable);
    
    /**
     * Search food items by name with pagination
     */
    @Query("SELECT f FROM FoodItem f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(f.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY f.id DESC")
    Page<FoodItem> searchFoodItems(@Param("search") String search, Pageable pageable);
    
    /**
     * Search food items with status filter
     */
    @Query("SELECT f FROM FoodItem f WHERE (LOWER(f.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(f.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND f.status = :status ORDER BY f.id DESC")
    Page<FoodItem> searchFoodItemsByStatus(@Param("search") String search,
                                            @Param("status") FoodItemStatus status,
                                            Pageable pageable);
    
    /**
     * Count food items by status
     */
    long countByStatus(FoodItemStatus status);
    
    /**
     * Count food items by food type
     */
    long countByFoodTypeId(Integer foodTypeId);
}

