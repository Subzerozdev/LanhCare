package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ICD11Translation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ICD11Translation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icd_uri", nullable = false)
    private ICD11Code icdCode;
    
    @Column(name = "vn_title", columnDefinition = "TEXT")
    private String vnTitle;
    
    @Column(name = "vn_definition", columnDefinition = "TEXT")
    private String vnDefinition;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", length = 50)
    private ReviewStatus reviewStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private TranslationStatus status;
}
