package com.example.schoolmoney.auth;

import com.example.schoolmoney.auth.authtoken.AuthToken;
import com.example.schoolmoney.auth.authtoken.AuthTokenRepository;
import com.example.schoolmoney.auth.authtoken.AuthTokenType;
import com.example.schoolmoney.auth.dto.request.AuthenticationRequestDto;
import com.example.schoolmoney.auth.dto.request.RefreshTokenRequestDto;
import com.example.schoolmoney.auth.dto.request.RegisterRequestDto;
import com.example.schoolmoney.auth.dto.response.AuthenticationResponseDto;
import com.example.schoolmoney.auth.dto.response.RefreshTokenResponseDto;
import com.example.schoolmoney.auth.jwt.JwtService;
import com.example.schoolmoney.common.constants.messages.TokenMessages;
import com.example.schoolmoney.common.constants.messages.UserMessages;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.wallet.WalletService;
import com.example.schoolmoney.user.Role;
import com.example.schoolmoney.user.User;
import com.example.schoolmoney.user.UserRepository;
import com.example.schoolmoney.verification.VerificationTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final AuthTokenRepository authTokenRepository;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final WalletService walletService;

    private final VerificationTokenService verificationTokenService;

    private final DomainProperties domainProperties;

    private AuthenticationResponseDto generateUserToken(User user) {
        String jwtToken = jwtService.generateToken(user);
        saveUserAuthToken(user, jwtToken, AuthTokenType.ACCESS);

        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserAuthToken(user, refreshToken, AuthTokenType.REFRESH);

        return AuthenticationResponseDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }

    @Transactional
    public void register(RegisterRequestDto registerRequestDto) throws IllegalArgumentException {
        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new IllegalArgumentException(UserMessages.EMAIL_IS_ALREADY_TAKEN);
        }

        if (!domainProperties.isEmailDomainAuthorized(registerRequestDto.getEmail())) {
            log.warn(UserMessages.UNAUTHORIZED_EMAIL_DOMAIN);
            throw new IllegalArgumentException(UserMessages.UNAUTHORIZED_EMAIL_DOMAIN);
        }

        Role userRole = Role.PARENT;
        User user = createUserInstance(userRole);

        populateCommonUserFields(user, registerRequestDto);

        User savedUser = userRepository.save(user);

        walletService.createWallet(savedUser.getUserId());

        verificationTokenService.sendVerificationEmail(savedUser.getEmail());
    }

    private Role parseRole(String role) throws IllegalArgumentException {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(UserMessages.INVALID_USER_ROLE);
        }
    }

    private User createUserInstance(Role role) throws IllegalArgumentException {
        if (role == Role.PARENT) {
            return new Parent();
        } else {
            throw new IllegalArgumentException(UserMessages.INVALID_USER_ROLE);
        }
    }

    private void populateCommonUserFields(User user, RegisterRequestDto dto) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.PARENT);
    }

    @Transactional
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) throws AccessDeniedException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            throw new AccessDeniedException(UserMessages.WRONG_USERNAME_OR_PASSWORD);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AccessDeniedException(UserMessages.WRONG_USERNAME_OR_PASSWORD));

        if (!user.isVerified()) {
            throw new AccessDeniedException(UserMessages.ACCOUNT_NOT_VERIFIED);
        }

        user.setLastLoggedIn(Instant.now());
        userRepository.save(user);

        revokeAllUserAuthTokens(user);

        return generateUserToken(user);
    }

    private void saveUserAuthToken(User user, String tokenValue, AuthTokenType authTokenType) {
        AuthToken authToken = AuthToken
                .builder()
                .authToken(tokenValue)
                .authTokenType(authTokenType)
                .user(user)
                .build();

        authTokenRepository.save(authToken);
    }

    private void revokeAllUserAuthTokens(User user) {
        List<AuthToken> validUserAuthTokens = authTokenRepository.findAllByUser_UserIdAndIsRevokedFalse(user.getUserId());

        if (validUserAuthTokens.isEmpty()) {
            return;
        }

        validUserAuthTokens.forEach(
                authToken -> {
                    authToken.setRevoked(true);
                }
        );

        authTokenRepository.saveAll(validUserAuthTokens);
    }

    @Transactional
    public RefreshTokenResponseDto refreshAuthToken(RefreshTokenRequestDto requestDto) throws IllegalArgumentException {
        String refreshToken = requestDto.getRefreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException(TokenMessages.PROVIDED_REFRESH_TOKEN_IS_INVALID_OR_EXPIRED);
        }

        String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new IllegalArgumentException(TokenMessages.PROVIDED_REFRESH_TOKEN_IS_INVALID_OR_EXPIRED);
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException(TokenMessages.PROVIDED_REFRESH_TOKEN_IS_INVALID_OR_EXPIRED));

        AuthToken authToken = authTokenRepository.findByAuthToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException(TokenMessages.PROVIDED_REFRESH_TOKEN_IS_INVALID_OR_EXPIRED));

        if (authToken.getAuthTokenType() != AuthTokenType.REFRESH) {
            throw new IllegalArgumentException(TokenMessages.PROVIDED_REFRESH_TOKEN_IS_INVALID_OR_EXPIRED);
        }

        if (!jwtService.isTokenValid(refreshToken, user) || authToken.isRevoked()) {
            throw new IllegalArgumentException(TokenMessages.PROVIDED_REFRESH_TOKEN_IS_INVALID_OR_EXPIRED);
        }

        revokeAllUserAuthTokens(user);

        String newAccessToken = jwtService.generateToken(user);
        saveUserAuthToken(user, newAccessToken, AuthTokenType.ACCESS);

        String newRefreshToken = jwtService.generateRefreshToken(user);
        saveUserAuthToken(user, newRefreshToken, AuthTokenType.REFRESH);

        return RefreshTokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

}
