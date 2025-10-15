package com.example.schoolmoney.auth.authtoken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, UUID> {

    List<AuthToken> findAllByUser_UserIdAndIsExpiredFalseOrIsRevokedFalse(UUID userId);

    Optional<AuthToken> findByAuthToken(String authToken);

}
