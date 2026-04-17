package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "test_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestAttempt {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	private LocalDateTime createdAt;

	private Integer totalScore;
	private Integer listeningScore;
	private Integer readingScore;
	private Integer correctListeningCount;
	private Integer correctReadingCount;
	private Integer timeSpent;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id", nullable = false)
	private Users student;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id", nullable = false)
	private Test test;

	@OneToMany(mappedBy = "testAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<AttemptDetail> attemptDetails = new ArrayList<>();
}

