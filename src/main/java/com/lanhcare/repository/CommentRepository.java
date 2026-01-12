package com.lanhcare.repository;

import com.lanhcare.entity.Comment;
import com.lanhcare.enums.CommentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Comment entity
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    // ========== Basic Queries ==========
    
    /**
     * Find all comments ordered by creation date
     */
    Page<Comment> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Find comments by deleted status
     */
    Page<Comment> findByIsDeletedOrderByCreatedAtDesc(Boolean isDeleted, Pageable pageable);
    
    /**
     * Find comments by moderation status
     */
    Page<Comment> findByStatusOrderByCreatedAtDesc(CommentStatus status, Pageable pageable);
    
    /**
     * Find comments by post ID
     */
    Page<Comment> findByPostIdOrderByCreatedAtDesc(Integer postId, Pageable pageable);
    
    /**
     * Find comments by post ID and deleted status
     */
    Page<Comment> findByPostIdAndIsDeletedOrderByCreatedAtDesc(Integer postId, Boolean isDeleted, Pageable pageable);
    
    /**
     * Find comments by account ID
     */
    Page<Comment> findByAccountIdOrderByCreatedAtDesc(Integer accountId, Pageable pageable);
    
    /**
     * Find comments by account ID and deleted status
     */
    Page<Comment> findByAccountIdAndIsDeletedOrderByCreatedAtDesc(Integer accountId, Boolean isDeleted, Pageable pageable);
    
    // ========== Search Queries ==========
    
    /**
     * Search comments by content
     */
    @Query("SELECT c FROM Comment c WHERE LOWER(c.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY c.createdAt DESC")
    Page<Comment> searchComments(@Param("search") String search, Pageable pageable);
    
    /**
     * Search comments by content with status filter
     */
    @Query("SELECT c FROM Comment c WHERE LOWER(c.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "AND c.status = :status ORDER BY c.createdAt DESC")
    Page<Comment> searchCommentsByStatus(@Param("search") String search,
                                          @Param("status") CommentStatus status,
                                          Pageable pageable);
    
    /**
     * Search comments by content and post
     */
    @Query("SELECT c FROM Comment c WHERE LOWER(c.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "AND c.post.id = :postId ORDER BY c.createdAt DESC")
    Page<Comment> searchCommentsByPost(@Param("search") String search, 
                                        @Param("postId") Integer postId, 
                                        Pageable pageable);
    
    /**
     * Search comments by content, post, and deleted status
     */
    @Query("SELECT c FROM Comment c WHERE LOWER(c.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "AND c.post.id = :postId AND c.isDeleted = :isDeleted ORDER BY c.createdAt DESC")
    Page<Comment> searchCommentsByPostAndDeletedStatus(@Param("search") String search,
                                                         @Param("postId") Integer postId,
                                                         @Param("isDeleted") Boolean isDeleted,
                                                         Pageable pageable);
    
    /**
     * Search comments by content and user
     */
    @Query("SELECT c FROM Comment c WHERE LOWER(c.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "AND c.account.id = :userId ORDER BY c.createdAt DESC")
    Page<Comment> searchCommentsByUser(@Param("search") String search, 
                                        @Param("userId") Integer userId, 
                                        Pageable pageable);

    // Lấy các comment gốc của một bài post
    List<Comment> findByPostIdAndParentCommentIsNullAndIsDeletedFalseOrderByCreatedAtDesc(Integer postId);

    // Lấy các comment con dựa trên ID của comment cha
    List<Comment> findByParentCommentIdAndIsDeletedFalseOrderByCreatedAtAsc(Integer parentId);

    // ========== Statistics Queries ==========
    
    /**
     * Count comments by post ID
     */
    long countByPostId(Integer postId);
    
    /**
     * Count comments by post ID and deleted status
     */
    long countByPostIdAndIsDeleted(Integer postId, Boolean isDeleted);
    
    /**
     * Count comments by deleted status
     */
    long countByIsDeleted(Boolean isDeleted);
    
    /**
     * Count comments by moderation status
     */
    long countByStatus(CommentStatus status);
}
