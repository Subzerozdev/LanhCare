package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ServicePlan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "period_value")
    private Integer periodValue;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "period_unit", length = 50)
    private PeriodUnit periodUnit;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    @Builder.Default
    private ServicePlanStatus status = ServicePlanStatus.ACTIVE;
    
    // Relationships
    @OneToMany(mappedBy = "servicePlan", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}
