package com.lanhcare.repository;

import com.lanhcare.entity.MedicalSpecialty;
import com.lanhcare.enums.SpecialtyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for MedicalSpecialty entity
 */
@Repository
public interface MedicalSpecialtyRepository extends JpaRepository<MedicalSpecialty, Integer> {
    
    /**
     * Find specialties by hospital ID
     */
    List<MedicalSpecialty> findByHospitalId(Integer hospitalId);
    
    /**
     * Find specialties by hospital ID ordered by Vietnamese name
     */
    List<MedicalSpecialty> findByHospitalIdOrderByNameVnAsc(Integer hospitalId);
    
    /**
     * Find specialties by hospital ID and status
     */
    List<MedicalSpecialty> findByHospitalIdAndStatus(Integer hospitalId, SpecialtyStatus status);
    
    /**
     * Find specialties by hospital ID with pagination
     */
    Page<MedicalSpecialty> findByHospitalId(Integer hospitalId, Pageable pageable);
    
    /**
     * Find specialties by status with pagination
     */
    Page<MedicalSpecialty> findByStatus(SpecialtyStatus status, Pageable pageable);
    
    /**
     * Find specialties by hospital ID and status with pagination
     */
    Page<MedicalSpecialty> findByHospitalIdAndStatus(Integer hospitalId, SpecialtyStatus status, Pageable pageable);
    
    /**
     * Find specialties by ICD code URI with pagination
     */
    Page<MedicalSpecialty> findByIcdCodeIcdUri(String icdUri, Pageable pageable);
    
    /**
     * Search specialties by name (Vietnamese or English) and hospital
     */
    @Query("SELECT ms FROM MedicalSpecialty ms WHERE ms.hospital.id = :hospitalId AND " +
           "(LOWER(ms.nameVn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ms.nameEn) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<MedicalSpecialty> searchByHospitalIdAndName(@Param("hospitalId") Integer hospitalId,
                                                      @Param("search") String search,
                                                      Pageable pageable);
    
    /**
     * Search specialties by name (Vietnamese or English), hospital and status
     */
    @Query("SELECT ms FROM MedicalSpecialty ms WHERE ms.hospital.id = :hospitalId AND ms.status = :status AND " +
           "(LOWER(ms.nameVn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ms.nameEn) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<MedicalSpecialty> searchByHospitalIdAndStatusAndName(@Param("hospitalId") Integer hospitalId,
                                                                @Param("status") SpecialtyStatus status,
                                                                @Param("search") String search,
                                                                Pageable pageable);
    
    /**
     * Count specialties by hospital ID
     */
    long countByHospitalId(Integer hospitalId);
    
    /**
     * Delete all specialties by hospital ID
     */
    void deleteByHospitalId(Integer hospitalId);
}
