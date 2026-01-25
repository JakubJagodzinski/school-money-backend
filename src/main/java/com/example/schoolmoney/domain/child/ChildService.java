package com.example.schoolmoney.domain.child;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.ChildMessages;
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
import com.example.schoolmoney.domain.parent.ParentFinder;
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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChildService {

    private final ChildMapper childMapper;

    private final ChildRepository childRepository;

    private final SchoolClassRepository schoolClassRepository;

    private final SecurityUtils securityUtils;

    private final EmailService emailService;

    private final ChildAccessService childAccessService;

    private final FundOperationRepository fundOperationRepository;

    private final ChildFinder childFinder;

    private final ParentFinder parentFinder;

    @Transactional
    public ChildShortInfoResponseDto createChild(CreateChildRequestDto createChildRequestDto) {
        log.debug("Enter createChild(createChildRequestDto={})", createChildRequestDto);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Child child = Child.builder()
                .parent(parent)
                .firstName(createChildRequestDto.getFirstName())
                .lastName(createChildRequestDto.getLastName())
                .birthDate(createChildRequestDto.getBirthDate())
                .build();

        Child savedChild = childRepository.save(child);
        log.info("Child saved with id={}", savedChild.getChildId());

        log.debug("Exit createChild(createChildRequestDto={})", createChildRequestDto);
        return childMapper.toShortInfoDto(savedChild);
    }

    @Transactional(readOnly = true)
    public ChildResponseDto getChildById(UUID childId) {
        log.debug("Enter getChildById(childId={})", childId);

        Child child = childFinder.getByIdOrThrow(childId);

        log.debug("Exit getChildById(childId={})", childId);
        return childMapper.toDto(child);
    }

    @Transactional(readOnly = true)
    public List<ChildWithSchoolClassInfoResponseDto> getParentAllChildren() {
        log.debug("Enter getParentAllChildren()");

        UUID userId = securityUtils.getCurrentUserId();
        parentFinder.assertParentExists(userId);

        List<Child> childList = childRepository.findAllByParent_UserId(userId);

        List<ChildWithSchoolClassInfoResponseDto> responseDtoList = childList.stream().map(childMapper::toWithSchoolClassInfoDto).toList();

        log.debug("Exit getParentAllChildren()");
        return responseDtoList;
    }

    @Transactional
    public ChildShortInfoResponseDto updateChild(UUID childId, UpdateChildRequestDto updateChildRequestDto) throws EntityNotFoundException {
        log.debug("Enter updateChild(childId={}, updateChildRequestDto={})", childId, updateChildRequestDto);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Child child = childFinder.getByIdOrThrow(childId);
        childAccessService.assertCanAccessChild(parent, child);

        childMapper.updateEntityFromDto(updateChildRequestDto, child);
        childRepository.save(child);
        log.info("Child with id={} updated successfully", child.getChildId());

        log.debug("Exit updateChild(childId={}, updateChildRequestDto={})", childId, updateChildRequestDto);
        return childMapper.toShortInfoDto(child);
    }

    @Transactional
    public void assignChildToSchoolClass(UUID childId, String invitationCode) throws EntityNotFoundException, IllegalStateException {
        log.debug("Enter assignChildToSchoolClass(childId={})", childId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Child child = childFinder.getByIdOrThrow(childId);
        childAccessService.assertCanAccessChild(parent, child);

        SchoolClass schoolClass = schoolClassRepository.findByInvitationCode(invitationCode).orElseThrow(() -> {
            log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
            return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
        });

        if (child.getSchoolClass() != null) {
            log.warn("Child with id={} is already assigned to school class", childId);
            return;
        }

        child.setSchoolClass(schoolClass);
        childRepository.save(child);
        log.info("Child with id={} assigned to school class with id={}", child.getChildId(), schoolClass.getSchoolClassId());

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        emailService.sendChildAddedToClassEmail(
                                parent.getEmail(),
                                parent.getFirstName(),
                                child.getFullName(),
                                schoolClass.getFullName(),
                                parent.isNotificationsEnabled()
                        );
                    }
                }
        );

        log.debug("Exit assignChildToSchoolClass(childId={})", childId);
    }

    @Transactional
    public void unassignChildFromSchoolClass(UUID childId) throws IllegalStateException {
        log.debug("Enter unassignChildFromSchoolClass(childId={})", childId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Child child = childFinder.getByIdOrThrow(childId);
        childAccessService.assertCanAccessChild(parent, child);

        if (fundOperationRepository.existsByChild_ChildIdAndFund_FundStatusAndOperationStatus(childId, FundStatus.ACTIVE, FinancialOperationStatus.SUCCESS)) {
            log.warn(ChildMessages.CHILD_HAS_ACTIVE_FUNDS);
            throw new IllegalStateException(ChildMessages.CHILD_HAS_ACTIVE_FUNDS);
        }

        child.setSchoolClass(null);
        childRepository.save(child);
        log.info("Child with id={} removed from school class {}", child.getChildId(), child.getSchoolClass());

        log.debug("Exit unassignChildFromSchoolClass(childId={})", childId);
    }

}
