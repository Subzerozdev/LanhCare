package com.lanhcare.repository;

import com.lanhcare.entity.ExerciseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Integer> {
    List<ExerciseLog> findByDailyLogId(Integer dailyLogId);
}