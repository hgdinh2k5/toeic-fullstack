package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "book_collections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    private Integer yearPublished;

    @OneToMany(mappedBy = "bookCollection", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Test> tests = new ArrayList<>();
}

