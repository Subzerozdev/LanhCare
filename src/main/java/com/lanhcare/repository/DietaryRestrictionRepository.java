package com.lanhcare.repository;

import com.lanhcare.entity.DietaryRestriction;
import com.lanhcare.enums.RestrictionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for DietaryRestriction entity
 */
@Repository
public interface DietaryRestrictionRepository extends JpaRepository<DietaryRestriction, Integer> {
    
    /**
     * Find restrictions by user health profile ID
     */
    List<DietaryRestriction> findByUserHealthProfileId(Integer userHealthProfileId);
    
    /**
     * Find restrictions by user health profile ID ordered by ID desc
     */
    Page<DietaryRestriction> findByUserHealthProfileIdOrderByIdDesc(Integer userHealthProfileId, Pageable pageable);
    
    /**
     * Find restrictions by status
     */
    Page<DietaryRestriction> findByStatusOrderByIdDesc(RestrictionStatus status, Pageable pageable);
    
    /**
     * Find restrictions by nutrient ID
     */
    Page<DietaryRestriction> findByNutrientIdOrderByIdDesc(Integer nutrientId, Pageable pageable);
    
    /**
     * Find restrictions by ICD code URI
     */
    Page<DietaryRestriction> findByIcdCodeIcdUriOrderByIdDesc(String icdUri, Pageable pageable);
    
    /**
     * Search restrictions
     */
    @Query("SELECT dr FROM DietaryRestriction dr WHERE " +
           "LOWER(dr.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(dr.description) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "ORDER BY dr.id DESC")
    Page<DietaryRestriction> searchRestrictions(@Param("search") String search, Pageable pageable);
    
    /**
     * Search restrictions by status
     */
    @Query("SELECT dr FROM DietaryRestriction dr WHERE " +
           "(LOWER(dr.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(dr.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND dr.status = :status " +
           "ORDER BY dr.id DESC")
    Page<DietaryRestriction> searchRestrictionsByStatus(@Param("search") String search, 
                                                         @Param("status") RestrictionStatus status, 
                                                         Pageable pageable);
    
    /**
     * Count by status
     */
    long countByStatus(RestrictionStatus status);
    
    /**
     * Count by user health profile ID
     */
    long countByUserHealthProfileId(Integer userHealthProfileId);
}
