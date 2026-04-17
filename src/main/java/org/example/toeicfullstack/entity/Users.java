package org.example.toeicfullstack.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.toeicfullstack.entity.enums.Gender;
import org.example.toeicfullstack.entity.enums.Role;

import java.time.LocalDateTime;
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

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt ;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
	private List<Certificate> certificates;

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
	private List<Discussion> discussions;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
	private List<CourseReview>courseReviews;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
	private List<Enrollment>enrollments;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
	private List<CourseOrder>courseOrders;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
	private List<LessonProgress>lessonProgresses;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
	private List<UserNotification>userNotifications;

	@OneToMany(mappedBy = "users", cascade = CascadeType.ALL)
	private List<Notification>notifications;



}
