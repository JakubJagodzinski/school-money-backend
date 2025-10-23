package com.example.schoolmoney.domain.parent;

import com.example.schoolmoney.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "parents")
@PrimaryKeyJoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_parents_user_id"))
public class Parent extends User {

    @Column(name = "avatar_id")
    private UUID avatarId;

}
