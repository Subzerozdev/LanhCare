package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ICD11Code")
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
    
    @Column(name = "exclusion_terms_en", columnDefinition = "TEXT")
    private String exclusionTermsEn;
    
    // Self-referencing for hierarchical structure
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_uri")
    private ICD11Code parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ICD11Code> children = new ArrayList<>();
    
    @Column(name = "is_residual_category")
    private Boolean isResidualCategory;
    
    @Column(name = "is_leaf")
    private Boolean isLeaf;
    
    @Column(name = "last_synced")
    private LocalDateTime lastSynced;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ICD11Status status;
    
    // Relationships
    @OneToMany(mappedBy = "icdCode", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ICD11Translation> translations = new ArrayList<>();
    
    @OneToMany(mappedBy = "icdCode", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DietaryRestriction> dietaryRestrictions = new ArrayList<>();
    
    @OneToMany(mappedBy = "icdCode", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MedicalSpecialty> medicalSpecialties = new ArrayList<>();
}
