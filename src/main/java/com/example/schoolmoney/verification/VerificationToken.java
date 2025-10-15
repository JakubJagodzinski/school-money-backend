package com.example.schoolmoney.verification;

import com.example.schoolmoney.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "verification_tokens")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "token_id")
    private UUID tokenId;

    @NotNull
    @Column(name = "token", nullable = false, updatable = false, unique = true)
    private String token;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_verification_tokens_user_id"))
    private User user;

    @NotNull
    @Column(name = "expiry_date", nullable = false, updatable = false)
    private LocalDateTime expiryDate;

    @NotNull
    @Column(name = "used", nullable = false)
    private boolean used;

}
