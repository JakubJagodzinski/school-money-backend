package com.example.schoolmoney.domain.parent;

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
public class ParentFinder {

    private final ParentRepository parentRepository;

    @Transactional(readOnly = true)
    public Parent getByIdOrThrow(UUID parentId) {
        return parentRepository.findById(parentId)
                .orElseThrow(() -> {
                    log.error("Parent with id={} not found", parentId);
                    return new EntityNotFoundException(UserMessages.USER_NOT_FOUND);
                });
    }

    @Transactional(readOnly = true)
    public void assertParentExists(UUID parentId) {
        getByIdOrThrow(parentId);
    }

}
