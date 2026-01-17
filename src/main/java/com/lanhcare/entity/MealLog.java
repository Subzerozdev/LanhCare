package com.lanhcare.entity;

import com.lanhcare.enums.MealType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_log_entry_id", nullable = false)
    private DailyLog dailyLog;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", length = 50)
    private MealType mealType;
    
    @Column(name = "logged_time")
    private LocalTime loggedTime;
    
    @Column(name = "total_calories", precision = 10, scale = 2)
    private BigDecimal totalCalories;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "mealLog", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MealFood> mealFoods = new ArrayList<>();

    public void calculateTotalCalories() {
        totalCalories = mealFoods.stream()
                .map(MealFood::getCalories)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
