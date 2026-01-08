package com.lanhcare.repository;

import com.lanhcare.entity.ICD11Translation;
import com.lanhcare.enums.TranslationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ICD11Translation entity
 */
@Repository
public interface ICD11TranslationRepository extends JpaRepository<ICD11Translation, Integer> {
    
    /**
     * Find translations by ICD code
     */
    List<ICD11Translation> findByIcdCodeIcdUri(String icdUri);
    
    /**
     * Find published translation by ICD code
     */
    Optional<ICD11Translation> findByIcdCodeIcdUriAndStatus(String icdUri, TranslationStatus status);
    
    /**
     * Find all with pagination
     */
    Page<ICD11Translation> findAllByOrderByIdDesc(Pageable pageable);
    
    /**
     * Find by status with pagination
     */
    Page<ICD11Translation> findByStatusOrderByIdDesc(TranslationStatus status, Pageable pageable);
    
    /**
     * Search translations
     */
    @Query("SELECT t FROM ICD11Translation t WHERE " +
           "LOWER(t.vnTitle) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.icdCode.icdCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.icdCode.originalTitleEn) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY t.id DESC")
    Page<ICD11Translation> searchTranslations(@Param("search") String search, Pageable pageable);
    
    /**
     * Search translations with status filter
     */
    @Query("SELECT t FROM ICD11Translation t WHERE " +
           "(LOWER(t.vnTitle) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.icdCode.icdCode) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.icdCode.originalTitleEn) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND t.status = :status " +
           "ORDER BY t.id DESC")
    Page<ICD11Translation> searchTranslationsByStatus(@Param("search") String search,
                                                       @Param("status") TranslationStatus status,
                                                       Pageable pageable);
    
    /**
     * Count by status
     */
    long countByStatus(TranslationStatus status);
    
    /**
     * Check if translation exists for a code
     */
    boolean existsByIcdCodeIcdUri(String icdUri);
}

