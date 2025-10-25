package com.example.schoolmoney.usermoderation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserModerationEventRepository extends JpaRepository<UserModerationEvent, UUID> {
}
