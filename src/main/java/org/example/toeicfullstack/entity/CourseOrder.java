package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.toeicfullstack.entity.enums.CourseOrderStatus;

import java.time.LocalDateTime;

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
    private String orderCode;              // mã đơn hàng (VD: ORD-20240416-001234)
    private LocalDateTime orderDate;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CourseOrderStatus status;      // PENDING | COMPLETED | FAILED | REFUNDED
    // Nhiều order thuộc 1 student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Users student;
    // Nhiều order thuộc 1 khóa học
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
