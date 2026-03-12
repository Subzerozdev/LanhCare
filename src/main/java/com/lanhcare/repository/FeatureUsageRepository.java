package com.lanhcare.repository;

import com.lanhcare.entity.FeatureUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeatureUsageRepository extends JpaRepository<FeatureUsage, Integer> {

    Optional<FeatureUsage> findByAccountIdAndFeatureCodeAndUsageDate(
            Integer accountId, String featureCode, LocalDate usageDate);
    
    // ========== ADMIN QUERIES ==========
    
    /**
     * Get total usage count grouped by feature code
     */
    @Query("SELECT f.featureCode, SUM(f.usageCount) FROM FeatureUsage f " +
           "GROUP BY f.featureCode ORDER BY SUM(f.usageCount) DESC")
    List<Object[]> getTotalUsageByFeature();
    
    /**
     * Get usage stats by feature and date range
     */
    @Query("SELECT f.featureCode, SUM(f.usageCount) FROM FeatureUsage f " +
           "WHERE f.usageDate BETWEEN :startDate AND :endDate " +
           "GROUP BY f.featureCode ORDER BY SUM(f.usageCount) DESC")
    List<Object[]> getUsageByFeatureAndDateRange(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);
    
    /**
     * Get daily usage trend for a specific feature
     */
    @Query("SELECT f.usageDate, SUM(f.usageCount) FROM FeatureUsage f " +
           "WHERE f.featureCode = :featureCode AND f.usageDate BETWEEN :startDate AND :endDate " +
           "GROUP BY f.usageDate ORDER BY f.usageDate")
    List<Object[]> getDailyTrend(@Param("featureCode") String featureCode,
                                  @Param("startDate") LocalDate startDate,
                                  @Param("endDate") LocalDate endDate);
    
    /**
     * Count unique users per feature
     */
    @Query("SELECT f.featureCode, COUNT(DISTINCT f.accountId) FROM FeatureUsage f " +
           "GROUP BY f.featureCode ORDER BY COUNT(DISTINCT f.accountId) DESC")
    List<Object[]> getUniqueUsersByFeature();
    
    /**
     * Get usage by account
     */
    Page<FeatureUsage> findByAccountIdOrderByUsageDateDesc(Integer accountId, Pageable pageable);
}
