package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "discussions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discussion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private int rating;                    // (nếu dùng discussion để review bài học)
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime createdAt;
    // Comment cha (null nếu là comment gốc)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_discussion_id")
    private Discussion parentDiscussion;
    // Danh sách reply
    @OneToMany(mappedBy = "parentDiscussion", cascade = CascadeType.ALL)
    private List<Discussion> replies;
    // Nhiều discussion thuộc 1 student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Users student;
    // Nhiều discussion thuộc 1 lesson
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    // Nhiều discussion thuộc 1 test
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id")
    private Test test;
}
