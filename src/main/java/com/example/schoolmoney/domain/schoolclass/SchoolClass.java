package com.example.schoolmoney.domain.schoolclass;

import com.example.schoolmoney.domain.parent.Parent;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "school_classes")
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "school_class_id")
    private UUID schoolClassId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "treasurer_id", nullable = false, foreignKey = @ForeignKey(name = "fk_school_classes_treasurer_id"))
    private Parent treasurer;

    @NotBlank
    @Size(min = 12, max = 12)
    @Column(name = "invitation_code", nullable = false, unique = true, length = 12)
    private String invitationCode;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(name = "school_class_name", nullable = false, length = 50)
    private String schoolClassName;

    @NotBlank
    @Size(min = 1, max = 30)
    @Column(name = "school_class_year", nullable = false, length = 30)
    private String schoolClassYear;

    @NotNull
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public String getFullName() {
        return schoolClassName + " " + schoolClassYear;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

}
