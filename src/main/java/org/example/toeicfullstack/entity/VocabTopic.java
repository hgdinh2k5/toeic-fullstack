package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.toeicfullstack.entity.enums.TopicType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vocab_topics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VocabTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String topicName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TopicType topicType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Users createdBy;

    @ManyToMany
    @JoinTable(
            name = "vocab_topic_items",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "vocabulary_id")
    )
    @Builder.Default
    private List<Vocabulary> vocabularies = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void validateOwnership() {
        if (topicType == TopicType.PERSONAL && createdBy == null) {
            throw new IllegalStateException("PERSONAL topic must have owner");
        }
        if (topicType == TopicType.SYSTEM && createdBy != null) {
            throw new IllegalStateException("SYSTEM topic cannot have owner");
        }
    }
}


