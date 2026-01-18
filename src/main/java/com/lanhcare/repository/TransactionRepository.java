package com.lanhcare.repository;

import com.lanhcare.entity.Transaction;
import com.lanhcare.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Transaction entity
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    
    /**
     * Find all transactions for an account
     */
    List<Transaction> findByAccountIdOrderByIdDesc(Integer accountId);
    
    // ========== ADMIN QUERIES ==========
    
    /**
     * Find all transactions with pagination
     */
    Page<Transaction> findAllByOrderByTransactionDateDesc(Pageable pageable);
    
    /**
     * Find transactions by status
     */
    Page<Transaction> findByStatusOrderByTransactionDateDesc(TransactionStatus status, Pageable pageable);
    
    /**
     * Find transactions by account ID
     */
    Page<Transaction> findByAccountIdOrderByTransactionDateDesc(Integer accountId, Pageable pageable);
    
    /**
     * Find transactions by service plan ID
     */
    Page<Transaction> findByServicePlanIdOrderByTransactionDateDesc(Integer servicePlanId, Pageable pageable);
    
    /**
     * Find transactions by date range
     */
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate DESC")
    Page<Transaction> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate,
                                       Pageable pageable);
    
    /**
     * Find transactions by date range and status
     */
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate " +
           "AND t.status = :status ORDER BY t.transactionDate DESC")
    Page<Transaction> findByDateRangeAndStatus(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate,
                                                @Param("status") TransactionStatus status,
                                                Pageable pageable);
    
    /**
     * Find transactions by account and date range
     */
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY t.transactionDate DESC")
    Page<Transaction> findByAccountIdAndDateRange(@Param("accountId") Integer accountId,
                                                    @Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate,
                                                    Pageable pageable);
    
    /**
     * Calculate total revenue
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.status = 'COMPLETED'")
    BigDecimal calculateTotalRevenue();
    
    /**
     * Calculate total revenue by date range
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.status = 'COMPLETED' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueByDateRange(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
    
    /**
     * Calculate revenue by service plan
     */
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.status = 'COMPLETED' " +
           "AND t.servicePlan.id = :servicePlanId")
    BigDecimal calculateRevenueByServicePlan(@Param("servicePlanId") Integer servicePlanId);
    
    /**
     * Count transactions by status
     */
    long countByStatus(TransactionStatus status);
    
    /**
     * Count transactions by date range
     */
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    long countByDateRange(@Param("startDate") LocalDateTime startDate,
                          @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get revenue statistics by month
     * Using PostgreSQL to_char() function instead of MySQL DATE_FORMAT()
     */
    @Query(value = "SELECT TO_CHAR(t.transaction_date, 'YYYY-MM') as month, " +
           "COUNT(t.id) as count, COALESCE(SUM(t.amount), 0) as revenue " +
           "FROM transactions t WHERE t.status = 'COMPLETED' " +
           "AND t.transaction_date BETWEEN :startDate AND :endDate " +
           "GROUP BY TO_CHAR(t.transaction_date, 'YYYY-MM') " +
           "ORDER BY month", nativeQuery = true)
    List<Object[]> getRevenueStatsByMonth(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get revenue statistics by service plan
     */
    @Query("SELECT t.servicePlan.id, t.servicePlan.name, " +
           "COUNT(t) as count, COALESCE(SUM(t.amount), 0) as revenue " +
           "FROM Transaction t WHERE t.status = 'COMPLETED' " +
           "AND t.transactionDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.servicePlan.id, t.servicePlan.name " +
           "ORDER BY revenue DESC")
    List<Object[]> getRevenueStatsByServicePlan(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count transactions by service plan ID
     */
    long countByServicePlanId(Integer servicePlanId);
    
    /**
     * Find transactions by service plan ID (for detail view)
     */
    List<Transaction> findByServicePlanIdOrderByTransactionDateDesc(Integer servicePlanId);
}

