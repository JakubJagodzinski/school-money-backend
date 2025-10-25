package com.example.schoolmoney.usermoderation;

import com.example.schoolmoney.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserModerationEventService {

    private final UserModerationEventRepository userModerationEventRepository;

    public void saveUserBlockEvent(User user, User adminUser, UserModerationReason reason, Instant until) {
        log.debug("Enter saveUserBlockEvent");

        UserModerationEvent event = UserModerationEvent.builder()
                .user(user)
                .performedBy(adminUser)
                .action(UserModerationAction.BLOCK)
                .reason(reason)
                .until(until)
                .build();

        userModerationEventRepository.save(event);
        log.info("User block event saved for user {}", user.getEmail());

        log.debug("Exit saveUserBlockEvent");
    }

    public void saveUserUnblockEvent(User user, User adminUser, UserModerationReason reason) {
        log.debug("Enter saveUserUnblockEvent");

        UserModerationEvent event = UserModerationEvent.builder()
                .user(user)
                .performedBy(adminUser)
                .action(UserModerationAction.UNBLOCK)
                .reason(reason)
                .build();

        userModerationEventRepository.save(event);
        log.info("User unblock event saved for user {}", user.getEmail());

        log.debug("Exit saveUserUnblockEvent");
    }

}
