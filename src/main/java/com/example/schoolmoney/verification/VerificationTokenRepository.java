package com.example.schoolmoney.verification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    Optional<VerificationToken> findByToken(String token);

    List<VerificationToken> findAllByUser_UserIdAndTokenTypeAndUsedFalse(UUID userId, TokenType tokenType);

}
