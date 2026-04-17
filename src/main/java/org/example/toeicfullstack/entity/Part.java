package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private Integer partNumber;

    @Column(columnDefinition = "TEXT")
    private String instructionText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuestionGroup> questionGroups = new ArrayList<>();
}

