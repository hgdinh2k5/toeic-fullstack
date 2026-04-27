package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCategoryRepo extends JpaRepository<CourseCategory, String> {
}
