package com.example.schoolmoney.domain.parent;

import com.example.schoolmoney.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parents")
@PrimaryKeyJoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_parents_user_id"))
public class Parent extends User {

    @Size(max = 2_048)
    @Column(name = "avatar_url", length = 2_048)
    private String avatarUrl;

}
