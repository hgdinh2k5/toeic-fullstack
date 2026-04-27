package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepo extends JpaRepository<Course, String> {
}
