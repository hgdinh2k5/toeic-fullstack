package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.toeicfullstack.entity.enums.CourseOrderStatus;
import org.example.toeicfullstack.entity.enums.DiscountType;
import org.example.toeicfullstack.entity.enums.VoucherStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double discountValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DiscountType discountType = DiscountType.PERCENTAGE;

    @Column(nullable = false)
    private Integer maxUsage;

    @Column(nullable = false)
    @Builder.Default
    private Integer usedCount = 0;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private VoucherStatus status = VoucherStatus.ACTIVE;

    @Column(length = 20)
    private DiscountType discountType;



    @ManyToMany
    @JoinTable(
            name = "voucher_courses",
            joinColumns = @JoinColumn(name = "voucher_code", referencedColumnName = "code"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    @Builder.Default
    private List<Course> courses = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "voucher_subscription_packages",
            joinColumns = @JoinColumn(name = "voucher_code", referencedColumnName = "code"),
            inverseJoinColumns = @JoinColumn(name = "subscription_package_id")
    )
    @Builder.Default
    private List<SubscriptionPackage> subscriptionPackages = new ArrayList<>();
    private List<Course> courses;

    @OneToMany(mappedBy = "voucher", fetch = FetchType.LAZY)
    @Builder.Default
    private List<SubscriptionOrder> subscriptionOrders = new ArrayList<>();
    @OneToMany(mappedBy = "voucher")
    private List<CourseOrder> orders;

    @OneToMany(mappedBy = "voucher", fetch = FetchType.LAZY)
    @Builder.Default
    private List<CourseOrder> courseOrders = new ArrayList<>();
}



