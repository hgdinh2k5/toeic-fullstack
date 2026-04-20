package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepo extends JpaRepository<Test, String> {
}
