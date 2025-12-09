package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.revenue.AdminCreateTransactionRequest;
import com.lanhcare.dto.admin.revenue.AdminTransactionResponse;
import com.lanhcare.dto.admin.revenue.RevenueStatsResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.Account;
import com.lanhcare.entity.ServicePlan;
import com.lanhcare.entity.Transaction;
import com.lanhcare.entity.TransactionStatus;
import com.lanhcare.repository.AccountRepository;
import com.lanhcare.repository.ServicePlanRepository;
import com.lanhcare.repository.TransactionRepository;
import com.lanhcare.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Revenue & Transaction Service
 */
@Service
@Transactional
public class AdminRevenueService {
    
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final ServicePlanRepository servicePlanRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public AdminRevenueService(TransactionRepository transactionRepository,
                               AccountRepository accountRepository,
                               ServicePlanRepository servicePlanRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.servicePlanRepository = servicePlanRepository;
    }
    
    /**
     * Get all transactions with filters
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminTransactionResponse> getAllTransactions(
            TransactionStatus status,
            Integer userId,
            Integer servicePlanId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactionPage;
        
        // Apply filters
        if (startDate != null && endDate != null && status != null) {
            transactionPage = transactionRepository.findByDateRangeAndStatus(startDate, endDate, status, pageable);
        } else if (startDate != null && endDate != null && userId != null) {
            transactionPage = transactionRepository.findByAccountIdAndDateRange(userId, startDate, endDate, pageable);
        } else if (startDate != null && endDate != null) {
            transactionPage = transactionRepository.findByDateRange(startDate, endDate, pageable);
        } else if (status != null) {
            transactionPage = transactionRepository.findByStatusOrderByTransactionDateDesc(status, pageable);
        } else if (userId != null) {
            transactionPage = transactionRepository.findByAccountIdOrderByTransactionDateDesc(userId, pageable);
        } else if (servicePlanId != null) {
            transactionPage = transactionRepository.findByServicePlanIdOrderByTransactionDateDesc(servicePlanId, pageable);
        } else {
            transactionPage = transactionRepository.findAllByOrderByTransactionDateDesc(pageable);
        }
        
        List<AdminTransactionResponse> transactions = transactionPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminTransactionResponse>builder()
                .content(transactions)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(transactionPage.getNumber())
                        .pageSize(transactionPage.getSize())
                        .totalElements(transactionPage.getTotalElements())
                        .totalPages(transactionPage.getTotalPages())
                        .first(transactionPage.isFirst())
                        .last(transactionPage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Get transaction detail
     */
    @Transactional(readOnly = true)
    public AdminTransactionResponse getTransactionById(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        return mapToResponse(transaction);
    }
    
    /**
     * Get revenue statistics
     */
    @Transactional(readOnly = true)
    public RevenueStatsResponse getRevenueStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        // If no date range provided, use all time
        BigDecimal totalRevenue;
        long totalTransactions;
        
        if (startDate != null && endDate != null) {
            totalRevenue = transactionRepository.calculateRevenueByDateRange(startDate, endDate);
            totalTransactions = transactionRepository.countByDateRange(startDate, endDate);
        } else {
            totalRevenue = transactionRepository.calculateTotalRevenue();
            totalTransactions = transactionRepository.count();
        }
        
        // Count by status
        long completedCount = transactionRepository.countByStatus(TransactionStatus.COMPLETED);
        long pendingCount = transactionRepository.countByStatus(TransactionStatus.PENDING);
        long failedCount = transactionRepository.countByStatus(TransactionStatus.FAILED);
        
        // Calculate average
        BigDecimal averageValue = totalTransactions > 0 
                ? totalRevenue.divide(BigDecimal.valueOf(totalTransactions), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        // Get monthly revenue breakdown
        List<RevenueStatsResponse.MonthlyRevenue> monthlyRevenue = null;
        if (startDate != null && endDate != null) {
            List<Object[]> monthlyStats = transactionRepository.getRevenueStatsByMonth(startDate, endDate);
            monthlyRevenue = monthlyStats.stream()
                    .map(row -> RevenueStatsResponse.MonthlyRevenue.builder()
                            .month((String) row[0])
                            .transactionCount(((Number) row[1]).longValue())
                            .revenue((BigDecimal) row[2])
                            .build())
                    .collect(Collectors.toList());
        }
        
        // Get service plan revenue breakdown
        List<RevenueStatsResponse.ServicePlanRevenue> servicePlanRevenue = null;
        if (startDate != null && endDate != null) {
            List<Object[]> planStats = transactionRepository.getRevenueStatsByServicePlan(startDate, endDate);
            servicePlanRevenue = planStats.stream()
                    .map(row -> RevenueStatsResponse.ServicePlanRevenue.builder()
                            .servicePlanId((Integer) row[0])
                            .servicePlanName((String) row[1])
                            .transactionCount(((Number) row[2]).longValue())
                            .revenue((BigDecimal) row[3])
                            .build())
                    .collect(Collectors.toList());
        }
        
        return RevenueStatsResponse.builder()
                .totalRevenue(totalRevenue)
                .totalTransactions(totalTransactions)
                .completedTransactions(completedCount)
                .pendingTransactions(pendingCount)
                .failedTransactions(failedCount)
                .averageTransactionValue(averageValue)
                .revenueByMonth(monthlyRevenue)
                .revenueByServicePlan(servicePlanRevenue)
                .build();
    }
    
    /**
     * Get transactions for export
     */
    @Transactional(readOnly = true)
    public List<AdminTransactionResponse> getTransactionsForExport(
            LocalDateTime startDate, LocalDateTime endDate, TransactionStatus status) {
        
        List<Transaction> transactions;
        
        if (startDate != null && endDate != null && status != null) {
            transactions = transactionRepository.findByDateRangeAndStatus(
                    startDate, endDate, status, Pageable.unpaged()).getContent();
        } else if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByDateRange(
                    startDate, endDate, Pageable.unpaged()).getContent();
        } else {
            transactions = transactionRepository.findAll();
        }
        
        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Create transaction (Admin manual entry)
     */
    public AdminTransactionResponse createTransaction(AdminCreateTransactionRequest request) {
        Account account = accountRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + request.getUserId()));
        
        ServicePlan servicePlan = servicePlanRepository.findById(request.getServicePlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Service Plan not found with ID: " + request.getServicePlanId()));
        
        Transaction transaction = Transaction.builder()
                .account(account)
                .servicePlan(servicePlan)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(request.getStatus() != null ? request.getStatus() : TransactionStatus.COMPLETED)
                .build();
        
        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }
    
    /**
     * Update transaction status
     */
    public AdminTransactionResponse updateTransactionStatus(Integer id, TransactionStatus newStatus) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        
        // Validate status transition
        TransactionStatus currentStatus = transaction.getStatus();
        validateStatusTransition(currentStatus, newStatus);
        
        transaction.setStatus(newStatus);
        Transaction updated = transactionRepository.save(transaction);
        
        return mapToResponse(updated);
    }
    
    /**
     * Delete transaction (soft delete - set status to CANCELLED)
     */
    public void deleteTransaction(Integer id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        
        // Soft delete - set status to CANCELLED
        transaction.setStatus(TransactionStatus.CANCELLED);
        transactionRepository.save(transaction);
    }
    
    /**
     * Validate if status transition is allowed
     */
    private void validateStatusTransition(TransactionStatus current, TransactionStatus newStatus) {
        // Define allowed transitions
        // PENDING can go to: COMPLETED, FAILED, CANCELLED
        // COMPLETED can go to: REFUNDED
        // FAILED can go to: PENDING (retry)
        // REFUNDED and CANCELLED are final states
        
        if (current == newStatus) {
            return; // No change
        }
        
        boolean isValid = switch (current) {
            case PENDING -> newStatus == TransactionStatus.COMPLETED 
                         || newStatus == TransactionStatus.FAILED 
                         || newStatus == TransactionStatus.CANCELLED;
            case COMPLETED -> newStatus == TransactionStatus.REFUNDED;
            case FAILED -> newStatus == TransactionStatus.PENDING; // Allow retry
            case REFUNDED, CANCELLED -> false; // Final states
        };
        
        if (!isValid) {
            throw new IllegalStateException(
                    String.format("Cannot transition from %s to %s", current, newStatus));
        }
    }
    
    // ========== Helper Methods ==========
    
    private AdminTransactionResponse mapToResponse(Transaction transaction) {
        return AdminTransactionResponse.builder()
                .id(transaction.getId())
                .userId(transaction.getAccount().getId())
                .userEmail(transaction.getAccount().getEmail())
                .userName(transaction.getAccount().getFullname())
                .servicePlanName(transaction.getServicePlan().getName())
                .amount(transaction.getAmount())
                .paymentMethod(transaction.getPaymentMethod())
                .status(transaction.getStatus().toString())
                .transactionDate(transaction.getTransactionDate().format(DATE_FORMATTER))
                .build();
    }
}
