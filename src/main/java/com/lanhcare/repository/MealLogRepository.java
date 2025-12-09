package com.lanhcare.repository;

import com.lanhcare.entity.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for MealLog entity
 */
@Repository
public interface MealLogRepository extends JpaRepository<MealLog, Integer> {
    
    /**
     * Find all meal logs for an account (using loggedTime)
     */
    List<MealLog> findByAccountIdOrderByMealDateDescLoggedTimeDesc(Integer accountId);
    
    /**
     * Find meal logs for an account on a specific date
     */
    List<MealLog> findByAccountIdAndMealDate(Integer accountId, LocalDate mealDate);
    
    /**
     * Find meal logs for an account within a date range
     */
    @Query("SELECT m FROM MealLog m WHERE m.account.id = :accountId " +
           "AND m.mealDate BETWEEN :startDate AND :endDate " +
           "ORDER BY m.mealDate DESC, m.loggedTime DESC")
    List<MealLog> findByAccountIdAndDateRange(
            @Param("accountId") Integer accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    /**
     * Count meal logs by food item
     */
    long countByFoodItemId(Integer foodItemId);
}
