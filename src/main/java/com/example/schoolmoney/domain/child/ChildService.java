package com.example.schoolmoney.domain.child;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.ChildMessages;
import com.example.schoolmoney.common.constants.messages.ParentMessages;
import com.example.schoolmoney.common.constants.messages.SchoolClassMessages;
import com.example.schoolmoney.domain.child.dto.ChildMapper;
import com.example.schoolmoney.domain.child.dto.request.CreateChildRequestDto;
import com.example.schoolmoney.domain.child.dto.response.ChildShortInfoResponseDto;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.domain.schoolclass.SchoolClassRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChildService {

    private final ChildMapper childMapper;

    private final ChildRepository childRepository;

    private final ParentRepository parentRepository;

    private final SchoolClassRepository schoolClassRepository;

    private final SecurityUtils securityUtils;

    @Transactional
    public ChildShortInfoResponseDto createChild(CreateChildRequestDto createChildRequestDto) throws EntityNotFoundException {
        log.debug("Enter createChild {}", createChildRequestDto);

        UUID userId = securityUtils.getCurrentUserId();

        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(ParentMessages.PARENT_NOT_FOUND);
                    return new EntityNotFoundException(ParentMessages.PARENT_NOT_FOUND);
                });

        Child child = Child
                .builder()
                .parent(parent)
                .firstName(createChildRequestDto.getFirstName())
                .lastName(createChildRequestDto.getLastName())
                .birthDate(createChildRequestDto.getBirthDate())
                .build();

        childRepository.save(child);
        log.info("Child saved {}", child);

        log.debug("Exit createChild");

        return childMapper.toShortInfoDto(child);
    }

    @Transactional
    public void assignChildToSchoolClass(UUID childId, String invitationCode) throws EntityNotFoundException, IllegalArgumentException {
        log.debug("Enter assignChildToSchoolClass childId={}, invitationCode={}", childId, invitationCode);

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.error(ChildMessages.CHILD_NOT_FOUND);
                    return new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();

        if (!child.getParent().getUserId().equals(userId)) {
            throw new IllegalArgumentException(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
        }

        SchoolClass schoolClass = schoolClassRepository.findByInvitationCode(invitationCode)
                .orElseThrow(() -> {
                    log.error(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                    return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                });

        if (child.getSchoolClass() != null) {
            log.error(ChildMessages.CHILD_ALREADY_IN_SCHOOL_CLASS);
            throw new IllegalArgumentException(ChildMessages.CHILD_ALREADY_IN_SCHOOL_CLASS);
        }

        child.setSchoolClass(schoolClass);
        childRepository.save(child);
        log.info("Child assigned to school class {}", child);

        log.debug("Exit assignChildToSchoolClass");
    }

    public Page<ChildShortInfoResponseDto> getParentAllChildren(Pageable pageable) {
        log.debug("Enter getParentAllChildren");

        UUID userId = securityUtils.getCurrentUserId();

        Page<Child> childPage = childRepository.findAllByParent_UserId(userId, pageable);

        log.debug("Exit getParentAllChildren");

        return childPage.map(childMapper::toShortInfoDto);
    }

}
