package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "UserHealthProfile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserHealthProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;
    
    @Column(name = "height_cm", precision = 5, scale = 2)
    private BigDecimal heightCm;
    
    @Column(name = "weight_kg", precision = 5, scale = 2)
    private BigDecimal weightKg;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_level", length = 50)
    private ActivityLevel activityLevel;
    
    @Column(name = "bmi_value", precision = 5, scale = 2)
    private BigDecimal bmiValue;
    
    @Column(name = "health_goals", columnDefinition = "TEXT")
    private String healthGoals;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "userHealthProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DietaryRestriction> dietaryRestrictions = new ArrayList<>();
}
