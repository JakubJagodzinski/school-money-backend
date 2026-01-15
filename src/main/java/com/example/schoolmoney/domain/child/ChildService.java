package com.example.schoolmoney.domain.child;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.ChildMessages;
import com.example.schoolmoney.common.constants.messages.domain.ParentMessages;
import com.example.schoolmoney.common.constants.messages.domain.SchoolClassMessages;
import com.example.schoolmoney.domain.child.dto.ChildMapper;
import com.example.schoolmoney.domain.child.dto.request.CreateChildRequestDto;
import com.example.schoolmoney.domain.child.dto.request.UpdateChildRequestDto;
import com.example.schoolmoney.domain.child.dto.response.ChildResponseDto;
import com.example.schoolmoney.domain.child.dto.response.ChildShortInfoResponseDto;
import com.example.schoolmoney.domain.child.dto.response.ChildWithSchoolClassInfoResponseDto;
import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.fund.FundStatus;
import com.example.schoolmoney.domain.fundoperation.FundOperationRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChildService {

    private final ChildMapper childMapper;

    private final ChildRepository childRepository;

    private final ParentRepository parentRepository;

    private final SchoolClassRepository schoolClassRepository;

    private final SecurityUtils securityUtils;

    private final EmailService emailService;

    private final ChildAccessService childAccessService;

    private final FundOperationRepository fundOperationRepository;

    private final ChildFinder childFinder;

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
    public void assignChildToSchoolClass(UUID childId, String invitationCode) throws EntityNotFoundException, IllegalStateException {
        log.debug("Enter assignChildToSchoolClass(childId={}, invitationCode={})", childId, invitationCode);

        Child child = childFinder.getByIdOrThrow(childId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        childAccessService.assertCanAccessChild(parent, child);

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

        emailService.sendChildAddedToClassEmail(
                parent.getEmail(),
                parent.getFirstName(),
                child.getFullName(),
                schoolClass.getFullName(),
                parent.isNotificationsEnabled()
        );

        log.debug("Exit assignChildToSchoolClass");
    }

    @Transactional
    public void unassignChildFromSchoolClass(UUID childId) throws EntityNotFoundException, IllegalStateException {
        log.debug("Enter unassignChildFromSchoolClass(childId={})", childId);

        Child child = childFinder.getByIdOrThrow(childId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        childAccessService.assertCanAccessChild(parent, child);

        if (fundOperationRepository.existsByChild_ChildIdAndFund_FundStatusAndOperationStatus(
                childId,
                FundStatus.ACTIVE,
                FinancialOperationStatus.SUCCESS
        )) {
            log.warn(ChildMessages.CHILD_HAS_ACTIVE_FUNDS);
            throw new IllegalStateException(ChildMessages.CHILD_HAS_ACTIVE_FUNDS);
        }

        child.setSchoolClass(null);
        childRepository.save(child);
        log.info("Child {} removed from school class {}", child, child.getSchoolClass());

        log.debug("Exit unassignChildFromSchoolClass");
    }

    public Page<ChildWithSchoolClassInfoResponseDto> getParentAllChildren(Pageable pageable) {
        log.debug("Enter getParentAllChildren(pageable={}", pageable);

        UUID userId = securityUtils.getCurrentUserId();

        Page<Child> childPage = childRepository.findAllByParent_UserId(userId, pageable);

        log.debug("Exit getParentAllChildren");
        return childPage.map(childMapper::toWithSchoolClassInfoDto);
    }

    @Transactional
    public ChildShortInfoResponseDto updateChild(UUID childId, UpdateChildRequestDto updateChildRequestDto) throws EntityNotFoundException {
        log.debug("Enter updateChild(childId={}, updateChildRequestDto={})", childId, updateChildRequestDto);

        Child child = childFinder.getByIdOrThrow(childId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        childAccessService.assertCanAccessChild(parent, child);

        childMapper.updateEntityFromDto(updateChildRequestDto, child);
        childRepository.save(child);
        log.info("Child updated {}", child);

        log.debug("Exit updateChild");
        return childMapper.toShortInfoDto(child);
    }

    @Transactional(readOnly = true)
    public ChildResponseDto getChildById(UUID childId) {
        log.debug("Enter getChildById(childId={})", childId);

        Child child = childFinder.getByIdOrThrow(childId);

        log.debug("Exit getChildById(childId={})", childId);
        return childMapper.toDto(child);
    }

}
