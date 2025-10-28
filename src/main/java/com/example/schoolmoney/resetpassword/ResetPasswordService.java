package com.example.schoolmoney.resetpassword;

import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.resetpassword.dto.request.RequestPasswordResetRequestDto;
import com.example.schoolmoney.resetpassword.dto.request.ResetPasswordRequestDto;
import com.example.schoolmoney.user.User;
import com.example.schoolmoney.user.UserRepository;
import com.example.schoolmoney.verification.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResetPasswordService {

    private final UserRepository userRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenService verificationTokenService;

    private final VerificationLinkService verificationLinkService;

    private final VerificationTokenRepository verificationTokenRepository;

    @Transactional
    public void requestPasswordReset(RequestPasswordResetRequestDto requestPasswordResetRequestDto) {
        log.debug("Enter requestPasswordReset for user {}", requestPasswordResetRequestDto.getEmail());

        User user = userRepository.findByEmail(requestPasswordResetRequestDto.getEmail())
                .orElse(null);

        if (user == null) {
            log.error("User with email {} not found", requestPasswordResetRequestDto.getEmail());
            return;
        }

        String verificationToken = verificationTokenService.createVerificationToken(user, TokenType.PASSWORD_RESET);

        String resetPasswordRedirectUrl = verificationLinkService.buildResetPasswordLink(verificationToken);

        emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), resetPasswordRedirectUrl);
        log.debug("Exit requestPasswordReset");
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDto resetPasswordRequestDto) throws EntityNotFoundException {
        log.debug("Enter resetPassword");

        VerificationToken verificationToken = verificationTokenService.validateToken(resetPasswordRequestDto.getResetPasswordToken());

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        User user = verificationToken.getUser();

        user.setPassword(passwordEncoder.encode(resetPasswordRequestDto.getNewPassword()));
        userRepository.save(user);

        log.debug("Exit resetPassword");
    }

}
