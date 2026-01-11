package com.lanhcare.service.impls;

import com.lanhcare.dto.meallog.MealLogRequest;
import com.lanhcare.dto.meallog.MealLogResponse;
import com.lanhcare.entity.Account;
import com.lanhcare.entity.MealLog;
import com.lanhcare.exception.exps.AuthenticationException;
import com.lanhcare.exception.exps.MealLogException;
import com.lanhcare.repository.AccountRepository;
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
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MealLogServiceImpl implements MealLogService {
    private final MealLogRepository mealLogRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public MealLog create(MealLogRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AuthenticationException("Account not found"));

        MealLog mealLog = MealLog.builder()
                .account(account)
                .mealType(request.getMealType())
                .mealDate(request.getMealDate())
                .loggedTime(LocalTime.now())
                .totalCalories(BigDecimal.ZERO)
                .notes(request.getNotes())
                .build();

        return mealLogRepository.save(mealLog);
    }

    @Override
    public MealLog update(MealLogRequest request) {
        MealLog mealLog = getById(request.getMealLogId());

        Optional.ofNullable(request.getMealType()).ifPresent(mealLog::setMealType);
        Optional.ofNullable(request.getNotes()).ifPresent(mealLog::setNotes);

        return mealLogRepository.save(mealLog);
    }

    @Override
    public MealLog getById(int id) {
        return mealLogRepository.findById(id)
                .orElseThrow(() -> new MealLogException("MealLog not found"));
    }

    @Override
    public void delete(int mealLogId) {
        mealLogRepository.deleteById(mealLogId);
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
    public MealLogResponse mapToResponse(MealLog mealLog) {
        if (mealLog == null) return null;

        return MealLogResponse.builder()
                .id(mealLog.getId())
                .accountId(mealLog.getAccount().getId())
                .mealType(mealLog.getMealType())
                .mealDate(mealLog.getMealDate())
                .loggedTime(mealLog.getLoggedTime())
                .totalCalories(mealLog.getTotalCalories())
                .notes(mealLog.getNotes())
                .createdAt(mealLog.getCreatedAt())
                .build();
    }
}
