package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "FoodItem")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_type_id")
    private FoodType foodType;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal calo;
    
    @Column(name = "serving_unit", length = 50)
    private String servingUnit;
    
    @Column(name = "standard_serving_size", precision = 10, scale = 2)
    private BigDecimal standardServingSize;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private FoodItemStatus status;
    
    @Column(name = "data_source", length = 100)
    private String dataSource;
    
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;
    
    // Relationships
    @OneToMany(mappedBy = "foodItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FoodNutrient> foodNutrients = new ArrayList<>();
    
    @OneToMany(mappedBy = "foodItem", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MealLog> mealLogs = new ArrayList<>();
}
