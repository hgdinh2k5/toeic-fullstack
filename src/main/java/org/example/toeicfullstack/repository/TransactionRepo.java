package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepo extends JpaRepository<Transaction, String> {
}
