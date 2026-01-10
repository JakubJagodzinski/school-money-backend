package com.example.schoolmoney.domain.child;

import com.example.schoolmoney.common.constants.messages.domain.ChildMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChildFinder {

    private final ChildRepository childRepository;

    @Transactional(readOnly = true)
    public Child getByIdOrThrow(UUID childId) {
        return childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.warn(ChildMessages.CHILD_NOT_FOUND);
                    return new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
                });
    }

}
