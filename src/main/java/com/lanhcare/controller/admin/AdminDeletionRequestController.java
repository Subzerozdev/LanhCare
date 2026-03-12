package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.deletionrequest.AdminDeletionRequestResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.CustomerRequest;
import com.lanhcare.service.admin.AdminDeletionRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Deletion Request Management Controller
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin/deletion-requests")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Deletion Request Management", description = "Admin APIs for managing account deletion requests")
public class AdminDeletionRequestController {
    
    private final AdminDeletionRequestService requestService;
    
    public AdminDeletionRequestController(AdminDeletionRequestService requestService) {
        this.requestService = requestService;
    }
    
    /**
     * Get all deletion requests with pagination and filters
     */
    @GetMapping
    @Operation(summary = "Get all deletion requests", 
               description = "Get paginated list of deletion requests with optional status filter")
    public ResponseEntity<ApiResponse<PageResponse<AdminDeletionRequestResponse>>> getAllRequests(
            @RequestParam(required = false) CustomerRequest.RequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminDeletionRequestResponse> requests = requestService.getAllRequests(status, page, size);
        return ResponseEntity.ok(ApiResponse.success("Deletion requests retrieved successfully", requests));
    }
    
    /**
     * Get deletion request detail
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get deletion request detail", 
               description = "Get detailed information about a deletion request")
    public ResponseEntity<ApiResponse<AdminDeletionRequestResponse>> getRequestDetail(@PathVariable Integer id) {
        AdminDeletionRequestResponse request = requestService.getRequestDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Deletion request retrieved successfully", request));
    }
    
    /**
     * Approve deletion request
     */
    @PatchMapping("/{id}/approve")
    @Operation(summary = "Approve deletion request", 
               description = "Approve a deletion request (sets status to COMPLETED)")
    public ResponseEntity<ApiResponse<AdminDeletionRequestResponse>> approveRequest(@PathVariable Integer id) {
        AdminDeletionRequestResponse request = requestService.approveRequest(id);
        return ResponseEntity.ok(ApiResponse.success("Deletion request approved", request));
    }
    
    /**
     * Reject deletion request
     */
    @PatchMapping("/{id}/reject")
    @Operation(summary = "Reject deletion request", 
               description = "Reject a deletion request (sets status to CANCELLED)")
    public ResponseEntity<ApiResponse<AdminDeletionRequestResponse>> rejectRequest(@PathVariable Integer id) {
        AdminDeletionRequestResponse request = requestService.rejectRequest(id);
        return ResponseEntity.ok(ApiResponse.success("Deletion request rejected", request));
    }
    
    /**
     * Get deletion request statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Get deletion request stats", 
               description = "Get statistics about deletion requests by status")
    public ResponseEntity<ApiResponse<AdminDeletionRequestService.DeletionRequestStats>> getStats() {
        AdminDeletionRequestService.DeletionRequestStats stats = requestService.getStats();
        return ResponseEntity.ok(ApiResponse.success("Deletion request stats retrieved", stats));
    }
}
