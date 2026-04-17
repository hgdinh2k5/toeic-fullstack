package org.example.toeicfullstack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import org.example.toeicfullstack.entity.enums.Gender;
import org.example.toeicfullstack.entity.enums.Role;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", length = 36, nullable = false, updatable = false)
	private String id;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "email", nullable = false, unique = true, length = 150)
	private String email;

	@Column(name = "avatar")
	private String avatar;

	@Column(name = "fullname", length = 150)
	private String fullname;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender", length = 20)
	private Gender gender;

	@Column(name = "phone", length = 20)
	private String phone;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private Role role = Role.STUDENT;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	@Builder.Default
	private List<Vocabulary> vocabularies = new ArrayList<>();

	@OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
	@Builder.Default
	private List<VocabTopic> vocabTopicsCreated = new ArrayList<>();

	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
	@Builder.Default
	private List<TestAttempt> testAttempts = new ArrayList<>();

	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
	@Builder.Default
	private List<SubscriptionOrder> subscriptionOrders = new ArrayList<>();

	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
	@Builder.Default
	private List<CourseOrder> courseOrders = new ArrayList<>();
}
