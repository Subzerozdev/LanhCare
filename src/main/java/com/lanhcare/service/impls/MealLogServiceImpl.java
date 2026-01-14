package com.lanhcare.service.impls;

import com.lanhcare.dto.meallog.MealLogRequest;
import com.lanhcare.dto.meallog.MealLogResponse;
import com.lanhcare.entity.DailyLog;
import com.lanhcare.entity.MealLog;
import com.lanhcare.exception.exps.DailyLogException;
import com.lanhcare.exception.exps.MealLogException;
import com.lanhcare.repository.DailyLogRepository;
import com.lanhcare.repository.MealLogRepository;
import com.lanhcare.service.MealLogService;
import com.lanhcare.specification.MealLogSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MealLogServiceImpl implements MealLogService {
    private final MealLogRepository mealLogRepository;
    private final DailyLogRepository dailyLogRepository;

    @Override
    @Transactional
    public MealLog create(MealLogRequest request) {
        DailyLog dailyLog = dailyLogRepository.findById(request.getDailyLogId())
                .orElseThrow(() -> new DailyLogException("Daily Log not found"));

        MealLog mealLog = MealLog.builder()
                .dailyLog(dailyLog)
                .mealType(request.getMealType())
                .loggedTime(request.getLoggedTime())
                .notes(request.getNotes())
                .totalCalories(BigDecimal.ZERO)
                .build();

        return mealLogRepository.save(mealLog);
    }

    @Override
    @Transactional
    public MealLog update(Integer id, MealLogRequest request) {
        MealLog mealLog = getById(id);

        Optional.ofNullable(request.getMealType()).ifPresent(mealLog::setMealType);
        Optional.ofNullable(request.getLoggedTime()).ifPresent(mealLog::setLoggedTime);
        Optional.ofNullable(request.getNotes()).ifPresent(mealLog::setNotes);

        mealLog.calculateTotalCalories();

        MealLog saved = mealLogRepository.save(mealLog);

        DailyLog dailyLog = saved.getDailyLog();
        dailyLog.calculateCaloriesIn();
        dailyLog.calculateCaloriesOut();
        dailyLogRepository.save(dailyLog);

        return saved;
    }

    @Override
    public MealLog getById(int id) {
        return mealLogRepository.findById(id)
                .orElseThrow(() -> new MealLogException("MealLog not found"));
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        MealLog mealLog = getById(id);

        DailyLog dailyLog = mealLog.getDailyLog();
        dailyLog.getMealLogs().remove(mealLog);
        mealLogRepository.delete(mealLog);

        dailyLog.calculateCaloriesIn();
        dailyLog.calculateCaloriesOut();
        dailyLogRepository.save(dailyLog);
    }

    @Override
    public Page<MealLogResponse> getByAccountId(int accountId, Pageable pageable, Map<String, String> criteria) {
        if (accountId == 0) {
            throw new MealLogException("Account Id not found");
        }

        if (criteria == null) {
            criteria = new HashMap<>();
        }

        criteria.put("accountId", String.valueOf(accountId));
        Specification<MealLog> spec = MealLogSpec.filterByCriteria(criteria);
        Page<MealLog> mealLogs = mealLogRepository.findAll(spec, pageable);
        return mealLogs.map(this::mapToResponse);
    }

    @Override
    public List<MealLogResponse> getByDailyLogId(Integer dailyLogId) {
        return mealLogRepository.findByDailyLogId(dailyLogId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public MealLogResponse mapToResponse(MealLog mealLog) {
        if (mealLog == null) return null;

        return MealLogResponse.builder()
                .id(mealLog.getId())
                .dailyLogId(mealLog.getDailyLog().getId())
                .mealType(mealLog.getMealType())
                .mealTypeName(mealLog.getMealType().getName())
                .mealDate(mealLog.getDailyLog().getLoggedDate())
                .loggedTime(mealLog.getLoggedTime())
                .totalCalories(mealLog.getTotalCalories())
                .notes(mealLog.getNotes())
                .createdAt(mealLog.getCreatedAt())
                .build();
    }
}
