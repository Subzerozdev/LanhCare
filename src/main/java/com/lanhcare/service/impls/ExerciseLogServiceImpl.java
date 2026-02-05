package com.lanhcare.service.impls;

import com.lanhcare.dto.exerciselog.ExerciseLogRequest;
import com.lanhcare.dto.exerciselog.ExerciseLogResponse;
import com.lanhcare.entity.DailyLog;
import com.lanhcare.entity.ExerciseLog;
import com.lanhcare.entity.ExerciseType;
import com.lanhcare.exception.exps.DailyLogException;
import com.lanhcare.exception.exps.ExerciseException;
import com.lanhcare.repository.DailyLogRepository;
import com.lanhcare.repository.ExerciseLogRepository;
import com.lanhcare.service.ExerciseLogService;
import com.lanhcare.service.ExerciseTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseLogServiceImpl implements ExerciseLogService {
    private final ExerciseLogRepository exerciseLogRepository;
    private final DailyLogRepository dailyLogRepository;
    private final ExerciseTypeService exerciseTypeService;

    @Override
    @Transactional
    public ExerciseLog addExercise(ExerciseLogRequest request) {
        DailyLog dailyLog = dailyLogRepository.findById(request.getDailyLogId())
                .orElseThrow(() -> new DailyLogException("Daily Log not found"));

        ExerciseType type = exerciseTypeService.getById(request.getExerciseTypeId());

        ExerciseLog exerciseLog = ExerciseLog.builder()
                .duration(request.getDuration())
                .exerciseType(type)
                .dailyLog(dailyLog)
                .build();

        exerciseLog.calculateEAT();
        ExerciseLog saved = exerciseLogRepository.save(exerciseLog);

        dailyLog.calculateCaloriesOut();
        dailyLogRepository.save(dailyLog);

        return saved;
    }

    @Override
    @Transactional
    public ExerciseLog updateExercise(Integer id, ExerciseLogRequest request) {
        ExerciseLog exerciseLog = getById(id);

        if (request.getExerciseTypeId() != null && request.getExerciseTypeId() != 0) {
            ExerciseType type = exerciseTypeService.getById(request.getExerciseTypeId());
            exerciseLog.setExerciseType(type);
        }

        if (request.getDuration() != null && !request.getDuration().equals(BigDecimal.ZERO)) {
            exerciseLog.setDuration(request.getDuration());
        }

        exerciseLog.calculateEAT();
        ExerciseLog saved = exerciseLogRepository.save(exerciseLog);

        DailyLog dailyLog = saved.getDailyLog();
        dailyLog.calculateCaloriesOut();
        dailyLogRepository.save(dailyLog);

        return saved;
    }

    @Override
    public ExerciseLog getById(Integer id) {
        return exerciseLogRepository.findById(id)
                .orElseThrow(() -> new ExerciseException("Không tìm thấy nhật ký bài tập"));
    }

    @Transactional
    @Override
    public List<ExerciseLogResponse> getByDailyLogId(Integer dailyLogId) {
        return exerciseLogRepository.findByDailyLogId(dailyLogId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteExercise(Integer id) {
        ExerciseLog log = getById(id);

        DailyLog dailyLog = log.getDailyLog();
        dailyLog.getExerciseLogs().remove(log);
        exerciseLogRepository.delete(log);

        dailyLog.calculateCaloriesOut();
        dailyLogRepository.save(dailyLog);
    }

    @Override
    public ExerciseLogResponse mapToResponse(ExerciseLog entity) {
        return ExerciseLogResponse.builder()
                .id(entity.getId())
                .exerciseId(entity.getExerciseType().getId())
                .dailyLogId(entity.getDailyLog().getId())
                .activity(entity.getExerciseType().getActivity())
                .metValue(entity.getExerciseType().getMetValue())
                .duration(entity.getDuration())
                .caloriesOut(entity.getCaloriesOut())
                .dailyLogDate(entity.getDailyLog().getLoggedDate())
                .build();
    }
}