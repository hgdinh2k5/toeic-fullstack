package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    private Boolean isFreeTrial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_collection_id")
    private BookCollection bookCollection;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "test_parts",
            joinColumns = @JoinColumn(name = "test_id"),
            inverseJoinColumns = @JoinColumn(name = "part_id")
    )
    @Builder.Default
    private List<Part> parts = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuestionGroup> questionGroups = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestAttempt> attempts = new ArrayList<>();

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Discussion> discussions = new ArrayList<>();
}

