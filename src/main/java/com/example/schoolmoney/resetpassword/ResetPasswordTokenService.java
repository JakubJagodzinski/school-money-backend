package com.example.schoolmoney.resetpassword;

import com.example.schoolmoney.common.constants.messages.EmailMessages;
import com.example.schoolmoney.common.constants.messages.ResetPasswordTokenMessages;
import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.resetpassword.dto.request.CreateResetPasswordTokenRequestDto;
import com.example.schoolmoney.resetpassword.dto.request.ResetPasswordRequestDto;
import com.example.schoolmoney.user.User;
import com.example.schoolmoney.user.UserRepository;
import com.example.schoolmoney.utils.RandomBase64TokenGenerator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResetPasswordTokenService {

    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    private final UserRepository userRepository;

    private final ResetPasswordTokenProperties resetPasswordTokenProperties;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private void markExistingTokensAsUsed(User user) {
        List<ResetPasswordToken> tokens = resetPasswordTokenRepository.findAllByUser_UserIdAndUsedFalse(user.getUserId());

        for (ResetPasswordToken token : tokens) {
            token.setUsed(true);
        }

        resetPasswordTokenRepository.saveAll(tokens);
    }

    @Transactional
    public void createResetPasswordToken(CreateResetPasswordTokenRequestDto createResetPasswordTokenRequestDto) throws MailSendException {
        log.debug("Enter createResetPasswordToken for user {}", createResetPasswordTokenRequestDto.getEmail());

        User user = userRepository.findByEmail(createResetPasswordTokenRequestDto.getEmail())
                .orElse(null);

        if (user == null) {
            log.error("User with email {} not found", createResetPasswordTokenRequestDto.getEmail());
            return;
        }

        markExistingTokensAsUsed(user);

        ResetPasswordToken resetPasswordToken = ResetPasswordToken
                .builder()
                .user(user)
                .token(RandomBase64TokenGenerator.generate(resetPasswordTokenProperties.getTokenLength()))
                .expiryDate(Instant.now().plus(resetPasswordTokenProperties.getExpiryHours(), ChronoUnit.HOURS))
                .build();

        resetPasswordTokenRepository.save(resetPasswordToken);
        log.info("Reset password token saved {}", resetPasswordToken);

        String resetPasswordRedirectUrl = createResetPasswordTokenRequestDto.getRedirectUrl() + "?token=" + resetPasswordToken.getToken();

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), resetPasswordRedirectUrl);
            log.debug("Exit createResetPasswordToken");
        } catch (Exception e) {
            throw new MailSendException(EmailMessages.FAILED_TO_SEND_RESET_PASSWORD_EMAIL, e);
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) throws EntityNotFoundException, IllegalArgumentException {
        log.debug("Enter resetPassword");

        ResetPasswordToken resetPasswordToken = resetPasswordTokenRepository.findByToken(resetPasswordRequestDto.getResetPasswordToken())
                .orElseThrow(() -> {
                    log.error(ResetPasswordTokenMessages.RESET_PASSWORD_TOKEN_NOT_FOUND);
                    return new IllegalArgumentException(ResetPasswordTokenMessages.RESET_PASSWORD_TOKEN_NOT_FOUND);
                });

        if (resetPasswordToken.getExpiryDate().isBefore(Instant.now()) || resetPasswordToken.isUsed()) {
            throw new EntityNotFoundException(ResetPasswordTokenMessages.RESET_PASSWORD_TOKEN_NOT_FOUND);
        }

        resetPasswordToken.setUsed(true);
        resetPasswordTokenRepository.save(resetPasswordToken);

        User user = resetPasswordToken.getUser();

        user.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));
        userRepository.save(user);

        log.debug("Exit resetPassword");
    }

}
