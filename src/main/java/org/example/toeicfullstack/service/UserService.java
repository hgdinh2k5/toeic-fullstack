package org.example.toeicfullstack.service;

import org.example.toeicfullstack.entity.UserPrincipal;
import org.example.toeicfullstack.dto.auth.SendOtpRequest;
import org.example.toeicfullstack.dto.auth.VerifyOtpRegisterRequest;
import org.example.toeicfullstack.entity.Users;
import org.example.toeicfullstack.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserService implements UserDetailsService {

	private static final int OTP_TTL_SECONDS = 180;
	private static final int RESEND_COOLDOWN_SECONDS = 30;

	@Autowired
	private UsersRepo usersRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private EmailService emailService;

	private final Map<String, PendingRegistration> pendingRegistrations = new ConcurrentHashMap<>();

	@Override
	public @NonNull UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
		Users user = usersRepo.findByEmail(email);

		if (user == null) {
			throw new UsernameNotFoundException("Tài khoản không tồn tại");
		}

		return new UserPrincipal(user);
	}

	public Users getByEmail(String email) {
		return usersRepo.findByEmail(email);
	}

	public boolean overLapByPassword(String password, String email) {
		Users user = usersRepo.findByEmail(email);

		if (user == null || user.getPassword() == null) {
			return false;
		}

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		return passwordEncoder.matches(password, user.getPassword());
	}

	public boolean existPassword(String password, String email) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		for (Users user : usersRepo.findAll()) {
			if (user.getPassword() != null
					&& passwordEncoder.matches(password, user.getPassword())
					&& !user.getEmail().equals(email)) {
				return true;
			}
		}
		return false;
	}

	public void updateUser(String email, String password) {
		Users user = usersRepo.findByEmail(email);

		if (user == null) {
			throw new AuthenticationCredentialsNotFoundException("Tài khoản không tồn tại");
		}

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		user.setPassword(passwordEncoder.encode(password));

		usersRepo.save(user);
	}

	public void sendOtpForRegistration(SendOtpRequest request) {
		String email = normalizeEmail(request.getEmail());

		if (usersRepo.existsByEmail(email)) {
			throw new IllegalArgumentException("Email is already registered");
		}

		Instant now = Instant.now();
		PendingRegistration existing = pendingRegistrations.get(email);
		if (existing != null && now.isBefore(existing.lastSentAt.plusSeconds(RESEND_COOLDOWN_SECONDS))) {
			throw new IllegalArgumentException("Please wait before requesting a new OTP");
		}

		String otp = generateOtp();
		PendingRegistration pending = new PendingRegistration(
				passwordEncoder.encode(request.getPassword()),
				request.getFullname(),
				request.getGender(),
				request.getPhone(),
				otp,
				now.plusSeconds(OTP_TTL_SECONDS),
				now
		);

		pendingRegistrations.put(email, pending);
		emailService.sendOtp(email, otp);
	}

	public void verifyOtpAndRegister(VerifyOtpRegisterRequest request) {
		String email = normalizeEmail(request.getEmail());

		if (usersRepo.existsByEmail(email)) {
			pendingRegistrations.remove(email);
			throw new IllegalArgumentException("Email is already registered");
		}

		PendingRegistration pending = pendingRegistrations.get(email);
		if (pending == null) {
			throw new IllegalArgumentException("OTP request not found for this email");
		}

		if (Instant.now().isAfter(pending.expiresAt)) {
			pendingRegistrations.remove(email);
			throw new IllegalArgumentException("OTP has expired");
		}

		if (!normalizeOtp(pending.otp).equals(normalizeOtp(request.getOtp()))) {
			throw new IllegalArgumentException("OTP is invalid");
		}

		Users user = new Users();
		user.setId(generateStudentId());
		user.setEmail(email);
		user.setPassword(pending.encodedPassword);
		user.setFullname(pending.fullname);
		user.setGender(pending.gender);
		user.setPhone(pending.phone);
		user.setAvatar("default-avatar.png");
		user.setRole("STUDENT");

		usersRepo.save(user);
		pendingRegistrations.remove(email);
	}

	private String generateStudentId() {
		long count = usersRepo.count();
		return String.format("ST%03d", count + 1);
	}

	private String normalizeEmail(String email) {
		return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
	}

	private String normalizeOtp(String otp) {
		return otp == null ? "" : otp.trim();
	}

	private String generateOtp() {
		return String.format("%06d", ThreadLocalRandom.current().nextInt(0, 1_000_000));
	}

	private static class PendingRegistration {
		private final String encodedPassword;
		private final String fullname;
		private final String gender;
		private final String phone;
		private final String otp;
		private final Instant expiresAt;
		private final Instant lastSentAt;

		private PendingRegistration(String encodedPassword,
									String fullname,
									String gender,
									String phone,
									String otp,
									Instant expiresAt,
									Instant lastSentAt) {
			this.encodedPassword = encodedPassword;
			this.fullname = fullname;
			this.gender = gender;
			this.phone = phone;
			this.otp = otp;
			this.expiresAt = expiresAt;
			this.lastSentAt = lastSentAt;
		}
	}
}
