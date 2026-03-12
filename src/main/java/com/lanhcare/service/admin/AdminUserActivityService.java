package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.useractivity.AdminUserActivityResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.DailyLog;
import com.lanhcare.repository.DailyLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin User Activity Overview Service
 * Provides read-only view into user daily logs, meals, and exercises
 */
@Service
@Transactional(readOnly = true)
public class AdminUserActivityService {
    
    private final DailyLogRepository dailyLogRepository;
    
    public AdminUserActivityService(DailyLogRepository dailyLogRepository) {
        this.dailyLogRepository = dailyLogRepository;
    }
    
    /**
     * Get user daily logs with pagination
     */
    public PageResponse<AdminUserActivityResponse> getUserActivity(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DailyLog> logPage = dailyLogRepository.findByAccountIdOrderByLoggedDateDesc(userId, pageable);
        
        List<AdminUserActivityResponse> activities = logPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminUserActivityResponse>builder()
                .content(activities)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(logPage.getNumber())
                        .pageSize(logPage.getSize())
                        .totalElements(logPage.getTotalElements())
                        .totalPages(logPage.getTotalPages())
                        .first(logPage.isFirst())
                        .last(logPage.isLast())
                        .build())
                .build();
    }
    
    // ========== Helper Methods ==========
    
    private AdminUserActivityResponse mapToResponse(DailyLog log) {
        List<AdminUserActivityResponse.MealSummary> meals = log.getMealLogs().stream()
                .map(meal -> AdminUserActivityResponse.MealSummary.builder()
                        .id(meal.getId())
                        .mealType(meal.getMealType() != null ? meal.getMealType().toString() : null)
                        .totalCalories(meal.getTotalCalories())
                        .foodCount(meal.getMealFoods() != null ? meal.getMealFoods().size() : 0)
                        .build())
                .collect(Collectors.toList());
        
        List<AdminUserActivityResponse.ExerciseSummary> exercises = log.getExerciseLogs().stream()
                .map(ex -> AdminUserActivityResponse.ExerciseSummary.builder()
                        .id(ex.getId())
                        .exerciseTypeName(ex.getExerciseType() != null ? ex.getExerciseType().getActivity() : null)
                        .durationMinutes(ex.getDuration() != null ? ex.getDuration().intValue() : null)
                        .caloriesOut(ex.getCaloriesOut())
                        .build())
                .collect(Collectors.toList());
        
        return AdminUserActivityResponse.builder()
                .loggedDate(log.getLoggedDate() != null ? log.getLoggedDate().toString() : null)
                .stepAmount(log.getStepAmount())
                .totalCaloriesIn(log.getTotalCaloriesIn())
                .totalCaloriesOut(log.getTotalCaloriesOut())
                .meals(meals)
                .exercises(exercises)
                .build();
    }
}
