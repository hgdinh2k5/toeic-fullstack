package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.toeicfullstack.entity.enums.OrderStatus;

import java.time.LocalDate;
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

    private LocalDate purchaseDate;

    private LocalDate expireDate;

    private double paidAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private SubscriptionPackage subscriptionPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @OneToMany(mappedBy = "subscriptionOrder", cascade = CascadeType.ALL)
    private List<Transaction> transactions;
}
