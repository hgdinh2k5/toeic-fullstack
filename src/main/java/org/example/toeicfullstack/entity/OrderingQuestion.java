package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("ORDERING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderingQuestion extends Question {

    @Column(columnDefinition = "TEXT")
    private String correctSequence;

    @ElementCollection
    @CollectionTable(name = "ordering_question_shuffled_words", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "word_order")
    @Column(name = "word_text", columnDefinition = "TEXT")
    private List<String> shuffledWords = new ArrayList<>();
}


