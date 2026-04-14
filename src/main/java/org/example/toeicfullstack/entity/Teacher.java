package org.example.toeicfullstack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("TEACHER")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class Teacher extends Users {

    @Column
    private Float commissionRate;
}
