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
import com.example.schoolmoney.common.constants.messages.VerificationTokenMessages;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.wallet.WalletService;
import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.user.Role;
import com.example.schoolmoney.user.User;
import com.example.schoolmoney.user.UserRepository;
import com.example.schoolmoney.verification.*;
import jakarta.persistence.EntityNotFoundException;
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

    private final DomainProperties domainProperties;

    private final AuthTokenService authTokenService;

    private final VerificationTokenService verificationTokenService;

    private final VerificationTokenRepository verificationTokenRepository;

    private final EmailService emailService;

    private final VerificationLinkService verificationLinkService;

    @Transactional
    public void register(RegisterRequestDto registerRequestDto, Role role) throws IllegalArgumentException, AccessDeniedException {
        log.debug("Enter register");

        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            log.warn(UserMessages.EMAIL_IS_ALREADY_TAKEN);
            return;
        }

        if (!domainProperties.isEmailDomainAuthorized(registerRequestDto.getEmail())) {
            log.warn(UserMessages.UNAUTHORIZED_EMAIL_DOMAIN);
            throw new AccessDeniedException(UserMessages.UNAUTHORIZED_EMAIL_DOMAIN);
        }

        User user = createUser(role, registerRequestDto);

        postRegistrationActions(user);

        log.debug("Exit register");
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

        sendVerificationEmail(user.getEmail());
    }

    public void sendVerificationEmail(String email) {
        log.debug("Enter sendVerificationEmail for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            log.debug("User with email {} not found", email);
            return;
        }

        if (user.isVerified()) {
            log.debug("User {} is already verified", email);
            return;
        }

        String verificationToken = verificationTokenService.createVerificationToken(user, TokenType.ACCOUNT_VERIFICATION);
        String verificationLink = verificationLinkService.buildAccountVerificationLink(verificationToken);
        emailService.sendVerificationEmail(
                user.getEmail(),
                user.getFirstName(),
                verificationLink,
                user.isNotificationsEnabled()
        );

        log.debug("Exit sendVerificationEmail");
    }

    @Transactional
    public void verifyAccount(String token) {
        log.debug("Enter verifyAccount(token={})", token);

        VerificationToken verificationToken = verificationTokenService.validateToken(token);

        if (verificationToken.getTokenType() != TokenType.ACCOUNT_VERIFICATION) {
            log.warn(VerificationTokenMessages.VERIFICATION_TOKEN_NOT_FOUND);
            throw new EntityNotFoundException(VerificationTokenMessages.VERIFICATION_TOKEN_NOT_FOUND);
        }

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);
        log.info("User {} verified", user.getEmail());

        log.debug("Exit verifyAccount");
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
    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto requestDto) throws BadCredentialsException {
        String refreshToken = requestDto.getRefreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BadCredentialsException(TokenMessages.PROVIDED_REFRESH_TOKEN_IS_INVALID_OR_EXPIRED);
        }

        String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail == null) {
            throw new BadCredentialsException(TokenMessages.PROVIDED_REFRESH_TOKEN_IS_INVALID_OR_EXPIRED);
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadCredentialsException(TokenMessages.PROVIDED_REFRESH_TOKEN_IS_INVALID_OR_EXPIRED));

        AuthToken authToken = authTokenRepository.findByAuthToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException(TokenMessages.PROVIDED_REFRESH_TOKEN_IS_INVALID_OR_EXPIRED));

        if (authToken.getAuthTokenType() != AuthTokenType.REFRESH) {
            throw new BadCredentialsException(TokenMessages.PROVIDED_REFRESH_TOKEN_IS_INVALID_OR_EXPIRED);
        }

        if (!jwtService.isJwtValid(refreshToken, user) || authToken.isRevoked()) {
            throw new BadCredentialsException(TokenMessages.PROVIDED_REFRESH_TOKEN_IS_INVALID_OR_EXPIRED);
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
