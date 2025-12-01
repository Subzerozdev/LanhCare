package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Nutrient")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Nutrient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 20)
    private String unit;
    
    // Relationships
    @OneToMany(mappedBy = "nutrient", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FoodNutrient> foodNutrients = new ArrayList<>();
    
    @OneToMany(mappedBy = "nutrient", cascade = CascadeType.ALL)
    @Builder.Default
    private List<DietaryRestriction> dietaryRestrictions = new ArrayList<>();
}
