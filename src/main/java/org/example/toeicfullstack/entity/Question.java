package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.toeicfullstack.entity.enums.SkillType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer score;

    @Column(columnDefinition = "TEXT")
    private String explanationText;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private SkillType skillType;

    @Column(name = "question_number", nullable = false)
    private Integer questionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_group_id", nullable = false)
    private QuestionGroup questionGroup;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    private List<AttemptDetail> attemptDetails = new ArrayList<>();
}

