package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.post.*;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.enums.PostStatus;
import com.lanhcare.service.admin.AdminPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Admin Post Management Controller with Moderation
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin/posts")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Post Management", description = "Admin APIs for managing community posts with moderation")
public class AdminPostController {
    
    private final AdminPostService postService;
    
    public AdminPostController(AdminPostService postService) {
        this.postService = postService;
    }
    
    /**
     * Get all posts with pagination and filters
     */
    @GetMapping
    @Operation(summary = "Get all posts", description = "Get paginated list of posts with filters")
    public ResponseEntity<ApiResponse<PageResponse<AdminPostResponse>>> getAllPosts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Boolean isDeleted,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminPostResponse> posts = postService.getAllPosts(
                search, userId, isDeleted, startDate, endDate, page, size);
        
        return ResponseEntity.ok(ApiResponse.success("Posts retrieved successfully", posts));
    }
    
    /**
     * Get posts by moderation status (for moderation queue)
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get posts by status", description = "Get posts by moderation status (PENDING/APPROVED/REJECTED)")
    public ResponseEntity<ApiResponse<PageResponse<AdminPostResponse>>> getPostsByStatus(
            @PathVariable PostStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageResponse<AdminPostResponse> posts = postService.getPostsByStatus(status, page, size);
        return ResponseEntity.ok(ApiResponse.success("Posts retrieved successfully", posts));
    }
    
    /**
     * Get post detail by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get post detail", description = "Get detailed information about a post")
    public ResponseEntity<ApiResponse<AdminPostDetailResponse>> getPostDetail(@PathVariable Integer id) {
        AdminPostDetailResponse post = postService.getPostDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Post retrieved successfully", post));
    }
    
    /**
     * Get post statistics
     */
    @GetMapping("/stats")
    @Operation(summary = "Get post statistics", description = "Get statistics about posts")
    public ResponseEntity<ApiResponse<AdminPostStatsResponse>> getPostStats(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        
        AdminPostStatsResponse stats = postService.getPostStats(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Post statistics retrieved successfully", stats));
    }
    
    /**
     * Approve post
     */
    @PatchMapping("/{id}/approve")
    @Operation(summary = "Approve post", description = "Approve a pending post")
    public ResponseEntity<ApiResponse<AdminPostResponse>> approvePost(@PathVariable Integer id) {
        AdminPostResponse post = postService.approvePost(id);
        return ResponseEntity.ok(ApiResponse.success("Post approved successfully", post));
    }
    
    /**
     * Reject post
     */
    @PatchMapping("/{id}/reject")
    @Operation(summary = "Reject post", description = "Reject a post with reason")
    public ResponseEntity<ApiResponse<AdminPostResponse>> rejectPost(
            @PathVariable Integer id,
            @Valid @RequestBody AdminRejectPostRequest request) {
        
        AdminPostResponse post = postService.rejectPost(id, request.getRejectionReason());
        return ResponseEntity.ok(ApiResponse.success("Post rejected successfully", post));
    }
    
    /**
     * Delete post (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete post", description = "Soft delete a post (sets isDeleted to true)")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Integer id) {
        postService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success("Post deleted successfully", null));
    }
    
    /**
     * Restore deleted post
     */
    @PatchMapping("/{id}/restore")
    @Operation(summary = "Restore post", description = "Restore a soft-deleted post")
    public ResponseEntity<ApiResponse<AdminPostResponse>> restorePost(@PathVariable Integer id) {
        AdminPostResponse post = postService.restorePost(id);
        return ResponseEntity.ok(ApiResponse.success("Post restored successfully", post));
    }
    
    /**
     * Permanently delete post
     */
    @DeleteMapping("/{id}/permanent")
    @Operation(summary = "Permanently delete post", description = "Permanently delete a post and all its data")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeletePost(@PathVariable Integer id) {
        postService.permanentlyDeletePost(id);
        return ResponseEntity.ok(ApiResponse.success("Post permanently deleted", null));
    }
}
