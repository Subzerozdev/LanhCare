package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.user.*;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.*;
import com.lanhcare.exception.ResourceAlreadyExistsException;
import com.lanhcare.exception.ResourceNotFoundException;
import com.lanhcare.repository.AccountRepository;
import com.lanhcare.repository.MealLogRepository;
import com.lanhcare.repository.TransactionRepository;
import com.lanhcare.repository.UserHealthProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin User Management Service
 */
@Service
@Transactional
public class AdminUserService {
    
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final MealLogRepository mealLogRepository;
    private final UserHealthProfileRepository healthProfileRepository;
    private final PasswordEncoder passwordEncoder;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public AdminUserService(AccountRepository accountRepository,
                            TransactionRepository transactionRepository,
                            MealLogRepository mealLogRepository,
                            UserHealthProfileRepository healthProfileRepository,
                            PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.mealLogRepository = mealLogRepository;
        this.healthProfileRepository = healthProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Get all users with pagination and filters
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminUserResponse> getAllUsers(String search, AccountRole role, AccountStatus status,
                                                         int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("DESC") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Account> accountPage;
        
        // Apply filters
        if (search != null && !search.isEmpty() && role != null && status != null) {
            accountPage = accountRepository.searchAccountsByRoleAndStatus(search, role, status, pageable);
        } else if (search != null && !search.isEmpty() && role != null) {
            accountPage = accountRepository.searchAccountsByRole(search, role, pageable);
        } else if (search != null && !search.isEmpty() && status != null) {
            accountPage = accountRepository.searchAccountsByStatus(search, status, pageable);
        } else if (search != null && !search.isEmpty()) {
            accountPage = accountRepository.searchAccounts(search, pageable);
        } else if (role != null && status != null) {
            accountPage = accountRepository.findByRoleAndStatus(role, status, pageable);
        } else if (role != null) {
            accountPage = accountRepository.findByRole(role, pageable);
        } else if (status != null) {
            accountPage = accountRepository.findByStatus(status, pageable);
        } else {
            accountPage = accountRepository.findAll(pageable);
        }
        
        List<AdminUserResponse> users = accountPage.getContent().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminUserResponse>builder()
                .content(users)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(accountPage.getNumber())
                        .pageSize(accountPage.getSize())
                        .totalElements(accountPage.getTotalElements())
                        .totalPages(accountPage.getTotalPages())
                        .first(accountPage.isFirst())
                        .last(accountPage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Get user detail by ID
     */
    @Transactional(readOnly = true)
    public AdminUserDetailResponse getUserDetail(Integer id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        return mapToUserDetailResponse(account);
    }
    
    /**
     * Create new user
     */
    public AdminUserResponse createUser(AdminCreateUserRequest request) {
        // Check if email already exists
        if (accountRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists: " + request.getEmail());
        }
        
        // Create new account
        Account account = Account.builder()
                .email(request.getEmail())
                .fullname(request.getFullname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(request.getStatus())
                .build();
        
        Account savedAccount = accountRepository.save(account);
        return mapToUserResponse(savedAccount);
    }
    
    /**
     * Update user
     */
    public AdminUserResponse updateUser(Integer id, AdminUpdateUserRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        // Update fields if provided
        if (request.getFullname() != null) {
            account.setFullname(request.getFullname());
        }
        if (request.getRole() != null) {
            account.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            account.setStatus(request.getStatus());
        }
        
        Account updatedAccount = accountRepository.save(account);
        return mapToUserResponse(updatedAccount);
    }
    
    /**
     * Change user status
     */
    public AdminUserResponse changeUserStatus(Integer id, AccountStatus status) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        account.setStatus(status);
        Account updatedAccount = accountRepository.save(account);
        return mapToUserResponse(updatedAccount);
    }
    
    /**
     * Delete user (soft delete - set status to DELETED)
     */
    public void deleteUser(Integer id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        
        // Soft delete - set status to DELETED
        account.setStatus(AccountStatus.DELETED);
        accountRepository.save(account);
    }
    
    // ========== Private Helper Methods ==========
    
    private AdminUserResponse mapToUserResponse(Account account) {
        // Calculate transaction stats
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByIdDesc(account.getId());
        BigDecimal totalSpent = transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return AdminUserResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .fullname(account.getFullname())
                .role(account.getRole())
                .status(account.getStatus())
                .transactionCount(transactions.size())
                .totalSpent(totalSpent)
                .build();
    }
    
    private AdminUserDetailResponse mapToUserDetailResponse(Account account) {
        // Get health profile
        AdminUserDetailResponse.HealthProfileSummary healthProfile = null;
        UserHealthProfile profile = healthProfileRepository.findByAccountId(account.getId()).orElse(null);
        if (profile != null) {
            healthProfile = AdminUserDetailResponse.HealthProfileSummary.builder()
                    .heightCm(profile.getHeightCm() != null ? profile.getHeightCm().doubleValue() : null)
                    .currentWeightKg(profile.getWeightKg())
                    .bmi(profile.getBmiValue() != null ? profile.getBmiValue().doubleValue() : null)
                    .activityLevel(profile.getActivityLevel() != null ? profile.getActivityLevel().toString() : null)
                    .build();
        }
        
        // Get recent transactions (last 5)
        List<Transaction> allTransactions = transactionRepository.findByAccountIdOrderByIdDesc(account.getId());
        List<AdminUserDetailResponse.TransactionSummary> recentTransactions = allTransactions.stream()
                .limit(5)
                .map(t -> AdminUserDetailResponse.TransactionSummary.builder()
                        .id(t.getId())
                        .amount(t.getAmount())
                        .status(t.getStatus().toString())
                        .transactionDate(t.getTransactionDate().format(DATE_FORMATTER))
                        .build())
                .collect(Collectors.toList());
        
        // Calculate total spent
        BigDecimal totalSpent = allTransactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Get meal log count
        Integer mealLogCount = mealLogRepository.findByAccountIdOrderByMealDateDescLoggedTimeDesc(account.getId()).size();
        
        return AdminUserDetailResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .fullname(account.getFullname())
                .role(account.getRole())
                .status(account.getStatus())
                .healthProfile(healthProfile)
                .recentTransactions(recentTransactions)
                .totalTransactionCount(allTransactions.size())
                .totalSpent(totalSpent)
                .mealLogCount(mealLogCount)
                .build();
    }
}
