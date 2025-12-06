package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.user.*;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.AccountRole;
import com.lanhcare.entity.AccountStatus;
import com.lanhcare.service.admin.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin User Management Controller
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - User Management", description = "Admin APIs for managing users")
public class AdminUserController {
    
    private final AdminUserService userService;
    
    public AdminUserController(AdminUserService userService) {
        this.userService = userService;
    }
    
    /**
     * Get all users with pagination and filters
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Get paginated list of users with optional filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminUserResponse>>> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) AccountRole role,
            @RequestParam(required = false) AccountStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        PageResponse<AdminUserResponse> users = userService.getAllUsers(
                search, role, status, page, size, sortBy, sortDir);
        
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
    
    /**
     * Get user detail by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user detail", description = "Get detailed information about a user")
    public ResponseEntity<ApiResponse<AdminUserDetailResponse>> getUserDetail(@PathVariable Integer id) {
        AdminUserDetailResponse user = userService.getUserDetail(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }
    
    /**
     * Create new user
     */
    @PostMapping
    @Operation(summary = "Create user", description = "Create a new user account")
    public ResponseEntity<ApiResponse<AdminUserResponse>> createUser(
            @Valid @RequestBody AdminCreateUserRequest request) {
        
        AdminUserResponse user = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("User created successfully", user));
    }
    
    /**
     * Update user
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user information")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody AdminUpdateUserRequest request) {
        
        AdminUserResponse user = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", user));
    }
    
    /**
     * Change user status
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Change user status", description = "Change user account status")
    public ResponseEntity<ApiResponse<AdminUserResponse>> changeUserStatus(
            @PathVariable Integer id,
            @Valid @RequestBody AdminChangeStatusRequest request) {
        
        AdminUserResponse user = userService.changeUserStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", user));
    }
    
    /**
     * Delete user (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Soft delete a user (sets status to DELETED)")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}
