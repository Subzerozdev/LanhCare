package com.lanhcare.controller;

import com.lanhcare.dto.account.AccountResponse;
import com.lanhcare.dto.account.UpdateAccountRequest;
import com.lanhcare.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Account Controller
 * Manages user account operations (requires authentication)
 */
@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Accounts", description = "Account management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {
    
    private final AccountService accountService;
    
    // Manual constructor instead of @RequiredArgsConstructor
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    
    /**
     * Get current user's account info
     */
    @GetMapping("/me")
    @Operation(summary = "Get current user's account", description = "Get the authenticated user's account information")
    public ResponseEntity<AccountResponse> getCurrentAccount(
            @RequestAttribute("userEmail") String email) {
        AccountResponse account = accountService.getAccountByEmail(email);
        return ResponseEntity.ok(account);
    }
    
    /**
     * Get account by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID", description = "Get account details by ID (Admin or self only)")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Integer id) {
        AccountResponse account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }
    
    /**
     * Get all accounts (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all accounts", description = "Get all user accounts (Admin only)")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }
    
    /**
     * Get all active accounts (Admin only)
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get active accounts", description = "Get all active user accounts (Admin only)")
    public ResponseEntity<List<AccountResponse>> getAllActiveAccounts() {
        List<AccountResponse> accounts = accountService.getAllActiveAccounts();
        return ResponseEntity.ok(accounts);
    }
    
    /**
     * Update account
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update account", description = "Update account information (Admin or self only)")
    public ResponseEntity<AccountResponse> updateAccount(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateAccountRequest request) {
        AccountResponse account = accountService.updateAccount(id, request);
        return ResponseEntity.ok(account);
    }
    
    /**
     * Delete account (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete account", description = "Delete user account (Admin only)")
    public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
