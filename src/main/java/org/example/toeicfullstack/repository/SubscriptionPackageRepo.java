package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.SubscriptionPackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPackageRepo extends JpaRepository<SubscriptionPackage, String> {
}
