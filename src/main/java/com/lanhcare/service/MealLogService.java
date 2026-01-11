package com.lanhcare.service;

import com.lanhcare.dto.meallog.MealLogRequest;
import com.lanhcare.dto.meallog.MealLogResponse;
import com.lanhcare.entity.MealLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface MealLogService {
    MealLog create(MealLogRequest request);

    MealLog update(MealLogRequest request);

    MealLog getById(int id);

    void delete(int mealLogId);

    Page<MealLogResponse> getByAccountId(int accountId, Pageable pageable, Map<String, String> criteria);

    MealLogResponse mapToResponse(MealLog mealLog);
}

