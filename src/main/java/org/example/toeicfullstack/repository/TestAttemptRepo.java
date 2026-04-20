package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestAttemptRepo extends JpaRepository<TestAttempt, String> {
}
