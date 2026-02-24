package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Tracks daily usage count of quota-limited features per user.
 * Used for: MEAL_LOG (Free: 1/day), EXERCISE_LOG (Free: 1/day), AI_CHAT (Free: 3/day, Basic: 10/day)
 */
@Entity
@Table(name = "feature_usage",
       uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "feature_code", "usage_date"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeatureUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Column(name = "feature_code", nullable = false, length = 50)
    private String featureCode;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Integer usageCount = 0;
}
