package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.toeicfullstack.entity.enums.PackageType;

import java.util.UUID;

@Entity
@Table(name = "subscription_package")
@Getter
@Setter
@NoArgsConstructor
public class SubscriptionPackage {
    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;
    @Column(name = "package_name", nullable = false, length = 100)
    private String packageName;
    @Enumerated(EnumType.STRING)
    @Column(name = "package_type", nullable = false)
    private PackageType packageType;
    @Column(name = "price", nullable = false)
    private Double price;
    @Column(name = "duration_in_days", nullable = false)
    private Integer durationInDays;
    @PrePersist
    protected void onCreate() {
        if (id == null || id.isBlank()) id = UUID.randomUUID().toString();
    }
}
