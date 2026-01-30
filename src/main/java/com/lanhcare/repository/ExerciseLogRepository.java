package com.lanhcare.repository;

import com.lanhcare.entity.ExerciseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseLogRepository extends JpaRepository<ExerciseLog, Integer> {
    @Query("SELECT e FROM ExerciseLog e WHERE e.dailyLog.id = :dailyLogId")
    List<ExerciseLog> findByDailyLogId(@Param("dailyLogId") Integer dailyLogId);
}