package com.lanhcare.entity;

import com.lanhcare.enums.ICD11Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "icd11_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ICD11Code {
    @Id
    @Column(name = "icd_uri")
    private String icdUri;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_uri")
    private ICD11Chapter chapter;
    
    @Column(name = "icd_code", length = 50)
    private String icdCode;
    
    @Column(name = "original_title_en", columnDefinition = "TEXT")
    private String originalTitleEn;
    
    @Column(name = "definition_en", columnDefinition = "TEXT")
    private String definitionEn;

    @Column(name = "long_definition_en", columnDefinition = "TEXT")
    private String longDefinitionEn;
    
    @Column(name = "exclusion_terms_en", columnDefinition = "TEXT")
    private String exclusionTermsEn;

    @Column(name = "class_kind", columnDefinition = "TEXT")
    private String classKind;
    
    // Self-referencing for hierarchical structure
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_uri")
    private ICD11Code parent;
    
    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<ICD11Code> children = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "last_synced")
    private LocalDateTime lastSynced;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ICD11Status status;
    
    // Relationships
    @OneToMany(mappedBy = "icdCode")
    @Builder.Default
    private List<ICD11Translation> translations = new ArrayList<>();
    
    @OneToMany(mappedBy = "icdCode")
    @Builder.Default
    private List<DietaryRestriction> dietaryRestrictions = new ArrayList<>();
    
    @OneToMany(mappedBy = "icdCode")
    @Builder.Default
    private List<MedicalSpecialty> medicalSpecialties = new ArrayList<>();
}
