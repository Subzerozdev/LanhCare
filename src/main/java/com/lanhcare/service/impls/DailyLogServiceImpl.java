package com.lanhcare.service.impls;

import com.lanhcare.dto.dailylog.DailyLogRequest;
import com.lanhcare.dto.dailylog.DailyLogResponse;
import com.lanhcare.entity.Account;
import com.lanhcare.entity.DailyLog;
import com.lanhcare.exception.exps.AuthenticationException;
import com.lanhcare.exception.exps.DailyLogException;
import com.lanhcare.repository.AccountRepository;
import com.lanhcare.repository.DailyLogRepository;
import com.lanhcare.service.DailyLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyLogServiceImpl implements DailyLogService {
    private final DailyLogRepository dailyLogRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public DailyLog createLog(DailyLogRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AuthenticationException("Account not found"));

        DailyLog dailyLog = DailyLog.builder()
                .loggedDate(request.getLoggedDate())
                .stepAmount(request.getStepAmount() == null ? 0 : request.getStepAmount())
                .account(account)
                .totalCaloriesIn(BigDecimal.ZERO)
                .totalCaloriesOut(BigDecimal.ZERO)
                .build();

        return dailyLogRepository.save(dailyLog);
    }

    @Override
    public DailyLog getLogById(Integer id) {
        return dailyLogRepository.findById(id)
                .orElseThrow(() -> new DailyLogException("Daily Log not found"));
    }

    @Override
    public DailyLog getLogByAccountAndDate(Integer accountId, LocalDate date) {
        return dailyLogRepository.findByAccountIdAndLoggedDate(accountId, date)
                .orElseThrow(() -> new DailyLogException("Không tìm thấy nhật ký cho ngày: " + date));
    }

    @Override
    @Transactional
    public DailyLog updateSteps(Integer id, Integer steps) {
        DailyLog log = dailyLogRepository.findById(id)
                .orElseThrow(() -> new DailyLogException("Daily Log not found"));

        log.setStepAmount(steps);

        // Cập nhật lại Calories Out vì steps thay đổi ảnh hưởng đến NEAT
        log.calculateCaloriesOut();

        return dailyLogRepository.save(log);
    }

    @Override
    public void deleteLog(Integer id) {
        dailyLogRepository.deleteById(id);
    }

    @Override
    public List<DailyLogResponse> getAllLogsByAccountId(Integer accountId) {
        // Bạn có thể thêm method findByAccountId vào Repository nếu cần
        return dailyLogRepository.findAll().stream()
                .filter(log -> log.getAccount().getId().equals(accountId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public DailyLogResponse mapToResponse(DailyLog entity) {
        return DailyLogResponse.builder()
                .id(entity.getId())
                .loggedDate(entity.getLoggedDate())
                .stepAmount(entity.getStepAmount())
                .totalCaloriesIn(entity.getTotalCaloriesIn())
                .totalCaloriesOut(entity.getTotalCaloriesOut())
                .accountId(entity.getAccount().getId())
                .build();
    }
}