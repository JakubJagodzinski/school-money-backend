package com.example.schoolmoney.domain.schoolclass;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.SchoolClassMessages;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.child.dto.ChildMapper;
import com.example.schoolmoney.domain.child.dto.response.ChildWithParentInfoResponseDto;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fund.FundStatus;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.schoolclass.dto.SchoolClassMapper;
import com.example.schoolmoney.domain.schoolclass.dto.request.CreateSchoolClassRequestDto;
import com.example.schoolmoney.domain.schoolclass.dto.response.SchoolClassInvitationCodeResponseDto;
import com.example.schoolmoney.domain.schoolclass.dto.response.SchoolClassResponseDto;
import com.example.schoolmoney.utils.InvitationCodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SchoolClassService {

    private final SchoolClassMapper schoolClassMapper;

    private final ChildMapper childMapper;

    private final SchoolClassRepository schoolClassRepository;

    private final ChildRepository childRepository;

    private final ParentRepository parentRepository;

    private final FundRepository fundRepository;

    private final SecurityUtils securityUtils;

    @Transactional
    public SchoolClassResponseDto createSchoolClass(CreateSchoolClassRequestDto createSchoolClassRequestDto) {
        log.debug("Enter createSchoolClass(createSchoolClassRequestDto={})", createSchoolClassRequestDto);

        UUID userId = securityUtils.getCurrentUserId();

        log.debug("Creating school class for user with userId={}", userId);
        Parent parent = parentRepository.getReferenceById(userId);

        String invitationCode = InvitationCodeGenerator.generate();

        SchoolClass schoolClass = SchoolClass
                .builder()
                .treasurer(parent)
                .schoolClassName(createSchoolClassRequestDto.getSchoolClassName())
                .schoolClassYear(createSchoolClassRequestDto.getSchoolClassYear())
                .invitationCode(invitationCode)
                .build();

        schoolClassRepository.save(schoolClass);
        log.info("School class saved {}", schoolClass);

        log.debug("Exit createSchoolClass");
        return schoolClassMapper.toDto(schoolClass);
    }

    public Page<SchoolClassResponseDto> getAllSchoolClasses(Pageable pageable) {
        log.debug("Enter getAllSchoolClasses(pageable={})", pageable);

        Page<SchoolClass> schoolClassPage = schoolClassRepository.findAll(pageable);

        log.debug("Fetched {} school classes for pageable={}", schoolClassPage.getTotalElements(), pageable);
        log.debug("Exit getAllSchoolClasses");
        return schoolClassPage.map(schoolClassMapper::toDto);
    }

    public Page<SchoolClassResponseDto> getTreasurerAndParentChildrenSchoolClasses(Pageable pageable) {
        log.debug("Enter getTreasurerAndParentChildrenSchoolClasses(pageable={})", pageable);

        UUID userId = securityUtils.getCurrentUserId();

        List<UUID> parentChildrenSchoolClassesIds = childRepository.findDistinctSchoolClassIdsByParentUserId(userId);

        log.debug("Fetching {} school classes for user with userId={}", parentChildrenSchoolClassesIds.size(), userId);

        Page<SchoolClass> schoolClassPage = schoolClassRepository.findAllByTreasurer_UserIdOrSchoolClassIdIn(userId, parentChildrenSchoolClassesIds, pageable);

        Page<SchoolClassResponseDto> schoolClassResponseDtoPage = schoolClassPage.map(schoolClassMapper::toDto);
        schoolClassResponseDtoPage.forEach(schoolClass -> {
            UUID schoolClassId = schoolClass.getSchoolClassId();

            long numberOfChildren = childRepository.countBySchoolClass_SchoolClassId(schoolClassId);
            schoolClass.setNumberOfChildren(numberOfChildren);

            long numberOfActiveFunds = fundRepository.countBySchoolClass_SchoolClassIdAndFundStatus(schoolClassId, FundStatus.ACTIVE);
            schoolClass.setNumberOfActiveFunds(numberOfActiveFunds);
        });

        log.debug("Exit getTreasurerAndParentChildrenSchoolClasses");
        return schoolClassResponseDtoPage;
    }

    public Page<ChildWithParentInfoResponseDto> getSchoolClassAllChildren(UUID schoolClassId, Pageable pageable) throws EntityNotFoundException, AccessDeniedException {
        log.debug("Enter getSchoolClassAllChildren(schoolClassId={}, pageable={})", schoolClassId, pageable);

        UUID userId = securityUtils.getCurrentUserId();

        SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> {
                    log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                    return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                });

        boolean hasAnyChildrenInSchoolClass = childRepository.existsByParent_UserIdAndSchoolClass_SchoolClassId(userId, schoolClassId);
        boolean isTreasurer = schoolClass.getTreasurer().getUserId().equals(userId);

        if (!hasAnyChildrenInSchoolClass && !isTreasurer) {
            log.warn(SchoolClassMessages.PARENT_DOES_NOT_HAVE_ACCESS_TO_THIS_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.PARENT_DOES_NOT_HAVE_ACCESS_TO_THIS_CLASS);
        }

        Page<Child> schoolClassChildren = childRepository.findAllBySchoolClass_SchoolClassId(schoolClassId, pageable);
        log.debug("Fetched {} children for school class with schoolClassId={}", schoolClassChildren.getTotalElements(), schoolClassId);

        log.debug("Exit getSchoolClassAllChildren");
        return schoolClassChildren.map(childMapper::toWithParentInfoDto);
    }

    @Transactional
    public SchoolClassInvitationCodeResponseDto regenerateInvitationCode(UUID schoolClassId) throws EntityNotFoundException, AccessDeniedException {
        log.debug("Enter regenerateInvitationCode(schoolClassId={})", schoolClassId);

        SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> {
                    log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                    return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();

        if (!schoolClass.getTreasurer().getUserId().equals(userId)) {
            log.warn(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
        }

        String invitationCode = InvitationCodeGenerator.generate();

        schoolClass.setInvitationCode(invitationCode);
        schoolClassRepository.save(schoolClass);
        log.info("Invitation code regenerated for school class {}", schoolClass);

        log.debug("Exit regenerateInvitationCode");
        return schoolClassMapper.toInvitationCodeDto(schoolClass);
    }

}
