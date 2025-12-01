package com.lanhcare.service;

import com.lanhcare.dto.account.AccountResponse;
import com.lanhcare.dto.account.UpdateAccountRequest;
import com.lanhcare.entity.Account;
import com.lanhcare.exception.ResourceNotFoundException;
import com.lanhcare.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Account Service
 * Handles account management operations
 */
@Service
public class AccountService {
    
    private static final Logger log = LoggerFactory.getLogger(AccountService.class);
    
    private final AccountRepository accountRepository;
    
    // Manual constructor instead of @RequiredArgsConstructor
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    
    /**
     * Get account by ID
     */
    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Integer id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        return mapToResponse(account);
    }
    
    /**
     * Get account by email
     */
    @Transactional(readOnly = true)
    public AccountResponse getAccountByEmail(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "email", email));
        return mapToResponse(account);
    }
    
    /**
     * Get all accounts
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all active accounts
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> getAllActiveAccounts() {
        return accountRepository.findAllActive().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Update account
     */
    @Transactional
    public AccountResponse updateAccount(Integer id, UpdateAccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        
        // Update fields if provided
        if (request.getEmail() != null) {
            account.setEmail(request.getEmail());
        }
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
        log.info("Account updated: {}", updatedAccount.getEmail());
        
        return mapToResponse(updatedAccount);
    }
    
    /**
     * Delete account
     */
    @Transactional
    public void deleteAccount(Integer id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        
        accountRepository.delete(account);
        log.info("Account deleted: {}", account.getEmail());
    }
    
    /**
     * Map Account entity to AccountResponse DTO
     */
    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .email(account.getEmail())
                .fullname(account.getFullname())
                .role(account.getRole())
                .status(account.getStatus())
                .build();
    }
}
