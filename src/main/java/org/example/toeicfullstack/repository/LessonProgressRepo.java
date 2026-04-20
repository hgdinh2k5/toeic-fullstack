package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonProgressRepo extends JpaRepository<LessonProgress, String> {
}
