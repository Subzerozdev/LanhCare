package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * System Setting Entity
 * Stores system-wide configuration settings (key-value pairs)
 */
@Entity
@Table(name = "SystemSetting")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSetting {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "setting_key", nullable = false, unique = true, length = 100)
    private String settingKey;
    
    @Column(name = "setting_value", columnDefinition = "TEXT")
    private String settingValue;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;
}
