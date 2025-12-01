package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MedicalSpecialty")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalSpecialty {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icd_uri")
    private ICD11Code icdCode;
    
    @Column(name = "name_vn")
    private String nameVn;
    
    @Column(name = "name_en")
    private String nameEn;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SpecialtyStatus status;
}
