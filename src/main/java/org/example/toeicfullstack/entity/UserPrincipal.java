package org.example.toeicfullstack.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final Users user;

    public UserPrincipal(Users user) {
        this.user = user;
    }

    public static UserPrincipal fromUser(Users user) {
        return new UserPrincipal(user);
    }

    public Users getUser() {
        return user;
    }

    public String getId() {
        return user.getId();
    }

    public String getFullname() {
        return user.getFullname();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getRole() {
        return resolveRole();
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public @NonNull String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    private String resolveRole() {
        if (user instanceof Admin) {
            return "ROLE_ADMIN";
        }
        if (user instanceof Teacher) {
            return "ROLE_TEACHER";
        }
        if (user instanceof Student) {
            return "ROLE_STUDENT";
        }
        return "ROLE_USER";
    }
}



