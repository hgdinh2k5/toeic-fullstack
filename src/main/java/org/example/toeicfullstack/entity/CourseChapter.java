package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "course_chapters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseChapter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String chapterName;

    private String stageName;

    private Boolean isFreeTrial;

    private int orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons;
}
