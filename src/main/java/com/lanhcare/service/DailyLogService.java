package com.lanhcare.service;

import com.lanhcare.dto.dailylog.DailyLogRequest;
import com.lanhcare.dto.dailylog.DailyLogResponse;
import com.lanhcare.entity.DailyLog;

import java.time.LocalDate;
import java.util.List;

public interface DailyLogService {
    DailyLog createLog(DailyLogRequest request);
    DailyLog getLogById(Integer id);
    DailyLog getLogByAccountAndDate(Integer accountId, LocalDate date);
    List<DailyLogResponse> getAllLogsByAccountId(Integer accountId);
    DailyLog updateSteps(Integer id, Integer steps);
    void deleteLog(Integer id);
    DailyLogResponse mapToResponse(DailyLog dailyLog);
}
