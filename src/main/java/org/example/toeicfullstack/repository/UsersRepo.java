package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepo extends JpaRepository<Users, String> {

	Users findByEmail(String email);

	boolean existsByEmail(String email);
}
