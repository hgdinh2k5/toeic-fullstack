package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseReviewRepo extends JpaRepository<CourseReview, String> {
}
