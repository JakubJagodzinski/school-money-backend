package com.example.schoolmoney.verification;

import com.example.schoolmoney.common.constants.messages.EmailMessages;
import com.example.schoolmoney.common.constants.messages.VerificationTokenMessages;
import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.user.User;
import com.example.schoolmoney.user.UserRepository;
import com.example.schoolmoney.utils.RandomBase64TokenGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final VerificationTokenProperties verificationTokenProperties;

    private final VerificationLinkService verificationLinkService;

    private void markExistingTokensAsUsed(User user) {
        List<VerificationToken> tokens = verificationTokenRepository.findAllByUser_UserIdAndUsedFalse(user.getUserId());

        for (VerificationToken token : tokens) {
            token.setUsed(true);
        }

        verificationTokenRepository.saveAll(tokens);
    }

    @Transactional
    public String createVerificationToken(User user) {
        markExistingTokensAsUsed(user);

        VerificationToken token = VerificationToken
                .builder()
                .user(user)
                .token(RandomBase64TokenGenerator.generate(verificationTokenProperties.getTokenLength()))
                .expiryDate(Instant.now().plus(verificationTokenProperties.getExpiryHours(), ChronoUnit.HOURS))
                .build();

        VerificationToken savedToken = verificationTokenRepository.save(token);

        return savedToken.getToken();
    }

    @Transactional
    public void verifyUser(String token) throws EntityNotFoundException, IllegalArgumentException {
        log.debug("Enter verifyUser for token: {}", token);

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    log.warn(VerificationTokenMessages.VERIFICATION_TOKEN_NOT_FOUND);
                    return new EntityNotFoundException(VerificationTokenMessages.VERIFICATION_TOKEN_NOT_FOUND);
                });

        if (verificationToken.getExpiryDate().isBefore(Instant.now()) || verificationToken.isUsed()) {
            log.warn(VerificationTokenMessages.VERIFICATION_TOKEN_NOT_FOUND);
            throw new EntityNotFoundException(VerificationTokenMessages.VERIFICATION_TOKEN_NOT_FOUND);
        }

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        log.debug("Exit verifyUser");
    }

    @Transactional
    public void sendVerificationEmail(String email) throws MailException {
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

        String verificationToken = createVerificationToken(user);

        String verificationLink = verificationLinkService.buildLink(verificationToken);

        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getFirstName(), verificationLink);
        } catch (Exception e) {
            throw new MailSendException(EmailMessages.FAILED_TO_SEND_VERIFICATION_EMAIL, e);
        }
    }

}
