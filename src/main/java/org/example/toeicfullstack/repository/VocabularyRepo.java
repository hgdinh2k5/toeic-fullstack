package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.Vocabulary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabularyRepo extends JpaRepository<Vocabulary, String> {
}
