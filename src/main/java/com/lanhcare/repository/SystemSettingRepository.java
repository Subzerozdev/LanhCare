package com.lanhcare.repository;

import com.lanhcare.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for SystemSetting entity
 */
@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Integer> {
    
    /**
     * Find setting by key (not deleted)
     */
    Optional<SystemSetting> findBySettingKeyAndIsDeletedFalse(String settingKey);
    
    /**
     * Find all active settings
     */
    List<SystemSetting> findAllByIsDeletedFalse();
    
    /**
     * Check if setting exists by key
     */
    boolean existsBySettingKeyAndIsDeletedFalse(String settingKey);
}
