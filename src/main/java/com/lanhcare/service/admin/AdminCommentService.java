package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.comment.*;
import com.lanhcare.dto.common.PageResponse;
import com.lanhcare.entity.Comment;
import com.lanhcare.enums.CommentStatus;
import com.lanhcare.exception.exps.ResourceNotFoundException;
import com.lanhcare.repository.CommentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Comment Management Service with Moderation
 */
@Service
@Transactional
public class AdminCommentService {
    
    private final CommentRepository commentRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public AdminCommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    
    /**
     * Get all comments with pagination and filters
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminCommentResponse> getAllComments(String search, Integer postId, Integer userId,
                                                               Boolean isDeleted, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> commentPage;
        
        // Apply filters
        if (search != null && !search.isEmpty() && postId != null && isDeleted != null) {
            commentPage = commentRepository.searchCommentsByPostAndDeletedStatus(search, postId, isDeleted, pageable);
        } else if (search != null && !search.isEmpty() && postId != null) {
            commentPage = commentRepository.searchCommentsByPost(search, postId, pageable);
        } else if (search != null && !search.isEmpty() && userId != null) {
            commentPage = commentRepository.searchCommentsByUser(search, userId, pageable);
        } else if (search != null && !search.isEmpty()) {
            commentPage = commentRepository.searchComments(search, pageable);
        } else if (postId != null && isDeleted != null) {
            commentPage = commentRepository.findByPostIdAndIsDeletedOrderByCreatedAtDesc(postId, isDeleted, pageable);
        } else if (postId != null) {
            commentPage = commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable);
        } else if (userId != null && isDeleted != null) {
            commentPage = commentRepository.findByAccountIdAndIsDeletedOrderByCreatedAtDesc(userId, isDeleted, pageable);
        } else if (userId != null) {
            commentPage = commentRepository.findByAccountIdOrderByCreatedAtDesc(userId, pageable);
        } else if (isDeleted != null) {
            commentPage = commentRepository.findByIsDeletedOrderByCreatedAtDesc(isDeleted, pageable);
        } else {
            commentPage = commentRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        
        List<AdminCommentResponse> comments = commentPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminCommentResponse>builder()
                .content(comments)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(commentPage.getNumber())
                        .pageSize(commentPage.getSize())
                        .totalElements(commentPage.getTotalElements())
                        .totalPages(commentPage.getTotalPages())
                        .first(commentPage.isFirst())
                        .last(commentPage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Get comments by status (for moderation queue)
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminCommentResponse> getCommentsByStatus(CommentStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> commentPage = commentRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        
        List<AdminCommentResponse> comments = commentPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminCommentResponse>builder()
                .content(comments)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(commentPage.getNumber())
                        .pageSize(commentPage.getSize())
                        .totalElements(commentPage.getTotalElements())
                        .totalPages(commentPage.getTotalPages())
                        .first(commentPage.isFirst())
                        .last(commentPage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Get comment detail by ID
     */
    @Transactional(readOnly = true)
    public AdminCommentDetailResponse getCommentDetail(Integer id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + id));
        
        return mapToDetailResponse(comment);
    }
    
    /**
     * Get comments by post ID
     */
    @Transactional(readOnly = true)
    public PageResponse<AdminCommentResponse> getCommentsByPost(Integer postId, Boolean isDeleted, 
                                                                  int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> commentPage;
        
        if (isDeleted != null) {
            commentPage = commentRepository.findByPostIdAndIsDeletedOrderByCreatedAtDesc(postId, isDeleted, pageable);
        } else {
            commentPage = commentRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable);
        }
        
        List<AdminCommentResponse> comments = commentPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        
        return PageResponse.<AdminCommentResponse>builder()
                .content(comments)
                .pageable(PageResponse.PageInfo.builder()
                        .pageNumber(commentPage.getNumber())
                        .pageSize(commentPage.getSize())
                        .totalElements(commentPage.getTotalElements())
                        .totalPages(commentPage.getTotalPages())
                        .first(commentPage.isFirst())
                        .last(commentPage.isLast())
                        .build())
                .build();
    }
    
    /**
     * Approve comment
     */
    public AdminCommentResponse approveComment(Integer id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + id));
        
        comment.setStatus(CommentStatus.APPROVED);
        comment.setRejectionReason(null); // Clear rejection reason if any
        Comment approved = commentRepository.save(comment);
        return mapToResponse(approved);
    }
    
    /**
     * Reject comment
     */
    public AdminCommentResponse rejectComment(Integer id, String rejectionReason) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + id));
        
        comment.setStatus(CommentStatus.REJECTED);
        comment.setRejectionReason(rejectionReason);
        Comment rejected = commentRepository.save(comment);
        return mapToResponse(rejected);
    }
    
    /**
     * Delete comment (soft delete)
     */
    public void deleteComment(Integer id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + id));
        
        comment.setIsDeleted(true);
        commentRepository.save(comment);
    }
    
    /**
     * Restore deleted comment
     */
    public AdminCommentResponse restoreComment(Integer id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + id));
        
        comment.setIsDeleted(false);
        Comment restored = commentRepository.save(comment);
        return mapToResponse(restored);
    }
    
    /**
     * Permanently delete comment
     */
    public void permanentlyDeleteComment(Integer id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + id));
        
        commentRepository.delete(comment);
    }
    
    // ========== Private Helper Methods ==========
    
    private AdminCommentResponse mapToResponse(Comment comment) {
        long replyCount = comment.getReplies() != null 
                ? comment.getReplies().stream().filter(r -> !r.getIsDeleted()).count() 
                : 0;
        
        List<String> mediaUrls = comment.getMediaList() != null 
                ? comment.getMediaList().stream()
                    .map(media -> media.getUrl())
                    .collect(Collectors.toList())
                : List.of();
        
        return AdminCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .isDeleted(comment.getIsDeleted())
                .status(comment.getStatus())
                .rejectionReason(comment.getRejectionReason())
                .createdAt(comment.getCreatedAt() != null ? comment.getCreatedAt().format(DATE_FORMATTER) : null)
                .postId(comment.getPost().getId())
                .authorId(comment.getAccount().getId())
                .authorName(comment.getAccount().getFullname())
                .authorEmail(comment.getAccount().getEmail())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .replyCount(replyCount)
                .mediaCount(comment.getMediaList() != null ? comment.getMediaList().size() : 0)
                .mediaUrls(mediaUrls)
                .build();
    }
    
    private AdminCommentDetailResponse mapToDetailResponse(Comment comment) {
        long totalReplies = comment.getReplies() != null ? comment.getReplies().size() : 0;
        long activeReplies = comment.getReplies() != null 
                ? comment.getReplies().stream().filter(r -> !r.getIsDeleted()).count() 
                : 0;
        
        List<String> mediaUrls = comment.getMediaList() != null 
                ? comment.getMediaList().stream()
                    .map(media -> media.getUrl())
                    .collect(Collectors.toList())
                : List.of();
        
        return AdminCommentDetailResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .isDeleted(comment.getIsDeleted())
                .status(comment.getStatus())
                .rejectionReason(comment.getRejectionReason())
                .createdAt(comment.getCreatedAt() != null ? comment.getCreatedAt().format(DATE_FORMATTER) : null)
                .postId(comment.getPost().getId())
                .postContent(comment.getPost().getContent())
                .authorId(comment.getAccount().getId())
                .authorName(comment.getAccount().getFullname())
                .authorEmail(comment.getAccount().getEmail())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .totalReplies(totalReplies)
                .activeReplies(activeReplies)
                .mediaUrls(mediaUrls)
                .build();
    }
}
