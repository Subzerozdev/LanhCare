package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String email;

    @Column(length = 500)
    private String reason;

    private String verificationCode;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    public enum RequestStatus {
        PENDING,
        VERIFIED,
        COMPLETED,
        CANCELLED
    }
}
