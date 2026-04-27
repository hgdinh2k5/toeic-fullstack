package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepo extends JpaRepository<Certificate, String> {
}
