package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.SubscriptionOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionOrderRepo extends JpaRepository<SubscriptionOrder, String> {
}
