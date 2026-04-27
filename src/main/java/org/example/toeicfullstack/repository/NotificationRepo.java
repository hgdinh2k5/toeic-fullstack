package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepo extends JpaRepository<Notification, String> {
}
