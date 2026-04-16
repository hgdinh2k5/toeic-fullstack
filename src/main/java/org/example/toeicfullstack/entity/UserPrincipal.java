package org.example.toeicfullstack.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.jspecify.annotations.NonNull;
import org.example.toeicfullstack.entity.enums.Role;

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
        return resolveRole().getAuthority();
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(resolveRole());
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

    private Role resolveRole() {
        return user.getRole() == null ? Role.STUDENT : user.getRole();
    }
}



