package com.lanhcare.service.admin;

import com.lanhcare.dto.admin.settings.AdminSettingRequest;
import com.lanhcare.dto.admin.settings.AdminSettingResponse;
import com.lanhcare.entity.SystemSetting;
import com.lanhcare.exception.ResourceNotFoundException;
import com.lanhcare.repository.SystemSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin System Settings Service
 */
@Service
@Transactional
public class AdminSettingsService {
    
    private final SystemSettingRepository settingRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public AdminSettingsService(SystemSettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }
    
    /**
     * Get all settings (not deleted)
     */
    @Transactional(readOnly = true)
    public List<AdminSettingResponse> getAllSettings() {
        return settingRepository.findAllByIsDeletedFalse().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get setting by key
     */
    @Transactional(readOnly = true)
    public AdminSettingResponse getSettingByKey(String key) {
        SystemSetting setting = settingRepository.findBySettingKeyAndIsDeletedFalse(key)
                .orElseThrow(() -> new ResourceNotFoundException("Setting not found with key: " + key));
        return mapToResponse(setting);
    }
    
    /**
     * Create or update setting
     */
    public AdminSettingResponse createOrUpdateSetting(String key, AdminSettingRequest request) {
        SystemSetting setting = settingRepository.findBySettingKeyAndIsDeletedFalse(key)
                .orElse(null);
        
        if (setting == null) {
            // Create new
            setting = SystemSetting.builder()
                    .settingKey(key)
                    .settingValue(request.getValue())
                    .description(request.getDescription())
                    .isDeleted(false)
                    .build();
        } else {
            // Update existing
            setting.setSettingValue(request.getValue());
            if (request.getDescription() != null) {
                setting.setDescription(request.getDescription());
            }
        }
        
        SystemSetting saved = settingRepository.save(setting);
        return mapToResponse(saved);
    }
    
    /**
     * Delete setting (soft delete)
     */
    public void deleteSetting(String key) {
        SystemSetting setting = settingRepository.findBySettingKeyAndIsDeletedFalse(key)
                .orElseThrow(() -> new ResourceNotFoundException("Setting not found with key: " + key));
        
        setting.setIsDeleted(true);
        settingRepository.save(setting);
    }
    
    // ========== Helper Methods ==========
    
    private AdminSettingResponse mapToResponse(SystemSetting setting) {
        return AdminSettingResponse.builder()
                .id(setting.getId())
                .key(setting.getSettingKey())
                .value(setting.getSettingValue())
                .description(setting.getDescription())
                .updatedAt(setting.getUpdatedAt() != null 
                        ? setting.getUpdatedAt().format(DATE_FORMATTER) 
                        : null)
                .build();
    }
}
