package com.lanhcare.service;

import com.lanhcare.dto.meallog.MealFoodRequest;
import com.lanhcare.dto.meallog.MealFoodResponse;
import com.lanhcare.entity.MealFood;

import java.util.List;

public interface MealFoodService {
    MealFood create(MealFoodRequest request);
    MealFood update(int id, MealFoodRequest request);
    void delete(int id);
    MealFood findById(int id);
    MealFoodResponse mapToResponse(MealFood mealFood);
    List<MealFoodResponse> getByMeal(int mealId);
}
