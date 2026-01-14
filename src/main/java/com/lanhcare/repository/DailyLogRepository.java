package com.lanhcare.repository;

import com.lanhcare.entity.DailyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Integer> {
    Optional<DailyLog> findByAccountIdAndLoggedDate(Integer accountId, LocalDate loggedDate);
}