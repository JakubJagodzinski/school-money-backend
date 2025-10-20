package com.example.schoolmoney.auth;

import com.example.schoolmoney.auth.authtoken.AuthToken;
import com.example.schoolmoney.auth.authtoken.AuthTokenRepository;
import com.example.schoolmoney.common.constants.SecurityConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class LogoutService implements LogoutHandler {

    private final AuthTokenRepository authTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(SecurityConstants.HEADER_START)) {
            return;
        }

        final String jwt = authHeader.substring(SecurityConstants.HEADER_START.length());

        AuthToken storedAuthToken = authTokenRepository.findByAuthToken(jwt).orElse(null);

        if (storedAuthToken != null) {
            storedAuthToken.setRevoked(true);
            authTokenRepository.save(storedAuthToken);

            SecurityContextHolder.clearContext();
        }
    }

}
