package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.toeicfullstack.entity.enums.PackageType;

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
    @Column(length = 30)
    private PackageType packageType;

    private double price;

    private int durationInDays;             // thời hạn gói (ngày)

    @OneToMany(mappedBy = "subscriptionPackage", cascade = CascadeType.ALL)
    private List<SubscriptionOrder> orders;
}
