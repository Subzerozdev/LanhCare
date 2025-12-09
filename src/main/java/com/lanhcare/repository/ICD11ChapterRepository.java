package com.lanhcare.repository;

import com.lanhcare.entity.ICD11Chapter;
import com.lanhcare.entity.ICD11Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ICD11Chapter entity
 */
@Repository
public interface ICD11ChapterRepository extends JpaRepository<ICD11Chapter, String> {
    
    /**
     * Find all with pagination
     */
    Page<ICD11Chapter> findAllByOrderByChapterCodeAsc(Pageable pageable);
    
    /**
     * Find by status
     */
    List<ICD11Chapter> findByStatus(ICD11Status status);
    
    /**
     * Find by status with pagination
     */
    Page<ICD11Chapter> findByStatusOrderByChapterCodeAsc(ICD11Status status, Pageable pageable);
    
    /**
     * Search by title (Vietnamese or English)
     */
    @Query("SELECT c FROM ICD11Chapter c WHERE " +
           "LOWER(c.vnTitle) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.originalTitleEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.chapterCode) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY c.chapterCode ASC")
    Page<ICD11Chapter> searchChapters(@Param("search") String search, Pageable pageable);
    
    /**
     * Search by title with status filter
     */
    @Query("SELECT c FROM ICD11Chapter c WHERE " +
           "(LOWER(c.vnTitle) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.originalTitleEn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.chapterCode) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND c.status = :status " +
           "ORDER BY c.chapterCode ASC")
    Page<ICD11Chapter> searchChaptersByStatus(@Param("search") String search, 
                                               @Param("status") ICD11Status status, 
                                               Pageable pageable);
    
    /**
     * Check if chapter code exists
     */
    boolean existsByChapterCode(String chapterCode);
    
    /**
     * Count by status
     */
    long countByStatus(ICD11Status status);
    
    /**
     * Find all active chapters
     */
    default List<ICD11Chapter> findAllActive() {
        return findByStatus(ICD11Status.ACTIVE);
    }
}

