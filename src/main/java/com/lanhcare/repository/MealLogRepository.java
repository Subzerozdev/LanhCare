package com.lanhcare.repository;

import com.lanhcare.entity.MealLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    List<MealLog> findByDailyLog_Account_IdOrderByDailyLog_LoggedDateDescLoggedTimeDesc(Integer accountId);
    
    /**
     * Find meal logs for an account on a specific date
     */
    List<MealLog> findByDailyLog_Account_IdAndDailyLog_LoggedDate(Integer accountId, LocalDate mealDate);
    
    /**
     * Find meal logs for an account within a date range
     */
    @Query("SELECT m FROM MealLog m WHERE m.dailyLog.account.id = :accountId " +
           "AND m.dailyLog.loggedDate BETWEEN :startDate AND :endDate " +
           "ORDER BY m.dailyLog.loggedDate DESC, m.loggedTime DESC")
    List<MealLog> findByDailyLog_Account_IdAndDateRange(
            @Param("accountId") Integer accountId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    /**
     * Count meal logs by food item
     */
    long countByMealFoods_FoodItemId(Integer foodItemId);

    Page<MealLog> findAll(Specification<MealLog> spec, Pageable pageable);

    List<MealLog> findByDailyLogId(Integer dailyLogId);
}
