package com.example.schoolmoney.user;

import com.example.schoolmoney.auth.DomainProperties;
import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.PasswordMessages;
import com.example.schoolmoney.common.constants.messages.UserMessages;
import com.example.schoolmoney.common.constants.messages.VerificationTokenMessages;
import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.user.dto.request.ChangePasswordRequestDto;
import com.example.schoolmoney.verification.TokenType;
import com.example.schoolmoney.verification.VerificationLinkService;
import com.example.schoolmoney.verification.VerificationToken;
import com.example.schoolmoney.verification.VerificationTokenService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityUtils securityUtils;

    private final EmailService emailService;

    private final VerificationTokenService verificationTokenService;

    private final VerificationLinkService verificationLinkService;

    private final DomainProperties domainProperties;

    @Transactional
    public void changePassword(ChangePasswordRequestDto changePasswordRequestDto) throws BadCredentialsException, IllegalArgumentException {
        log.debug("Enter changePassword");

        User user = securityUtils.getCurrentUser();

        if (!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException(PasswordMessages.WRONG_PASSWORD);
        }

        if (!changePasswordRequestDto.getNewPassword().equals(changePasswordRequestDto.getConfirmationPassword())) {
            throw new IllegalArgumentException(PasswordMessages.PASSWORDS_DONT_MATCH);
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getNewPassword()));
        userRepository.save(user);
        log.debug("Password changed for user {}", user.getEmail());

        log.debug("Exit changePassword");
    }

    @Transactional
    public void unblockUsersWithExpiredBlock() {
        log.debug("Enter unblockUsersWithExpiredBlock");

        List<User> usersWithExpiredBlock = userRepository.findAllByIsBlockedTrueAndBlockedUntilIsNotNullAndBlockedUntilBefore(Instant.now());

        for (User user : usersWithExpiredBlock) {
            user.setBlocked(false);
            user.setBlockedUntil(null);
            userRepository.save(user);

            log.info("User {} unblocked", user.getEmail());

            emailService.sendAccountBlockExpiredEmail(
                    user.getEmail(),
                    user.getFirstName()
            );
        }

        log.debug("Exit unblockUsersWithExpiredBlock");
    }

    @Transactional
    public void requestEmailChange(String newEmail) throws EntityExistsException, AccessDeniedException {
        log.debug("Enter requestEmailChange");

        if (userRepository.findByEmail(newEmail).isPresent()) {
            log.warn(UserMessages.EMAIL_IS_ALREADY_TAKEN);
            throw new EntityExistsException(UserMessages.EMAIL_IS_ALREADY_TAKEN);
        }

        if (!domainProperties.isEmailDomainAuthorized(newEmail)) {
            log.warn(UserMessages.UNAUTHORIZED_EMAIL_DOMAIN);
            throw new AccessDeniedException(UserMessages.UNAUTHORIZED_EMAIL_DOMAIN);
        }

        User user = securityUtils.getCurrentUser();

        String verificationToken = verificationTokenService.createVerificationToken(user, TokenType.EMAIL_CHANGE);

        String verificationLink = verificationLinkService.buildChangeEmailConfirmationLink(verificationToken);

        user.setPendingNewEmail(newEmail);
        userRepository.save(user);
        log.info("New email ({}) requested for user {}", user.getPendingNewEmail(), user.getEmail());

        emailService.sendNewEmailConfirmationEmail(
                newEmail,
                user.getFirstName(),
                verificationLink
        );

        log.debug("Exit requestEmailChange");
    }

    @Transactional
    public void confirmEmailChange(String token) throws EntityNotFoundException {
        log.debug("Enter confirmEmailChange");

        VerificationToken verificationToken = verificationTokenService.validateToken(token);

        if (verificationToken.getTokenType() != TokenType.EMAIL_CHANGE) {
            log.warn(VerificationTokenMessages.VERIFICATION_TOKEN_NOT_FOUND);
            throw new EntityNotFoundException(VerificationTokenMessages.VERIFICATION_TOKEN_NOT_FOUND);
        }

        User user = verificationToken.getUser();

        user.setEmail(user.getPendingNewEmail());
        user.setPendingNewEmail(null);
        userRepository.save(user);

        log.debug("Exit confirmEmailChange");
    }

}
