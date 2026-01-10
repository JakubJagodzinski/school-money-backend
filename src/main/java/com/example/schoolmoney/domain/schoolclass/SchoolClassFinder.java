package com.example.schoolmoney.domain.schoolclass;

import com.example.schoolmoney.common.constants.messages.domain.SchoolClassMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SchoolClassFinder {

    private final SchoolClassRepository schoolClassRepository;

    @Transactional(readOnly = true)
    public SchoolClass getByIdOrThrow(UUID schoolClassId) {
        return schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> {
                    log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                    return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                });
    }

}
