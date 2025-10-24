package com.example.schoolmoney.auth.authtoken;

import com.example.schoolmoney.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthTokenService {

    private final AuthTokenRepository authTokenRepository;

    public boolean isActiveAccessToken(String authToken) {
        log.debug("Enter isValidAccessToken");

        AuthToken databaseAuthToken = authTokenRepository.findByAuthToken(authToken).orElse(null);

        if (databaseAuthToken == null) {
            return false;
        }

        boolean isAccessToken = databaseAuthToken.getAuthTokenType() == AuthTokenType.ACCESS;
        boolean isValid = isAccessToken && !databaseAuthToken.isRevoked();

        log.debug("isAccessToken: {}, isValid: {}", isAccessToken, isValid);

        log.debug("Exit isValidAccessToken");
        return isValid;
    }

    @Transactional
    public void saveAuthToken(User user, String tokenValue, AuthTokenType authTokenType) {
        AuthToken authToken = AuthToken
                .builder()
                .authToken(tokenValue)
                .authTokenType(authTokenType)
                .user(user)
                .build();

        authTokenRepository.save(authToken);
    }

    @Transactional
    public void revokeAllUserAuthTokens(User user) {
        List<AuthToken> validUserAuthTokens = authTokenRepository.findAllByUser_UserIdAndIsRevokedFalse(user.getUserId());

        validUserAuthTokens.forEach(authToken -> authToken.setRevoked(true));

        authTokenRepository.saveAll(validUserAuthTokens);
    }

}
