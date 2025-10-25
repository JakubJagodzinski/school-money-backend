package com.example.schoolmoney.usermoderation;

import com.example.schoolmoney.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_moderation_events")
public class UserModerationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id")
    private UUID eventId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "performed_by", nullable = false, updatable = false)
    private User performedBy;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // null if the action is permanent
    @Column(name = "until")
    private Instant until;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, updatable = false)
    private UserModerationAction action;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private UserModerationReason reason;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

}
