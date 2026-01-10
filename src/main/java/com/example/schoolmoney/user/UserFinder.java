package com.example.schoolmoney.user;

import com.example.schoolmoney.common.constants.messages.UserMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserFinder {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getByIdOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn(UserMessages.USER_NOT_FOUND);
                    return new EntityNotFoundException(UserMessages.USER_NOT_FOUND);
                });
    }

}
