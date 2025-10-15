package com.example.schoolmoney.auth.authtoken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthTokenService {

    private final AuthTokenRepository authTokenRepository;

    public boolean isAuthTokenValidInDatabase(String authToken) {
        log.debug("enter isAuthTokenValidInDatabase");

        AuthToken databaseAuthToken = authTokenRepository.findByAuthToken(authToken).orElse(null);

        if (databaseAuthToken == null) {
            return false;
        }

        boolean isValid = databaseAuthToken.getAuthTokenType() == AuthTokenType.ACCESS && !databaseAuthToken.isExpired() && !databaseAuthToken.isRevoked();

        log.debug("exit isAuthTokenValidInDatabase, isValid={}", isValid);

        return isValid;
    }

}
