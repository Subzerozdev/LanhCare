package com.lanhcare.entity;

import com.lanhcare.enums.SpecialtyStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "medical_specialty")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalSpecialty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToMany(mappedBy = "medicalSpecialties")
    private List<Hospital> hospital;

    @ManyToMany(mappedBy = "medicalSpecialties")
    private List<ICD11Code> icdCode;

    @Column(name = "name_vn")
    private String nameVn;

    @Column(name = "name_en")
    private String nameEn;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SpecialtyStatus status;
}
