package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.QuestionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionGroupRepo extends JpaRepository<QuestionGroup, String> {
}
