package com.lanhcare.repository;

import com.lanhcare.entity.ICD11Code;
import com.lanhcare.entity.ICD11Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ICD11Code entity
 */
@Repository
public interface ICD11CodeRepository extends JpaRepository<ICD11Code, String> {
    
    /**
     * Find all with pagination
     */
    Page<ICD11Code> findAllByOrderByIcdCodeAsc(Pageable pageable);
    
    /**
     * Find by status with pagination
     */
    Page<ICD11Code> findByStatusOrderByIcdCodeAsc(ICD11Status status, Pageable pageable);
    
    /**
     * Find by chapter
     */
    Page<ICD11Code> findByChapterChapterUriOrderByIcdCodeAsc(String chapterUri, Pageable pageable);
    
    /**
     * Find by chapter and status
     */
    Page<ICD11Code> findByChapterChapterUriAndStatusOrderByIcdCodeAsc(String chapterUri, 
                                                                       ICD11Status status, 
                                                                       Pageable pageable);
    
    /**
     * Find root codes (no parent)
     */
    Page<ICD11Code> findByParentIsNullOrderByIcdCodeAsc(Pageable pageable);
    
    /**
     * Find children of a code
     */
    List<ICD11Code> findByParentIcdUriOrderByIcdCodeAsc(String parentUri);
    
    /**
     * Search by code or title
     */
    @Query("SELECT c FROM ICD11Code c WHERE " +
           "LOWER(c.icdCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.originalTitleEn) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY c.icdCode ASC")
    Page<ICD11Code> searchCodes(@Param("search") String search, Pageable pageable);
    
    /**
     * Search by code or title with status filter
     */
    @Query("SELECT c FROM ICD11Code c WHERE " +
           "(LOWER(c.icdCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.originalTitleEn) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND c.status = :status " +
           "ORDER BY c.icdCode ASC")
    Page<ICD11Code> searchCodesByStatus(@Param("search") String search, 
                                         @Param("status") ICD11Status status, 
                                         Pageable pageable);
    
    /**
     * Search within a chapter
     */
    @Query("SELECT c FROM ICD11Code c WHERE c.chapter.chapterUri = :chapterUri AND " +
           "(LOWER(c.icdCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.originalTitleEn) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY c.icdCode ASC")
    Page<ICD11Code> searchCodesInChapter(@Param("chapterUri") String chapterUri,
                                          @Param("search") String search, 
                                          Pageable pageable);
    
    /**
     * Check if icd code exists
     */
    boolean existsByIcdCode(String icdCode);
    
    /**
     * Count by status
     */
    long countByStatus(ICD11Status status);
    
    /**
     * Count by chapter
     */
    long countByChapterChapterUri(String chapterUri);
    
    /**
     * Find leaf codes only
     */
    Page<ICD11Code> findByIsLeafTrueOrderByIcdCodeAsc(Pageable pageable);
}

