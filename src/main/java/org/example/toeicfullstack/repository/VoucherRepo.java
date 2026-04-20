package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoucherRepo extends JpaRepository<Voucher, String> {
}
