package com.lanhcare.service.impls;

import com.lanhcare.dto.meallog.MealFoodRequest;
import com.lanhcare.dto.meallog.MealFoodResponse;
import com.lanhcare.dto.meallog.MealLogRequest;
import com.lanhcare.entity.FoodItem;
import com.lanhcare.entity.MealFood;
import com.lanhcare.entity.MealLog;
import com.lanhcare.exception.exps.FoodItemException;
import com.lanhcare.exception.exps.MealFoodException;
import com.lanhcare.exception.exps.MealLogException;
import com.lanhcare.repository.FoodItemRepository;
import com.lanhcare.repository.MealFoodRepository;
import com.lanhcare.service.MealFoodService;
import com.lanhcare.service.MealLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MealFoodServiceImpl implements MealFoodService {
    private final MealFoodRepository mealFoodRepository;
    private final FoodItemRepository foodItemRepository;
    private final MealLogService mealLogService;

    @Override
    @Transactional
    public MealFood create(MealFoodRequest request) {
        FoodItem foodItem = foodItemRepository.findById(request.getFoodItemId())
                .orElseThrow(() -> new MealLogException("FoodItem không tồn tại"));

        MealLog mealLog = mealLogService.getById(request.getMealId());

        MealFood mealFood = MealFood.builder()
                .foodItem(foodItem)
                .quantity(request.getQuantity())
                .calories(BigDecimal.ZERO)
                .mealLog(mealLog)
                .build();
        mealFood.calculateCalories();

        MealFood saved = mealFoodRepository.save(mealFood);
        mealLogService.update(saved.getMealLog().getId(), MealLogRequest.builder().build());

        return saved;
    }

    @Override
    @Transactional
    public MealFood update(int id, MealFoodRequest request) {
        MealFood existingMealFood = findById(id);

        // Food Item
        if (request.getFoodItemId() != null
                && request.getFoodItemId() != 0
                && request.getFoodItemId() != existingMealFood.getFoodItem().getId()) {
            FoodItem newItem = foodItemRepository.findById(request.getFoodItemId())
                    .orElseThrow(() -> new FoodItemException("FoodItem mới không tồn tại"));
            existingMealFood.setFoodItem(newItem);
        }

        // Quantity
        Optional.ofNullable(request.getQuantity()).ifPresent(existingMealFood::setQuantity);

        existingMealFood.calculateCalories();

        MealFood saved = mealFoodRepository.save(existingMealFood);
        mealLogService.update(saved.getMealLog().getId(), MealLogRequest.builder().build());

        return saved;
    }

    @Override
    @Transactional
    public void delete(int id) {
        MealFood mealFood = findById(id);
        MealLog mealLog = mealFood.getMealLog();
        mealLog.getMealFoods().remove(mealFood);

        mealFoodRepository.deleteById(id);
        mealLogService.update(mealLog.getId(), MealLogRequest.builder().build());
    }

    @Override
    public MealFood findById(int id) {
        return mealFoodRepository.findById(id)
                .orElseThrow(() -> new MealFoodException("Không tìm thấy MealFood với ID: " + id));
    }

    @Override
    @Transactional
    public List<MealFoodResponse> getByMeal(int mealId) {
        return mealFoodRepository.findByMealLogId(mealId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public MealFoodResponse mapToResponse(MealFood mealFood) {
        if (mealFood == null) return null;

        return MealFoodResponse.builder()
                .id(mealFood.getId())
                .quantity(mealFood.getQuantity())
                .calories(mealFood.getCalories())
                .foodItemId(mealFood.getFoodItem() != null ? mealFood.getFoodItem().getId() : 0)
                .foodItemName(mealFood.getFoodItem().getName())
                .mealLogId(mealFood.getMealLog() != null ? mealFood.getMealLog().getId() : 0)
                .build();
    }
}