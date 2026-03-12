package com.lanhcare.repository;

import com.lanhcare.entity.DailyLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Repository
public interface DailyLogRepository extends JpaRepository<DailyLog, Integer> {
    Optional<DailyLog> findByAccountIdAndLoggedDate(Integer accountId, LocalDate loggedDate);
    
    /**
     * Find all daily logs by account ID (for deletion)
     */
    List<DailyLog> findByAccountId(Integer accountId);

    /**
     * Find daily logs within a date range for health reports
     */
    List<DailyLog> findByAccountIdAndLoggedDateBetween(Integer accountId, LocalDate from, LocalDate to);
    
    // ========== ADMIN QUERIES ==========
    
    /**
     * Find daily logs by account with pagination
     */
    Page<DailyLog> findByAccountIdOrderByLoggedDateDesc(Integer accountId, Pageable pageable);
}