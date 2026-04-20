package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepo extends JpaRepository<Part, String> {
}
