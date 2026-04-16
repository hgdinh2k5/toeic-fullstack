package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {
    @Id
    private String id;                     // UUID tự sinh
    @Column(unique = true, nullable = false)
    private String certificateCode;        // mã chứng chỉ tra cứu (VD: CERT-20240416-001)
    private LocalDateTime issuedDate;
    private String contentUrl;             // URL file PDF/ảnh chứng chỉ từ Cloudinary
    // Nhiều certificate thuộc 1 student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Users student;
    // Nhiều certificate thuộc 1 khóa học
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
