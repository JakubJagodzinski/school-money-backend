package com.example.schoolmoney.auth.access;

import com.example.schoolmoney.user.Role;
import com.example.schoolmoney.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public UUID getCurrentUserId() {
        User user = getCurrentUser();

        if (user == null) {
            return null;
        }

        return user.getUserId();
    }

    public User getCurrentUser() {
        Authentication auth = getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            return null;
        }

        return (User) auth.getPrincipal();
    }

    public boolean isAdmin() {
        return hasRole(Role.ADMIN.name());
    }

    public boolean hasRole(String role) {
        Authentication auth = getAuthentication();

        if (auth == null) {
            return false;
        }

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    public boolean hasPermission(String authority) {
        Authentication auth = getAuthentication();

        if (auth == null) {
            return false;
        }

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }

}
