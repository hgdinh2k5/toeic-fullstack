package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepo extends JpaRepository<Lesson, String> {
}
