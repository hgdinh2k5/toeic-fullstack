package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false)
    private String lessonTitle;

    private String videoUrl;

    private int orderIndex;

    private int duration;

    private Boolean isFreeTrial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private CourseChapter chapter;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<LessonProgress> progresses;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL)
    private List<Discussion> discussions;

}
