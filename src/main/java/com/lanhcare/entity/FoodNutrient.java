package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "FoodNutrient")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodNutrient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutrient_id", nullable = false)
    private Nutrient nutrient;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;
}
