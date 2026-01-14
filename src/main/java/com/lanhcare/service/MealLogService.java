package com.lanhcare.service;

import com.lanhcare.dto.meallog.MealLogRequest;
import com.lanhcare.dto.meallog.MealLogResponse;
import com.lanhcare.entity.MealLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface MealLogService {
    MealLog create(MealLogRequest request);

    MealLog update(Integer id, MealLogRequest request);

    MealLog getById(int id);

    void delete(Integer mealLogId);

    Page<MealLogResponse> getByAccountId(int accountId, Pageable pageable, Map<String, String> criteria);

    List<MealLogResponse> getByDailyLogId(Integer dailyLogId);

    MealLogResponse mapToResponse(MealLog mealLog);
}

