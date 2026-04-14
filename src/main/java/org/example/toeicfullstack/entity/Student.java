package org.example.toeicfullstack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("STUDENT")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Student extends Users {

    @Column
    private Integer targetScore;
}
