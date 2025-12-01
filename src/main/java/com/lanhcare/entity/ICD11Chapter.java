package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ICD11Chapter")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ICD11Chapter {
    
    @Id
    @Column(name = "chapter_uri")
    private String chapterUri;
    
    @Column(name = "vn_title")
    private String vnTitle;
    
    @Column(name = "original_title_en")
    private String originalTitleEn;
    
    @Column(name = "chapter_code", length = 50)
    private String chapterCode;
    
    @Column(name = "release_id", length = 50)
    private String releaseId;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ICD11Status status;
    
    // Relationships
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ICD11Code> codes = new ArrayList<>();
}
