package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.VocabTopic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VocabTopicRepo extends JpaRepository<VocabTopic, String> {
}
