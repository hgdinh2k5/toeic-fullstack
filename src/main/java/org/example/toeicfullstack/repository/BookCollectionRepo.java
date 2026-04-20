package org.example.toeicfullstack.repository;

import org.example.toeicfullstack.entity.BookCollection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookCollectionRepo extends JpaRepository<BookCollection, String> {
}
