package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepo extends JpaRepository<Question, String> {
}
