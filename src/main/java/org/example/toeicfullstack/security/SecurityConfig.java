package org.example.toeicfullstack.security;

import org.example.toeicfullstack.entity.UserPrincipal;
import org.example.toeicfullstack.entity.Users;
import org.example.toeicfullstack.service.CustomOAuth2UserService;
import org.example.toeicfullstack.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // TODO: Fill these redirect URLs later
    private static final String ADMIN_REDIRECT_URL = "";
    private static final String TEACHER_REDIRECT_URL = "";
    private static final String STUDENT_REDIRECT_URL = "/student-test";

    private final UserService usersService;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(UserService usersService, CustomOAuth2UserService customOAuth2UserService) {
        this.usersService = usersService;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public AuthenticationProvider authenicationProvider(UserDetailsService userDetailsService,
                                                        BCryptPasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Vô hiệu hóa CSRF (thường làm khi mới dev để tránh lỗi 403)
                .csrf(csrf -> csrf.disable())

                // 2. Cấu hình phân quyền truy cập
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/auth/register/**", "/css/**", "/js/**", "/images/**").permitAll() // Cho phép truy cập công khai
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/student/**", "/student-test").hasRole("STUDENT")
                        .anyRequest().authenticated() // Tất cả các request khác phải đăng nhập
                )

                // 3. Cấu hình đăng nhập bằng Form (Username/Password)
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(successHandler())
                        .failureHandler(formFailureHandler())
                        .permitAll()
                )

                // 4. Cấu hình đăng nhập OAuth2 (Google)
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService()))
                        .successHandler(oauth2UrlHandler())
                        .failureHandler(oAuth2FailureHandler())
                )

                // 5. Cấu hình Logout
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )

                // 6. Cho phép hiển thị Frame (cần nếu bạn dùng H2 console hoặc một số thư viện cũ)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler oauth2UrlHandler() {
        return (request, response, authentication) -> {

            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            String email = oAuth2User.getAttribute("email");
            if (email == null || email.isBlank()) {
                response.sendRedirect("/login?oauth_error=true");
                return;
            }

            Users user = usersService.getByEmail(email);

            if (user == null) {
                response.sendRedirect("/login?oauth_error=not_found");
                return;
            }

            UserPrincipal userPrincipal = new UserPrincipal(user);

            Authentication newAuth = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(newAuth);
            request.getSession().setMaxInactiveInterval(0);

            if (userPrincipal.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
                safeRedirect(response, ADMIN_REDIRECT_URL);
            } else if (userPrincipal.getAuthorities().stream().anyMatch(a -> "ROLE_TEACHER".equals(a.getAuthority()))) {
                safeRedirect(response, TEACHER_REDIRECT_URL);
            } else {
                safeRedirect(response, STUDENT_REDIRECT_URL);
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler oAuth2FailureHandler() {
        return (request, response, exception) -> {

            Throwable cause = exception.getCause();
            if (cause instanceof OAuth2RegistrationException) {
                if ("Tài khoản không tồn tại".equals(cause.getMessage())) {
                    response.sendRedirect("/login?oauth_error=not_found");
                    return;
                }
            }

            response.sendRedirect("/login?oauth_error=true");
        };
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities();

            if (authorities.stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
                safeRedirect(response, ADMIN_REDIRECT_URL);
            } else if (authorities.stream().anyMatch(a -> "ROLE_TEACHER".equals(a.getAuthority()))) {
                safeRedirect(response, TEACHER_REDIRECT_URL);
            } else {
                safeRedirect(response, STUDENT_REDIRECT_URL);
            }
        };
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        return customOAuth2UserService;
    }

    @Bean
    public AuthenticationFailureHandler formFailureHandler() {
        return (request, response, exception) -> response.sendRedirect("/login?error=true");
    }

    private void safeRedirect(jakarta.servlet.http.HttpServletResponse response, String targetUrl) throws IOException {
        if (targetUrl == null || targetUrl.isBlank()) {
            response.sendRedirect("/");
            return;
        }
        response.sendRedirect(targetUrl);
    }
}