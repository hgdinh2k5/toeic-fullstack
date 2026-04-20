package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepo extends JpaRepository<Enrollment, String> {
}
