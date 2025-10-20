package com.example.schoolmoney.auth.authtoken;

import com.example.schoolmoney.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auth_tokens")
public class AuthToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "auth_token_id")
    private UUID authTokenId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_auth_tokens_user_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @NotNull
    @Column(name = "auth_token", nullable = false, updatable = false, unique = true)
    private String authToken;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "auth_token_type", nullable = false, updatable = false)
    private AuthTokenType authTokenType;

    @NotNull
    @Column(name = "is_revoked", nullable = false)
    private boolean isRevoked;

}
