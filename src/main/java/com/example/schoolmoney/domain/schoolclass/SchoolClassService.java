package com.example.schoolmoney.domain.schoolclass;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.SchoolClassMessages;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.child.dto.ChildMapper;
import com.example.schoolmoney.domain.child.dto.response.ChildResponseDto;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fund.FundStatus;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.schoolclass.dto.SchoolClassMapper;
import com.example.schoolmoney.domain.schoolclass.dto.request.CreateSchoolClassRequestDto;
import com.example.schoolmoney.domain.schoolclass.dto.response.SchoolClassResponseDto;
import com.example.schoolmoney.utils.InvitationCodeGenerator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        log.debug("Enter createSchoolClass {}", createSchoolClassRequestDto);

        UUID userId = securityUtils.getCurrentUserId();

        log.debug("Creating school class for user {}", userId);
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
        log.info("school class saved {}", schoolClass);

        log.debug("exit createSchoolClass");

        return schoolClassMapper.toDto(schoolClass);
    }

    public Page<SchoolClassResponseDto> getAllSchoolClasses(Pageable pageable) {
        log.debug("Enter getAllSchoolClasses(pageable={})", pageable);

        Page<SchoolClass> schoolClassPage = schoolClassRepository.findAll(pageable);

        log.info("Fetched {} school classes for pageable={}", schoolClassPage.getTotalElements(), pageable);
        log.debug("Exit getAllSchoolClasses(pageable={})", pageable);

        return schoolClassPage.map(schoolClassMapper::toDto);
    }

    public Page<SchoolClassResponseDto> getParentChildrenSchoolClasses(Pageable pageable) {
        log.debug("Enter getSchoolClasses(pageable={})", pageable);

        UUID userId = securityUtils.getCurrentUserId();

        List<UUID> parentChildrenSchoolClassesIds = childRepository.findDistinctSchoolClassIdsByParentUserId(userId);

        log.debug("Fetching {} school classes for user {}", parentChildrenSchoolClassesIds.size(), userId);

        if (parentChildrenSchoolClassesIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<SchoolClass> schoolClassPage = schoolClassRepository.findAllBySchoolClassIdIn(parentChildrenSchoolClassesIds, pageable);

        Page<SchoolClassResponseDto> schoolClassResponseDtoPage = schoolClassPage.map(schoolClassMapper::toDto);
        schoolClassResponseDtoPage.forEach(schoolClass -> {
            UUID schoolClassId = schoolClass.getSchoolClassId();

            long numberOfChildren = childRepository.countBySchoolClass_SchoolClassId(schoolClassId);
            schoolClass.setNumberOfChildren(numberOfChildren);

            long numberOfActiveFunds = fundRepository.countBySchoolClass_SchoolClassIdAndFundStatus(schoolClassId, FundStatus.ACTIVE);
            schoolClass.setNumberOfActiveFunds(numberOfActiveFunds);
        });

        log.debug("Exit getSchoolClasses");

        return schoolClassResponseDtoPage;
    }

    public Page<ChildResponseDto> getSchoolClassAllChildren(UUID schoolClassId, Pageable pageable) throws EntityNotFoundException {
        log.debug("Enter getSchoolClassAllChildren(schoolClassId={}, pageable={})", schoolClassId, pageable);

        UUID userId = securityUtils.getCurrentUserId();

        log.debug("Fetching children for school class {} for user {}", schoolClassId, userId);

        SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> {
                    log.error(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                    return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                });

        boolean isParentTreasurer = schoolClass.getTreasurer().getUserId().equals(userId);
        boolean hasParentChildInSchoolClass = childRepository.existsByParent_UserIdAndSchoolClass_SchoolClassId(userId, schoolClassId);

        if (!hasParentChildInSchoolClass && !isParentTreasurer) {
            log.error(SchoolClassMessages.PARENT_DOES_NOT_HAVE_ANY_CHILD_IN_THIS_CLASS);
            throw new EntityNotFoundException(SchoolClassMessages.PARENT_DOES_NOT_HAVE_ANY_CHILD_IN_THIS_CLASS);
        }

        Page<Child> schoolClassChildren = childRepository.findAllBySchoolClass_SchoolClassId(schoolClassId, pageable);
        log.info("Fetched {} children for school class {}", schoolClassChildren.getTotalElements(), schoolClassId);

        log.debug("Exit getSchoolClassAllChildren");

        return schoolClassChildren.map(childMapper::toDto);
    }

}
