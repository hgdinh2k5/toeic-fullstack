package org.example.toeicfullstack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("DICTATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DictationQuestion extends Question {

    private String audioUrl;

    @Column(columnDefinition = "TEXT")
    private String transcript;

    @Column(columnDefinition = "TEXT")
    private String translation;

    @Column(columnDefinition = "TEXT")
    private String hint;
}

