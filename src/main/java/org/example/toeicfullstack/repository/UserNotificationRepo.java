package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationRepo extends JpaRepository<UserNotification, String> {
}
