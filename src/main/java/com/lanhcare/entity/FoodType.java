package com.lanhcare.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "food_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "isDeleted")
    @Builder.Default
    private Boolean isDeleted = false;
    
    // Relationships
    @OneToMany(mappedBy = "foodType", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonIgnore
    private List<FoodItem> foodItems = new ArrayList<>();
}

