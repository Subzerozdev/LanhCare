package com.lanhcare.repository;

import com.lanhcare.entity.MealFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MealFoodRepository extends JpaRepository<MealFood, Integer> {
    @Query("SELECT mf FROM MealFood mf WHERE mf.mealLog.id = :mealLogId")
    List<MealFood> findByMealLogId(@Param("mealLogId") int mealLogId);
}
