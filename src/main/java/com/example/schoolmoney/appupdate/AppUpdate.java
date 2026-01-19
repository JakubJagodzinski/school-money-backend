package com.example.schoolmoney.appupdate;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_updates")
public class AppUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "app_update_id")
    private UUID id;

    @Column(name = "version", nullable = false, updatable = false, unique = true)
    private String version;

    @Column(name = "changelog", updatable = false)
    private List<String> changelog;

    @Column(name = "updated_at", nullable = false, updatable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = Instant.now();
    }

}
