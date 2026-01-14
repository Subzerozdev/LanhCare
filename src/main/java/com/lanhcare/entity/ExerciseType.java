package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "exercise_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "activity")
    private String activity;

    @Column(name = "examples")
    private String examples;

    @Column(name = "met_value")
    private BigDecimal metValue;

    @Column(name = "is_deleted")
    private boolean deleted = false;
}
