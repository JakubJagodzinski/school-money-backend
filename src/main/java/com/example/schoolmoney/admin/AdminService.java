package com.example.schoolmoney.admin;

import com.example.schoolmoney.admin.dto.request.BlockUserRequestDto;
import com.example.schoolmoney.admin.dto.request.UnblockUserRequestDto;
import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.auth.authtoken.AuthTokenService;
import com.example.schoolmoney.common.constants.messages.UserMessages;
import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.user.User;
import com.example.schoolmoney.user.UserRepository;
import com.example.schoolmoney.usermoderation.UserModerationEventService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;

    private final UserModerationEventService userModerationEventService;

    private final AuthTokenService authTokenService;

    private final SecurityUtils securityUtils;
    private final EmailService emailService;

    @Transactional
    public void blockUser(UUID userId, BlockUserRequestDto blockUserRequestDto) throws EntityNotFoundException, IllegalStateException {
        log.debug("Enter blockUser");

        UUID adminUserId = securityUtils.getCurrentUserId();

        if (adminUserId.equals(userId)) {
            log.warn(UserMessages.YOU_CANNOT_BLOCK_YOURSELF);
            throw new IllegalStateException(UserMessages.YOU_CANNOT_BLOCK_YOURSELF);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn(UserMessages.USER_NOT_FOUND);
                    return new EntityNotFoundException(UserMessages.USER_NOT_FOUND);
                });

        if (user.isBlocked()) {
            log.warn("User {} already blocked", user.getEmail());
            return;
        }

        Instant until;
        if (blockUserRequestDto.getDurationInDays() > 0) {
            until = Instant.now().plus(blockUserRequestDto.getDurationInDays(), ChronoUnit.DAYS);
        } else {
            until = null;
        }

        user.setBlocked(true);
        user.setBlockedUntil(until);
        userRepository.save(user);
        log.info("User {} blocked", user.getEmail());

        authTokenService.revokeAllUserAuthTokens(user);

        User adminUser = securityUtils.getCurrentUser();
        userModerationEventService.saveUserBlockEvent(user, adminUser, blockUserRequestDto.getReason(), until);

        emailService.sendAccountBlockedEmail(
                user.getEmail(),
                user.getFirstName(),
                blockUserRequestDto.getReason().toString(),
                blockUserRequestDto.getDurationInDays(),
                until
        );

        log.debug("Exit blockUser");
    }

    @Transactional
    public void unblockUser(UUID userId, UnblockUserRequestDto unblockUserRequestDto) throws EntityNotFoundException {
        log.debug("Enter unblockUser");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn(UserMessages.USER_NOT_FOUND);
                    return new EntityNotFoundException(UserMessages.USER_NOT_FOUND);
                });

        if (!user.isBlocked()) {
            log.warn("User {} is not blocked", user.getEmail());
            return;
        }

        user.setBlocked(false);
        user.setBlockedUntil(null);
        userRepository.save(user);
        log.info("User {} unblocked", user.getEmail());

        User adminUser = securityUtils.getCurrentUser();
        userModerationEventService.saveUserUnblockEvent(user, adminUser, unblockUserRequestDto.getReason());

        emailService.sendAccountUnblockedEmail(
                user.getEmail(),
                user.getFirstName(),
                unblockUserRequestDto.getReason().toString()
        );

        log.debug("Exit unblockUser");
    }

}
