package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.toeicfullstack.entity.enums.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subscription_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String orderCode;

    private LocalDateTime purchaseDate;

    private LocalDateTime expireDate;

    @Column(nullable = false)
    private Double paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Users student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_package_id", nullable = false)
    private SubscriptionPackage subscriptionPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    @OneToMany(mappedBy = "subscriptionOrder", cascade = CascadeType.ALL)
    private List<Transaction> transactions;
    @OneToMany(mappedBy = "subscriptionOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}

