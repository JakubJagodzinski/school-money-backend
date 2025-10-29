package com.example.schoolmoney.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @NotBlank
    @Size(min = 1, max = 30)
    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    // based on RFC 5321 the maximum length of the email address is 254 characters
    @Email
    @NotBlank
    @Size(min = 5, max = 254)
    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @Email
    @Size(max = 254)
    @Column(name = "pendingNewEmail", length = 254)
    private String pendingNewEmail;

    @NotBlank
    @Size(min = 12, max = 128)
    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_logged_in")
    private Instant lastLoggedIn;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @NotNull
    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @NotNull
    @Column(name = "is_blocked", nullable = false)
    private boolean isBlocked;

    @Column(name = "blocked_until")
    private Instant blockedUntil;

    @NotNull
    @Column(name = "notifications_enabled", nullable = false)
    private boolean notificationsEnabled;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.isVerified = false;
        this.isBlocked = false;
        this.notificationsEnabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (!isBlocked) {
            return true;
        }

        // blocked permanently
        if (blockedUntil == null) {
            return false;
        }

        return Instant.now().isAfter(blockedUntil);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isVerified;
    }

}
