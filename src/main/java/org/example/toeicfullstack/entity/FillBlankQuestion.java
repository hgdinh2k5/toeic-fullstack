package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("FILL_BLANK")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FillBlankQuestion extends Question {

    @Column(columnDefinition = "TEXT")
    private String correctText;

    @Column(columnDefinition = "TEXT")
    private String audioUrl;

    @Column(columnDefinition = "TEXT")
    private String hint;
}

