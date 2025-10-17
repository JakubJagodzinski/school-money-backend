package com.example.schoolmoney.domain.child;

import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "children")
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "child_id")
    private UUID childId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false, foreignKey = @ForeignKey(name = "fk_children_parent_id"))
    private Parent parent;

    @ManyToOne
    @JoinColumn(name = "school_class_id", foreignKey = @ForeignKey(name = "fk_children_school_class_id"))
    private SchoolClass schoolClass;

    @NotBlank
    @Size(min = 1, max = 30)
    @Column(name = "first_name", nullable = false, length = 30)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Size(max = 2048)
    @Column(name = "avatar_url", length = 2048)
    private String avatarUrl;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

}
