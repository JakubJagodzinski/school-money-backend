package com.example.schoolmoney.domain.child;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.ChildMessages;
import com.example.schoolmoney.common.constants.messages.domain.ParentMessages;
import com.example.schoolmoney.common.constants.messages.domain.SchoolClassMessages;
import com.example.schoolmoney.domain.child.dto.ChildMapper;
import com.example.schoolmoney.domain.child.dto.request.CreateChildRequestDto;
import com.example.schoolmoney.domain.child.dto.request.UpdateChildRequestDto;
import com.example.schoolmoney.domain.child.dto.response.ChildShortInfoResponseDto;
import com.example.schoolmoney.domain.child.dto.response.ChildWithSchoolClassInfoResponseDto;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.domain.schoolclass.SchoolClassRepository;
import com.example.schoolmoney.email.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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

    private final EmailService emailService;

    @Transactional
    public ChildShortInfoResponseDto createChild(CreateChildRequestDto createChildRequestDto) throws EntityNotFoundException {
        log.debug("Enter createChild(createChildRequestDto={})", createChildRequestDto);

        UUID userId = securityUtils.getCurrentUserId();

        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn(ParentMessages.PARENT_NOT_FOUND);
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
    public void assignChildToSchoolClass(UUID childId, String invitationCode) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter assignChildToSchoolClass(childId={}, invitationCode={})", childId, invitationCode);

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.warn(ChildMessages.CHILD_NOT_FOUND);
                    return new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();

        if (!child.getParent().getUserId().equals(userId)) {
            log.warn(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
            throw new AccessDeniedException(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
        }

        SchoolClass schoolClass = schoolClassRepository.findByInvitationCode(invitationCode)
                .orElseThrow(() -> {
                    log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                    return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                });

        if (child.getSchoolClass() != null) {
            log.warn(ChildMessages.CHILD_ALREADY_IN_SCHOOL_CLASS);
            throw new IllegalStateException(ChildMessages.CHILD_ALREADY_IN_SCHOOL_CLASS);
        }

        child.setSchoolClass(schoolClass);
        childRepository.save(child);
        log.info("Child {} assigned to school class {}", child, schoolClass);

        Parent parent = child.getParent();

        emailService.sendChildAddedToClassEmail(
                parent.getEmail(),
                parent.getFirstName(),
                child.getFullName(),
                schoolClass.getFullName(),
                parent.isNotificationsEnabled()
        );

        log.debug("Exit assignChildToSchoolClass");
    }

    public Page<ChildWithSchoolClassInfoResponseDto> getParentAllChildren(Pageable pageable) {
        log.debug("Enter getParentAllChildren(pageable={}", pageable);

        UUID userId = securityUtils.getCurrentUserId();

        Page<Child> childPage = childRepository.findAllByParent_UserId(userId, pageable);

        log.debug("Exit getParentAllChildren");
        return childPage.map(childMapper::toWithSchoolClassInfoDto);
    }

    @Transactional
    public ChildShortInfoResponseDto updateChild(UUID childId, UpdateChildRequestDto updateChildRequestDto) throws EntityNotFoundException, AccessDeniedException {
        log.debug("Enter updateChild(childId={}, updateChildRequestDto={})", childId, updateChildRequestDto);

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.warn(ChildMessages.CHILD_NOT_FOUND);
                    return new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();

        if (!child.getParent().getUserId().equals(userId)) {
            log.warn(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
            throw new AccessDeniedException(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
        }

        childMapper.updateEntityFromDto(updateChildRequestDto, child);
        childRepository.save(child);
        log.info("Child updated {}", child);

        log.debug("Exit updateChild");
        return childMapper.toShortInfoDto(child);
    }

}
