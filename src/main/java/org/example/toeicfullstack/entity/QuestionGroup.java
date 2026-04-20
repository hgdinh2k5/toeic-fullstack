package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "question_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(columnDefinition = "TEXT")
	private String passageText;

	@Column(columnDefinition = "TEXT")
	private String audioUrl;

	@Column(columnDefinition = "TEXT")
	private String imageUrl;

	@Column(columnDefinition = "TEXT")
	private String transcript;

	@Column(columnDefinition = "TEXT")
	private String translation;

	private Integer groupOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "test_id", nullable = false)
	private Test test;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "part_id")
	private Part part;

	@OneToMany(mappedBy = "questionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Question> questions = new ArrayList<>();
}
