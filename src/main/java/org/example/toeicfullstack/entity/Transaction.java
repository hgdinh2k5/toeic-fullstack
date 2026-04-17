package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String transactionId;

    private String paymentGateway;

    private String bankCode;

    private Double amount;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String rawResponse;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_order_id")
    private SubscriptionOrder subscriptionOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_order_id")
    private CourseOrder courseOrder;
}

