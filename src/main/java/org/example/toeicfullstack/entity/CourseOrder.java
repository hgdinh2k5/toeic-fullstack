package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.toeicfullstack.entity.enums.CourseOrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "course_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String orderCode;

    private LocalDateTime orderDate;

    private double finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CourseOrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Users student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;


    @OneToMany(mappedBy = "courseOrder", cascade = CascadeType.ALL)
    private List<Transaction> transactions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

}
