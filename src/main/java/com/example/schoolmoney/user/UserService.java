package com.example.schoolmoney.user;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.PasswordMessages;
import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.user.dto.request.ChangePasswordRequestDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public void changePassword(ChangePasswordRequestDto changePasswordRequestDto) throws IllegalArgumentException {
        log.debug("Enter changePassword");

        User user = securityUtils.getCurrentUser();

        if (!passwordEncoder.matches(changePasswordRequestDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException(PasswordMessages.WRONG_PASSWORD);
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

            emailService.sendAccountBlockExpiredEmail(user.getEmail(), user.getFirstName());
        }

        log.debug("Exit unblockUsersWithExpiredBlock");
    }

}
