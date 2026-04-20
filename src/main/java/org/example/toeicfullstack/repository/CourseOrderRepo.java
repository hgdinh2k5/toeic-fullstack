package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.CourseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseOrderRepo extends JpaRepository<CourseOrder, String> {
}
