package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.AttemptDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttemptDetailRepo extends JpaRepository<AttemptDetail, String> {
}
