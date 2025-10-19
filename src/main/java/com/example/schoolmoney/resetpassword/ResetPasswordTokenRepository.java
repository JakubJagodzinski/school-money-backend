package com.example.schoolmoney.resetpassword;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, UUID> {

    Optional<ResetPasswordToken> findByToken(String token);

    List<ResetPasswordToken> findAllByUser_UserIdAndUsedFalse(UUID userId);

}
