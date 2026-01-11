package com.lanhcare.service.impls;

import com.lanhcare.dto.meallog.MealFoodRequest;
import com.lanhcare.dto.meallog.MealFoodResponse;
import com.lanhcare.entity.FoodItem;
import com.lanhcare.entity.MealFood;
import com.lanhcare.entity.MealLog;
import com.lanhcare.exception.exps.FoodItemException;
import com.lanhcare.exception.exps.MealFoodException;
import com.lanhcare.exception.exps.MealLogException;
import com.lanhcare.repository.FoodItemRepository;
import com.lanhcare.repository.MealFoodRepository;
import com.lanhcare.repository.MealLogRepository;
import com.lanhcare.service.MealFoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealFoodServiceImpl implements MealFoodService {

    private final MealFoodRepository mealFoodRepository;
    private final FoodItemRepository foodItemRepository;
    private final MealLogRepository mealLogRepository;

    @Override
    @Transactional
    public MealFood create(MealFoodRequest request) {
        FoodItem foodItem = foodItemRepository.findById(request.getFoodItemId())
                .orElseThrow(() -> new MealLogException("FoodItem không tồn tại"));

        MealLog mealLog = mealLogRepository.findById(request.getMealId())
                .orElseThrow(() -> new MealLogException("Meal Log không tồn tại"));

        BigDecimal calculatedCalories = foodItem.getCalo()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        MealFood mealFood = MealFood.builder()
                .foodItem(foodItem)
                .quantity(request.getQuantity())
                .calories(calculatedCalories)
                .mealLog(mealLog)
                .build();

        mealFood.getMealLog().setTotalCalories(
                mealLog.getTotalCalories().add(mealFood.getCalories())
        );

        return mealFoodRepository.save(mealFood);
    }

    @Override
    @Transactional
    public MealFood update(int id, MealFoodRequest request) {
        MealFood existingMealFood = mealFoodRepository.findById(id)
                .orElseThrow(() -> new MealFoodException("Không tìm thấy MealFood với ID: " + id));

        existingMealFood.getMealLog().setTotalCalories(
                existingMealFood.getMealLog().getTotalCalories().subtract(existingMealFood.getCalories())
        );

        // Food Item
        if (request.getFoodItemId() != 0 && request.getFoodItemId() != existingMealFood.getFoodItem().getId()) {
            FoodItem newItem = foodItemRepository.findById(request.getFoodItemId())
                    .orElseThrow(() -> new FoodItemException("FoodItem mới không tồn tại"));
            existingMealFood.setFoodItem(newItem);
        }

        // Quantity
        Optional.of(request.getQuantity()).ifPresent(existingMealFood::setQuantity);

        BigDecimal newCalories = existingMealFood.getFoodItem().getCalo()
                .multiply(BigDecimal.valueOf(existingMealFood.getQuantity()));
        existingMealFood.setCalories(newCalories);
        existingMealFood.getMealLog().setTotalCalories(
                existingMealFood.getMealLog().getTotalCalories().add(existingMealFood.getCalories())
        );

        return mealFoodRepository.save(existingMealFood);
    }

    @Override
    @Transactional
    public void delete(int id) {
        MealFood mealFood = findById(id);
        mealFood.getMealLog().setTotalCalories(
                mealFood.getMealLog().getTotalCalories().subtract(mealFood.getCalories())
        );
        mealFoodRepository.save(mealFood);
        mealFoodRepository.delete(mealFood);
    }

    @Override
    public MealFood findById(int id) {
        return mealFoodRepository.findById(id)
                .orElseThrow(() -> new MealFoodException("Không tìm thấy MealFood với ID: " + id));
    }

    @Override
    public List<MealFoodResponse> getByMeal(int mealId) {
        return mealFoodRepository.findByMealLogId(mealId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MealFoodResponse mapToResponse(MealFood mealFood) {
        if (mealFood == null) return null;

        return MealFoodResponse.builder()
                .id(mealFood.getId())
                .quantity(mealFood.getQuantity())
                .calories(mealFood.getCalories())
                .foodItemId(mealFood.getFoodItem() != null ? mealFood.getFoodItem().getId() : 0)
                .mealLogId(mealFood.getMealLog() != null ? mealFood.getMealLog().getId() : 0)
                .build();
    }
}