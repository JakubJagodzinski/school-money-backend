package com.example.schoolmoney.auth;

import com.example.schoolmoney.auth.authtoken.AuthToken;
import com.example.schoolmoney.auth.authtoken.AuthTokenRepository;
import com.example.schoolmoney.auth.authtoken.AuthTokenService;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

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

    private final AuthTokenService authTokenService;

    @Transactional
    public void register(RegisterRequestDto registerRequestDto, Role role) throws IllegalArgumentException, AccessDeniedException {
        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new IllegalArgumentException(UserMessages.EMAIL_IS_ALREADY_TAKEN);
        }

        if (!domainProperties.isEmailDomainAuthorized(registerRequestDto.getEmail())) {
            log.warn(UserMessages.UNAUTHORIZED_EMAIL_DOMAIN);
            throw new AccessDeniedException(UserMessages.UNAUTHORIZED_EMAIL_DOMAIN);
        }

        User user = createUser(role, registerRequestDto);

        postRegistrationActions(user);
    }

    private void populateUserCommonFields(User user, Role role, RegisterRequestDto registerRequestDto) {
        user.setFirstName(registerRequestDto.getFirstName());
        user.setLastName(registerRequestDto.getLastName());
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setRole(role);
    }

    private User createUser(Role role, RegisterRequestDto dto) throws IllegalArgumentException {
        User user = switch (role) {
            case PARENT -> new Parent();
            case SCHOOL_ADMIN -> new User();
            default -> throw new IllegalArgumentException(UserMessages.INVALID_USER_ROLE);
        };

        populateUserCommonFields(user, role, dto);
        return userRepository.save(user);
    }

    private void postRegistrationActions(User user) {
        if (user instanceof Parent) {
            walletService.createWallet(user.getUserId());
        }

        verificationTokenService.sendVerificationEmail(user.getEmail());
    }

    @Transactional
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) throws BadCredentialsException {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        );

        authenticationManager.authenticate(authenticationToken);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException(UserMessages.WRONG_USERNAME_OR_PASSWORD));

        user.setLastLoggedIn(Instant.now());
        userRepository.save(user);

        authTokenService.revokeAllUserAuthTokens(user);

        String jwtToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        authTokenService.saveAuthToken(user, jwtToken, AuthTokenType.ACCESS);
        authTokenService.saveAuthToken(user, refreshToken, AuthTokenType.REFRESH);

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
    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto requestDto) throws IllegalArgumentException {
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

        if (!jwtService.isJwtValid(refreshToken, user) || authToken.isRevoked()) {
            throw new IllegalArgumentException(TokenMessages.PROVIDED_REFRESH_TOKEN_IS_INVALID_OR_EXPIRED);
        }

        authTokenService.revokeAllUserAuthTokens(user);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        authTokenService.saveAuthToken(user, newAccessToken, AuthTokenType.ACCESS);
        authTokenService.saveAuthToken(user, newRefreshToken, AuthTokenType.REFRESH);

        return RefreshTokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

}
