package com.example.schoolmoney.auth.authtoken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthTokenService {

    private final AuthTokenRepository authTokenRepository;

    public boolean isAccessTokenValid(String authToken) {
        log.debug("enter isAuthTokenValidInDatabase");

        AuthToken databaseAuthToken = authTokenRepository.findByAuthToken(authToken).orElse(null);

        if (databaseAuthToken == null) {
            return false;
        }

        boolean isAccessToken = databaseAuthToken.getAuthTokenType() == AuthTokenType.ACCESS;
        boolean isValid = isAccessToken && !databaseAuthToken.isRevoked();

        log.debug("exit isAuthTokenValidInDatabase, isValid={}", isValid);

        return isValid;
    }

}
