package org.example.toeicfullstack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public abstract class Users {

	@Id
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

	@Column(name = "gender", length = 20)
	private String gender;

	@Column(name = "phone", length = 20)
	private String phone;

	public Users() {
	}

	public Users(String id, String password, String email, String avatar, String fullname, String gender, String phone) {
		this.id = id;
		this.password = password;
		this.email = email;
		this.avatar = avatar;
		this.fullname = fullname;
		this.gender = gender;
		this.phone = phone;
	}

	public Users(String password, String email, String avatar, String fullname, String gender, String phone) {
		this.password = password;
		this.email = email;
		this.avatar = avatar;
		this.fullname = fullname;
		this.gender = gender;
		this.phone = phone;
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
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@PrePersist
	protected void onCreate() {
		if (id == null || id.isBlank()) {
			id = UUID.randomUUID().toString();
		}
	}
}
