package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attempt_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttemptDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	private String selectedOption;

	private boolean isCorrect;

	private int timeToAnswer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "question_id", nullable = false)
	private Question question;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_attempt_id", nullable = false)
	private TestAttempt testAttempt;
}

