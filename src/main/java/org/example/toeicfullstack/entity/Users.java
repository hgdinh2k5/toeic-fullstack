package org.example.toeicfullstack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class Users {

	@Id
	@Column(name = "id", length = 10, nullable = false, updatable = false)
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

	@Column(name = "role", nullable = false, length = 30)
	private String role;

	public Users() {
	}

	public Users(String id, String password, String email, String avatar, String fullname, Gender gender, String phone, String role) {
		this.id = id;
		this.password = password;
		this.email = email;
		this.avatar = avatar;
		this.fullname = fullname;
		this.gender = gender;
		this.phone = phone;
		this.role = role;
	}

	public Users(String password, String email, String avatar, String fullname, Gender gender, String phone, String role) {
		this.password = password;
		this.email = email;
		this.avatar = avatar;
		this.fullname = fullname;
		this.gender = gender;
		this.phone = phone;
		this.role = role;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getGender() {
		return gender != null ? gender.getDisplayName() : null;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public void setGender(String genderStr) {
		this.gender = genderStr != null ? Gender.fromDisplayName(genderStr) : null;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
