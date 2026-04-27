package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.CourseChapter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseChapterRepo extends JpaRepository<CourseChapter, String> {
}
