package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.post.*;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.Post;
import com.lanhcare.enums.PostStatus;
import com.lanhcare.exception.exps.ResourceNotFoundException;
import com.lanhcare.repository.CommentRepository;
import com.lanhcare.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Post Management Service with Moderation
 */
@Service
@Transactional
public class AdminPostService {
    
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public AdminPostService(PostRepository postRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }
    
    /**
     * Get all posts with pagination and filters
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminPostResponse> getAllPosts(String search, Integer userId, Boolean isDeleted,
                                                         LocalDateTime startDate, LocalDateTime endDate,
                                                         int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage;
        
        // Apply filters
        if (search != null && !search.isEmpty() && userId != null && isDeleted != null) {
            postPage = postRepository.searchPostsByUserAndDeletedStatus(search, userId, isDeleted, pageable);
        } else if (search != null && !search.isEmpty() && userId != null) {
            postPage = postRepository.searchPostsByUser(search, userId, pageable);
        } else if (search != null && !search.isEmpty() && isDeleted != null) {
            postPage = postRepository.searchPostsByDeletedStatus(search, isDeleted, pageable);
        } else if (search != null && !search.isEmpty()) {
            postPage = postRepository.searchPosts(search, pageable);
        } else if (userId != null && isDeleted != null) {
            postPage = postRepository.findByAccountIdAndIsDeletedOrderByCreatedAtDesc(userId, isDeleted, pageable);
        } else if (userId != null) {
            postPage = postRepository.findByAccountIdOrderByCreatedAtDesc(userId, pageable);
        } else if (isDeleted != null) {
            postPage = postRepository.findByIsDeletedOrderByCreatedAtDesc(isDeleted, pageable);
        } else if (startDate != null && endDate != null) {
            postPage = postRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startDate, endDate, pageable);
        } else {
            postPage = postRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        
        List<AdminPostResponse> posts = postPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminPostResponse>builder()
                .content(posts)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(postPage.getNumber())
                        .pageSize(postPage.getSize())
                        .totalElements(postPage.getTotalElements())
                        .totalPages(postPage.getTotalPages())
                        .first(postPage.isFirst())
                        .last(postPage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Get posts by status (for moderation queue)
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminPostResponse> getPostsByStatus(PostStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        
        List<AdminPostResponse> posts = postPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminPostResponse>builder()
                .content(posts)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(postPage.getNumber())
                        .pageSize(postPage.getSize())
                        .totalElements(postPage.getTotalElements())
                        .totalPages(postPage.getTotalPages())
                        .first(postPage.isFirst())
                        .last(postPage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Get post detail by ID
     */
    @Transactional(readOnly = true)
    public AdminPostDetailResponse getPostDetail(Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));
        
        return mapToDetailResponse(post);
    }
    
    /**
     * Get post statistics
     */
    @Transactional(readOnly = true)
    public AdminPostStatsResponse getPostStats(LocalDateTime startDate, LocalDateTime endDate) {
        long totalPosts;
        long activePosts;
        long deletedPosts;
        long pendingPosts;
        long approvedPosts;
        long rejectedPosts;
        
        if (startDate != null && endDate != null) {
            totalPosts = postRepository.countByCreatedAtBetween(startDate, endDate);
            activePosts = postRepository.countByIsDeletedAndCreatedAtBetween(false, startDate, endDate);
            deletedPosts = postRepository.countByIsDeletedAndCreatedAtBetween(true, startDate, endDate);
            pendingPosts = postRepository.countByStatusAndCreatedAtBetween(PostStatus.PENDING, startDate, endDate);
            approvedPosts = postRepository.countByStatusAndCreatedAtBetween(PostStatus.APPROVED, startDate, endDate);
            rejectedPosts = postRepository.countByStatusAndCreatedAtBetween(PostStatus.REJECTED, startDate, endDate);
        } else {
            totalPosts = postRepository.count();
            activePosts = postRepository.countByIsDeleted(false);
            deletedPosts = postRepository.countByIsDeleted(true);
            pendingPosts = postRepository.countByStatus(PostStatus.PENDING);
            approvedPosts = postRepository.countByStatus(PostStatus.APPROVED);
            rejectedPosts = postRepository.countByStatus(PostStatus.REJECTED);
        }
        
        long totalComments = commentRepository.count();
        long activeComments = commentRepository.countByIsDeleted(false);
        
        return AdminPostStatsResponse.builder()
                .totalPosts(totalPosts)
                .activePosts(activePosts)
                .deletedPosts(deletedPosts)
                .pendingPosts(pendingPosts)
                .approvedPosts(approvedPosts)
                .rejectedPosts(rejectedPosts)
                .totalComments(totalComments)
                .activeComments(activeComments)
                .build();
    }
    
    /**
     * Approve post
     */
    public AdminPostResponse approvePost(Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));
        
        post.setStatus(PostStatus.APPROVED);
        post.setRejectionReason(null); // Clear rejection reason if any
        Post approved = postRepository.save(post);
        return mapToResponse(approved);
    }
    
    /**
     * Reject post
     */
    public AdminPostResponse rejectPost(Integer id, String rejectionReason) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));
        
        post.setStatus(PostStatus.REJECTED);
        post.setRejectionReason(rejectionReason);
        Post rejected = postRepository.save(post);
        return mapToResponse(rejected);
    }
    
    /**
     * Delete post (soft delete)
     */
    public void deletePost(Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));
        
        post.setIsDeleted(true);
        postRepository.save(post);
    }
    
    /**
     * Restore deleted post
     */
    public AdminPostResponse restorePost(Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));
        
        post.setIsDeleted(false);
        Post restored = postRepository.save(post);
        return mapToResponse(restored);
    }
    
    /**
     * Permanently delete post
     */
    public void permanentlyDeletePost(Integer id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with ID: " + id));
        
        postRepository.delete(post);
    }
    
    // ========== Private Helper Methods ==========
    
    private AdminPostResponse mapToResponse(Post post) {
        long commentCount = commentRepository.countByPostIdAndIsDeleted(post.getId(), false);
        
        List<String> mediaUrls = post.getMediaList() != null 
                ? post.getMediaList().stream()
                    .map(media -> media.getUrl())
                    .collect(Collectors.toList())
                : List.of();
        
        return AdminPostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .heart(post.getHeart())
                .isDeleted(post.getIsDeleted())
                .status(post.getStatus())
                .rejectionReason(post.getRejectionReason())
                .createdAt(post.getCreatedAt() != null ? post.getCreatedAt().format(DATE_FORMATTER) : null)
                .authorId(post.getAccount().getId())
                .authorName(post.getAccount().getFullname())
                .authorEmail(post.getAccount().getEmail())
                .commentCount(commentCount)
                .mediaCount(post.getMediaList() != null ? post.getMediaList().size() : 0)
                .mediaUrls(mediaUrls)
                .build();
    }
    
    private AdminPostDetailResponse mapToDetailResponse(Post post) {
        long totalComments = commentRepository.countByPostId(post.getId());
        long activeComments = commentRepository.countByPostIdAndIsDeleted(post.getId(), false);
        long deletedComments = commentRepository.countByPostIdAndIsDeleted(post.getId(), true);
        
        List<String> mediaUrls = post.getMediaList() != null 
                ? post.getMediaList().stream()
                    .map(media -> media.getUrl())
                    .collect(Collectors.toList())
                : List.of();
        
        return AdminPostDetailResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .heart(post.getHeart())
                .isDeleted(post.getIsDeleted())
                .status(post.getStatus())
                .rejectionReason(post.getRejectionReason())
                .createdAt(post.getCreatedAt() != null ? post.getCreatedAt().format(DATE_FORMATTER) : null)
                .authorId(post.getAccount().getId())
                .authorName(post.getAccount().getFullname())
                .authorEmail(post.getAccount().getEmail())
                .totalComments(totalComments)
                .activeComments(activeComments)
                .deletedComments(deletedComments)
                .mediaUrls(mediaUrls)
                .build();
    }
}
