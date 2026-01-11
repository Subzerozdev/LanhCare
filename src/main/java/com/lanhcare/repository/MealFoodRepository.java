package com.lanhcare.repository;

import com.lanhcare.entity.MealFood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MealFoodRepository extends JpaRepository<MealFood, Integer> {
    List<MealFood> findByMealLogId(int mealId);
}
