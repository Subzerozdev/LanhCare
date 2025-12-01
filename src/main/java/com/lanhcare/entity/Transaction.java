package com.lanhcare.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_plan_id", nullable = false)
    private ServicePlan servicePlan;
    
    @CreationTimestamp
    @Column(name = "transactionDate")
    private LocalDateTime transactionDate;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "paymentMethod", length = 50)
    private String paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private TransactionStatus status;
}
