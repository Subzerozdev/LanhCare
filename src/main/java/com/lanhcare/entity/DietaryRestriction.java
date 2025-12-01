package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "DietaryRestriction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietaryRestriction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_health_profile_id", nullable = false)
    private UserHealthProfile userHealthProfile;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutrient_id")
    private Nutrient nutrient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "icd_uri")
    private ICD11Code icdCode;
    
    @Column(name = "name")
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "limit_type", length = 50)
    private LimitType limitType;
    
    @Column(name = "limit_value", precision = 10, scale = 2)
    private BigDecimal limitValue;
    
    @Column(name = "limit_unit", length = 50)
    private String limitUnit;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Frequency frequency;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private RestrictionStatus status;
    
    @Column(name = "source_of_advice", length = 100)
    private String sourceOfAdvice;
}
