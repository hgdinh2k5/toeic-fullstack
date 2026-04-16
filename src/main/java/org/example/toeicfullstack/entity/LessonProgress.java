package org.example.toeicfullstack.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lesson_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private boolean isCompleted;           // đã hoàn thành chưa
    private int lastWatchedTime;           // giây đã xem đến (để resume)
    private LocalDateTime updatedAt;
    // Nhiều progress thuộc 1 student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Users student;
    // Nhiều progress thuộc 1 lesson
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
}
