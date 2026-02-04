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
     * Find specialties by hospital ID (Many-to-Many relationship)
     */
    @Query("SELECT DISTINCT ms FROM MedicalSpecialty ms JOIN ms.hospital h WHERE h.id = :hospitalId")
    List<MedicalSpecialty> findByHospitalId(@Param("hospitalId") Integer hospitalId);
    
    /**
     * Find specialties by hospital ID ordered by Vietnamese name
     */
    @Query("SELECT DISTINCT ms FROM MedicalSpecialty ms JOIN ms.hospital h WHERE h.id = :hospitalId ORDER BY ms.nameVn ASC")
    List<MedicalSpecialty> findByHospitalIdOrderByNameVnAsc(@Param("hospitalId") Integer hospitalId);
    
    /**
     * Find specialties by hospital ID and status
     */
    @Query("SELECT DISTINCT ms FROM MedicalSpecialty ms JOIN ms.hospital h WHERE h.id = :hospitalId AND ms.status = :status")
    List<MedicalSpecialty> findByHospitalIdAndStatus(@Param("hospitalId") Integer hospitalId, @Param("status") SpecialtyStatus status);
    
    /**
     * Find specialties by hospital ID with pagination
     */
    @Query("SELECT DISTINCT ms FROM MedicalSpecialty ms JOIN ms.hospital h WHERE h.id = :hospitalId")
    Page<MedicalSpecialty> findByHospitalId(@Param("hospitalId") Integer hospitalId, Pageable pageable);
    
    /**
     * Find specialties by status with pagination
     */
    Page<MedicalSpecialty> findByStatus(SpecialtyStatus status, Pageable pageable);
    
    /**
     * Find specialties by hospital ID and status with pagination
     */
    @Query("SELECT DISTINCT ms FROM MedicalSpecialty ms JOIN ms.hospital h WHERE h.id = :hospitalId AND ms.status = :status")
    Page<MedicalSpecialty> findByHospitalIdAndStatus(@Param("hospitalId") Integer hospitalId, @Param("status") SpecialtyStatus status, Pageable pageable);
    
    /**
     * Find specialties by ICD code URI with pagination (Many-to-Many relationship)
     */
    @Query("SELECT DISTINCT ms FROM MedicalSpecialty ms JOIN ms.icdCode icd WHERE icd.icdUri = :icdUri")
    Page<MedicalSpecialty> findByIcdCodeIcdUri(@Param("icdUri") String icdUri, Pageable pageable);
    
    /**
     * Search specialties by name (Vietnamese or English) and hospital
     */
    @Query("SELECT DISTINCT ms FROM MedicalSpecialty ms JOIN ms.hospital h WHERE h.id = :hospitalId " +
           "AND (LOWER(ms.nameVn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ms.nameEn) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<MedicalSpecialty> searchByHospitalIdAndName(@Param("hospitalId") Integer hospitalId,
                                                      @Param("search") String search,
                                                      Pageable pageable);
    
    /**
     * Search specialties by name (Vietnamese or English), hospital and status
     */
    @Query("SELECT DISTINCT ms FROM MedicalSpecialty ms JOIN ms.hospital h WHERE h.id = :hospitalId " +
           "AND ms.status = :status " +
           "AND (LOWER(ms.nameVn) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ms.nameEn) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<MedicalSpecialty> searchByHospitalIdAndStatusAndName(@Param("hospitalId") Integer hospitalId,
                                                                @Param("status") SpecialtyStatus status,
                                                                @Param("search") String search,
                                                                Pageable pageable);
    
    /**
     * Count specialties by hospital ID
     */
    @Query("SELECT COUNT(DISTINCT ms) FROM MedicalSpecialty ms JOIN ms.hospital h WHERE h.id = :hospitalId")
    long countByHospitalId(@Param("hospitalId") Integer hospitalId);
}
