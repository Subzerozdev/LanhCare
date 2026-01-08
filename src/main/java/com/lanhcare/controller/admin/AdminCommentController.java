package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.comment.*;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.enums.CommentStatus;
import com.lanhcare.service.admin.AdminCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Comment Management Controller with Moderation
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin/comments")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Comment Management", description = "Admin APIs for managing comments with moderation")
public class AdminCommentController {
    
    private final AdminCommentService commentService;
    
    public AdminCommentController(AdminCommentService commentService) {
        this.commentService = commentService;
    }
    
    /**
     * Get all comments with pagination and filters
     */
    @GetMapping
    @Operation(summary = "Get all comments", description = "Get paginated list of comments with filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminCommentResponse>>> getAllComments(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer postId,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminCommentResponse> comments = commentService.getAllComments(
                search, postId, userId, isDeleted, page, size);
        
        return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", comments));
    }
    
    /**
     * Get comments by moderation status (for moderation queue)
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get comments by status", description = "Get comments by moderation status (PENDING/APPROVED/REJECTED)")
    public ResponseEntity<ApiResponse<PageResponse<AdminCommentResponse>>> getCommentsByStatus(
            @PathVariable CommentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminCommentResponse> comments = commentService.getCommentsByStatus(status, page, size);
        return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", comments));
    }
    
    /**
     * Get comment detail by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get comment detail", description = "Get detailed information about a comment")
    public ResponseEntity<ApiResponse<AdminCommentDetailResponse>> getCommentDetail(@PathVariable Integer id) {
        AdminCommentDetailResponse comment = commentService.getCommentDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Comment retrieved successfully", comment));
    }
    
    /**
     * Get comments by post ID
     */
    @GetMapping("/post/{postId}")
    @Operation(summary = "Get comments by post", description = "Get all comments for a specific post")
    public ResponseEntity<ApiResponse<PageResponse<AdminCommentResponse>>> getCommentsByPost(
            @PathVariable Integer postId,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminCommentResponse> comments = commentService.getCommentsByPost(
                postId, isDeleted, page, size);
        
        return ResponseEntity.ok(ApiResponse.success("Comments retrieved successfully", comments));
    }
    
    /**
     * Approve comment
     */
    @PatchMapping("/{id}/approve")
    @Operation(summary = "Approve comment", description = "Approve a pending comment")
    public ResponseEntity<ApiResponse<AdminCommentResponse>> approveComment(@PathVariable Integer id) {
        AdminCommentResponse comment = commentService.approveComment(id);
        return ResponseEntity.ok(ApiResponse.success("Comment approved successfully", comment));
    }
    
    /**
     * Reject comment
     */
    @PatchMapping("/{id}/reject")
    @Operation(summary = "Reject comment", description = "Reject a comment with reason")
    public ResponseEntity<ApiResponse<AdminCommentResponse>> rejectComment(
            @PathVariable Integer id,
            @Valid @RequestBody AdminRejectCommentRequest request) {
        
        AdminCommentResponse comment = commentService.rejectComment(id, request.getRejectionReason());
        return ResponseEntity.ok(ApiResponse.success("Comment rejected successfully", comment));
    }
    
    /**
     * Delete comment (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment", description = "Soft delete a comment (sets isDeleted to true)")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable Integer id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
    }
    
    /**
     * Restore deleted comment
     */
    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restore comment", description = "Restore a soft-deleted comment")
    public ResponseEntity<ApiResponse<AdminCommentResponse>> restoreComment(@PathVariable Integer id) {
        AdminCommentResponse comment = commentService.restoreComment(id);
        return ResponseEntity.ok(ApiResponse.success("Comment restored successfully", comment));
    }
    
    /**
     * Permanently delete comment
     */
    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Permanently delete comment", description = "Permanently delete a comment")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteComment(@PathVariable Integer id) {
        commentService.permanentlyDeleteComment(id);
        return ResponseEntity.ok(ApiResponse.success("Comment permanently deleted", null));
    }
}
