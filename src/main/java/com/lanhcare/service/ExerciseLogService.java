package com.lanhcare.service;

import com.lanhcare.dto.exerciselog.ExerciseLogRequest;
import com.lanhcare.dto.exerciselog.ExerciseLogResponse;
import com.lanhcare.entity.ExerciseLog;

import java.util.List;

public interface ExerciseLogService {
    ExerciseLog addExercise(ExerciseLogRequest request);
    ExerciseLog updateExercise(Integer id, ExerciseLogRequest request);
    ExerciseLog getById(Integer id);
    List<ExerciseLogResponse> getByDailyLogId(Integer dailyLogId);
    void deleteExercise(Integer id);
    ExerciseLogResponse mapToResponse(ExerciseLog exerciseLog);
}
