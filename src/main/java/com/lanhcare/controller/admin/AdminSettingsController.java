package com.lanhcare.controller.admin;

import com.lanhcare.dto.admin.settings.AdminSettingRequest;
import com.lanhcare.dto.admin.settings.AdminSettingResponse;
import com.lanhcare.dto.common.ApiResponse;
import com.lanhcare.service.admin.AdminSettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin System Settings Controller
 */
@RestController
@RequestMapping("/api/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - System Settings", description = "Admin APIs for system configuration management")
public class AdminSettingsController {
    
    private final AdminSettingsService settingsService;
    
    public AdminSettingsController(AdminSettingsService settingsService) {
        this.settingsService = settingsService;
    }
    
    /**
     * Get all settings
     */
    @GetMapping
    @Operation(summary = "Get all settings", description = "Get list of all system settings")
    public ResponseEntity<ApiResponse<List<AdminSettingResponse>>> getAllSettings() {
        List<AdminSettingResponse> settings = settingsService.getAllSettings();
        return ResponseEntity.ok(ApiResponse.success("Settings retrieved successfully", settings));
    }
    
    /**
     * Get setting by key
     */
    @GetMapping("/{key}")
    @Operation(summary = "Get setting by key", description = "Get a specific setting by its key")
    public ResponseEntity<ApiResponse<AdminSettingResponse>> getSettingByKey(@PathVariable String key) {
        AdminSettingResponse setting = settingsService.getSettingByKey(key);
        return ResponseEntity.ok(ApiResponse.success("Setting retrieved successfully", setting));
    }
    
    /**
     * Create or update setting
     */
    @PutMapping("/{key}")
    @Operation(summary = "Create/Update setting", description = "Create a new setting or update existing one")
    public ResponseEntity<ApiResponse<AdminSettingResponse>> createOrUpdateSetting(
            @PathVariable String key,
            @Valid @RequestBody AdminSettingRequest request) {
        
        AdminSettingResponse setting = settingsService.createOrUpdateSetting(key, request);
        return ResponseEntity.ok(ApiResponse.success("Setting saved successfully", setting));
    }
    
    /**
     * Delete setting
     */
    @DeleteMapping("/{key}")
    @Operation(summary = "Delete setting", description = "Soft delete a system setting")
    public ResponseEntity<ApiResponse<Void>> deleteSetting(@PathVariable String key) {
        settingsService.deleteSetting(key);
        return ResponseEntity.ok(ApiResponse.success("Setting deleted successfully", null));
    }
}
