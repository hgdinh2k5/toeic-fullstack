package org.example.toeicfullstack.entity.enums;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ADMIN,
    TEACHER,
    STUDENT;

    @Override
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}
