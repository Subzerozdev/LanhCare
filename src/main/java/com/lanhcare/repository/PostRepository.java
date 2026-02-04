package com.lanhcare.repository;

import com.lanhcare.entity.Post;
import com.lanhcare.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Post entity
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    
    // ========== Basic Queries ==========
    
    /**
     * Find all posts ordered by creation date
     */
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Find posts by deleted status
     */
    Page<Post> findByIsDeletedOrderByCreatedAtDesc(Boolean isDeleted, Pageable pageable);
    
    /**
     * Find posts by moderation status
     */
    Page<Post> findByStatusOrderByCreatedAtDesc(PostStatus status, Pageable pageable);
    
    /**
     * Find posts by account ID
     */
    Page<Post> findByAccountIdOrderByCreatedAtDesc(Integer accountId, Pageable pageable);
    
    /**
     * Find all posts by account ID (for deletion)
     */
    List<Post> findByAccountId(Integer accountId);
    
    /**
     * Find posts by account ID and deleted status
     */
    Page<Post> findByAccountIdAndIsDeletedOrderByCreatedAtDesc(Integer accountId, Boolean isDeleted, Pageable pageable);
    
    /**
     * Find posts by date range
     */
    Page<Post> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // ========== Search Queries ==========
    
    /**
     * Search posts by content
     */
    @Query("SELECT p FROM Post p WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY p.createdAt DESC")
    Page<Post> searchPosts(@Param("search") String search, Pageable pageable);
    
    /**
     * Search posts by content with deleted status filter
     */
    @Query("SELECT p FROM Post p WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "AND p.isDeleted = :isDeleted ORDER BY p.createdAt DESC")
    Page<Post> searchPostsByDeletedStatus(@Param("search") String search, 
                                           @Param("isDeleted") Boolean isDeleted, 
                                           Pageable pageable);
    
    /**
     * Search posts by content with status filter
     */
    @Query("SELECT p FROM Post p WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "AND p.status = :status ORDER BY p.createdAt DESC")
    Page<Post> searchPostsByStatus(@Param("search") String search,
                                    @Param("status") PostStatus status,
                                    Pageable pageable);
    
    /**
     * Search posts by content and user
     */
    @Query("SELECT p FROM Post p WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "AND p.account.id = :userId ORDER BY p.createdAt DESC")
    Page<Post> searchPostsByUser(@Param("search") String search, 
                                  @Param("userId") Integer userId, 
                                  Pageable pageable);
    
    /**
     * Search posts by content, user, and deleted status
     */
    @Query("SELECT p FROM Post p WHERE LOWER(p.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "AND p.account.id = :userId AND p.isDeleted = :isDeleted ORDER BY p.createdAt DESC")
    Page<Post> searchPostsByUserAndDeletedStatus(@Param("search") String search, 
                                                   @Param("userId") Integer userId,
                                                   @Param("isDeleted") Boolean isDeleted,
                                                   Pageable pageable);
    
    // ========== Statistics Queries ==========
    
    /**
     * Count posts by deleted status
     */
    long countByIsDeleted(Boolean isDeleted);
    
    /**
     * Count posts by moderation status
     */
    long countByStatus(PostStatus status);
    
    /**
     * Count posts by date range
     */
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Count posts by deleted status and date range
     */
    @Query("SELECT COUNT(p) FROM Post p WHERE p.isDeleted = :isDeleted " +
           "AND p.createdAt BETWEEN :startDate AND :endDate")
    long countByIsDeletedAndCreatedAtBetween(@Param("isDeleted") Boolean isDeleted,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count posts by status and date range
     */
    @Query("SELECT COUNT(p) FROM Post p WHERE p.status = :status " +
           "AND p.createdAt BETWEEN :startDate AND :endDate")
    long countByStatusAndCreatedAtBetween(@Param("status") PostStatus status,
                                           @Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
}
