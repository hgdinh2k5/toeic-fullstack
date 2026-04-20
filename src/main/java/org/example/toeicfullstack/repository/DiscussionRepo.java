package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscussionRepo extends JpaRepository<Discussion, String> {
}
