package com.example.schoolmoney.verification;

import com.example.schoolmoney.common.constants.messages.EmailMessages;
import com.example.schoolmoney.common.constants.messages.UserMessages;
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
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException(VerificationTokenMessages.VERIFICATION_TOKEN_NOT_FOUND));

        if (verificationToken.isUsed()) {
            throw new IllegalArgumentException(VerificationTokenMessages.VERIFICATION_TOKEN_ALREADY_USED);
        }

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            throw new IllegalArgumentException(VerificationTokenMessages.VERIFICATION_TOKEN_EXPIRED);
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);
    }

    @Transactional
    public void sendVerificationEmail(String email) throws EntityNotFoundException, MailException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(UserMessages.USER_NOT_FOUND));

        if (user.isVerified()) {
            throw new IllegalArgumentException(UserMessages.ACCOUNT_ALREADY_VERIFIED);
        }

        String verificationToken = createVerificationToken(user);

        String verificationLink = verificationLinkService.buildLink(verificationToken);

        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getEmail(), verificationLink);
        } catch (Exception e) {
            throw new MailSendException(EmailMessages.FAILED_TO_SEND_VERIFICATION_EMAIL, e);
        }
    }

}
