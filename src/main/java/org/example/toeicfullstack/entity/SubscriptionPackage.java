package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.toeicfullstack.entity.enums.PackageType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "subscription_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String packageName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PackageType packageType;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer durationInDays;

    @OneToMany(mappedBy = "subscriptionPackage", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubscriptionOrder> subscriptionOrders = new ArrayList<>();

    @ManyToMany(mappedBy = "subscriptionPackages")
    @Builder.Default
    private List<Voucher> vouchers = new ArrayList<>();
}
