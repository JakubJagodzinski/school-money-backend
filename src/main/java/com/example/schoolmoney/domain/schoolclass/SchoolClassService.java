package com.example.schoolmoney.domain.schoolclass;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.child.dto.ChildMapper;
import com.example.schoolmoney.domain.child.dto.response.ChildWithParentInfoResponseDto;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fund.FundStatus;
import com.example.schoolmoney.domain.fundoperation.FundOperationFinder;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentFinder;
import com.example.schoolmoney.domain.schoolclass.dto.SchoolClassMapper;
import com.example.schoolmoney.domain.schoolclass.dto.request.CreateSchoolClassRequestDto;
import com.example.schoolmoney.domain.schoolclass.dto.request.UpdateSchoolClassRequestDto;
import com.example.schoolmoney.domain.schoolclass.dto.response.SchoolClassInvitationCodeResponseDto;
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

    private final FundRepository fundRepository;

    private final SecurityUtils securityUtils;

    private final SchoolClassAccessService schoolClassAccessService;

    private final SchoolClassFinder schoolClassFinder;

    private final FundOperationFinder fundOperationFinder;

    private final ParentFinder parentFinder;

    @Transactional
    public SchoolClassResponseDto createSchoolClass(CreateSchoolClassRequestDto createSchoolClassRequestDto) {
        log.debug("Enter createSchoolClass(createSchoolClassRequestDto={})", createSchoolClassRequestDto);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        SchoolClass schoolClass = SchoolClass
                .builder()
                .treasurer(parent)
                .schoolClassName(createSchoolClassRequestDto.getSchoolClassName())
                .schoolClassYear(createSchoolClassRequestDto.getSchoolClassYear())
                .invitationCode(InvitationCodeGenerator.generate())
                .build();

        schoolClassRepository.save(schoolClass);
        log.info("School class with id={} created", schoolClass.getSchoolClassId());

        log.debug("Exit createSchoolClass(createSchoolClassRequestDto={})", createSchoolClassRequestDto);
        return schoolClassMapper.toDto(schoolClass);
    }

    @Transactional(readOnly = true)
    public Page<SchoolClassResponseDto> getAllSchoolClasses(Pageable pageable) {
        log.debug("Enter getAllSchoolClasses(pageable={})", pageable);

        Page<SchoolClass> schoolClassPage = schoolClassRepository.findAll(pageable);

        log.debug("Exit getAllSchoolClasses(pageable={})", pageable);
        return schoolClassPage.map(schoolClassMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<SchoolClassResponseDto> getTreasurerAndParentChildrenSchoolClasses(Pageable pageable) {
        log.debug("Enter getTreasurerAndParentChildrenSchoolClasses(pageable={})", pageable);

        UUID userId = securityUtils.getCurrentUserId();
        parentFinder.assertParentExists(userId);

        List<UUID> parentChildrenSchoolClassesIds = childRepository.findDistinctSchoolClassIdsByParentUserId(userId);

        log.debug("Fetching {} school classes for user with userId={}", parentChildrenSchoolClassesIds.size(), userId);

        Page<SchoolClass> schoolClassPage = schoolClassRepository.findAllByTreasurer_UserIdOrSchoolClassIdIn(userId, parentChildrenSchoolClassesIds, pageable);

        Page<SchoolClassResponseDto> schoolClassResponseDtoPage = schoolClassPage.map(schoolClassMapper::toDto);
        schoolClassResponseDtoPage.forEach(this::updateSchoolClassStatistics);

        log.debug("Exit getTreasurerAndParentChildrenSchoolClasses(pageable={})", pageable);
        return schoolClassResponseDtoPage;
    }

    @Transactional(readOnly = true)
    public Page<ChildWithParentInfoResponseDto> getSchoolClassAllChildren(UUID schoolClassId, Pageable pageable) throws EntityNotFoundException {
        log.debug("Enter getSchoolClassAllChildren(schoolClassId={}, pageable={})", schoolClassId, pageable);

        SchoolClass schoolClass = schoolClassFinder.getByIdOrThrow(schoolClassId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        schoolClassAccessService.assertCanViewSchoolClass(parent, schoolClass);

        Page<Child> schoolClassChildren = childRepository.findAllBySchoolClass_SchoolClassId(schoolClassId, pageable);
        log.debug("Fetched {} children for school class with schoolClassId={}", schoolClassChildren.getTotalElements(), schoolClassId);

        log.debug("Exit getSchoolClassAllChildren(schoolClassId={}, pageable={})", schoolClassId, pageable);
        return schoolClassChildren.map(childMapper::toWithParentInfoDto);
    }

    private void updateSchoolClassStatistics(SchoolClassResponseDto schoolClassResponseDto) {
        log.debug("Enter updateSchoolClassStatistics(schoolClassResponseDto={})", schoolClassResponseDto);

        UUID schoolClassId = schoolClassResponseDto.getSchoolClassId();

        long numberOfChildren = childRepository.countBySchoolClass_SchoolClassId(schoolClassId);
        schoolClassResponseDto.setNumberOfChildren(numberOfChildren);

        long numberOfScheduledFunds = fundRepository.countBySchoolClass_SchoolClassIdAndFundStatus(schoolClassId, FundStatus.SCHEDULED);
        schoolClassResponseDto.setNumberOfScheduledFunds(numberOfScheduledFunds);

        long numberOfActiveFunds = fundRepository.countBySchoolClass_SchoolClassIdAndFundStatus(schoolClassId, FundStatus.ACTIVE);
        schoolClassResponseDto.setNumberOfActiveFunds(numberOfActiveFunds);

        long numberOfFinishedFunds = fundRepository.countBySchoolClass_SchoolClassIdAndFundStatus(schoolClassId, FundStatus.FINISHED);
        schoolClassResponseDto.setNumberOfFinishedFunds(numberOfFinishedFunds);

        long activeFundsCurrentBalanceInCents = fundOperationFinder.getSchoolClassFundsCurrentBalanceInCents(schoolClassId, FundStatus.ACTIVE);
        schoolClassResponseDto.setActiveFundsCurrentBalanceInCents(activeFundsCurrentBalanceInCents);

        long finishedFundsCurrentBalanceInCents = fundOperationFinder.getSchoolClassFundsCurrentBalanceInCents(schoolClassId, FundStatus.FINISHED);
        schoolClassResponseDto.setFinishedFundsCurrentBalanceInCents(finishedFundsCurrentBalanceInCents);

        log.debug("Exit updateSchoolClassStatistics(schoolClassResponseDto={})", schoolClassResponseDto);
    }

    @Transactional(readOnly = true)
    public SchoolClassResponseDto getSchoolClassById(UUID schoolClassId) {
        log.debug("Enter getSchoolClassById(schoolClassId={})", schoolClassId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        SchoolClass schoolClass = schoolClassFinder.getByIdOrThrow(schoolClassId);
        schoolClassAccessService.assertCanViewSchoolClass(parent, schoolClass);

        SchoolClassResponseDto schoolClassResponseDto = schoolClassMapper.toDto(schoolClass);
        updateSchoolClassStatistics(schoolClassResponseDto);

        log.debug("Exit getSchoolClassById(schoolClassId={})", schoolClassId);
        return schoolClassResponseDto;
    }

    @Transactional
    public SchoolClassResponseDto updateSchoolClass(UUID schoolClassId, UpdateSchoolClassRequestDto updateSchoolClassRequestDto) {
        log.debug("Enter updateSchoolClass(schoolClassId={}, updateSchoolClassRequestDto={})", schoolClassId, updateSchoolClassRequestDto);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        SchoolClass schoolClass = schoolClassFinder.getByIdOrThrow(schoolClassId);
        schoolClassAccessService.assertCanViewSchoolClass(parent, schoolClass);
        schoolClassAccessService.assertCanEditSchoolClass(parent, schoolClass);

        schoolClassMapper.updateEntityFromDto(updateSchoolClassRequestDto, schoolClass);
        SchoolClass updatedSchoolClass = schoolClassRepository.save(schoolClass);
        log.info("School class with id={} updated successfully", updatedSchoolClass.getSchoolClassId());

        log.debug("Exit updateSchoolClass(schoolClassId={}, updateSchoolClassRequestDto={})", schoolClassId, updateSchoolClassRequestDto);
        return schoolClassMapper.toDto(updatedSchoolClass);
    }

    @Transactional
    public SchoolClassInvitationCodeResponseDto regenerateInvitationCode(UUID schoolClassId) {
        log.debug("Enter regenerateInvitationCode(schoolClassId={})", schoolClassId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        SchoolClass schoolClass = schoolClassFinder.getByIdOrThrow(schoolClassId);
        schoolClassAccessService.assertCanViewSchoolClass(parent, schoolClass);
        schoolClassAccessService.assertCanEditSchoolClass(parent, schoolClass);

        schoolClass.setInvitationCode(InvitationCodeGenerator.generate());
        schoolClassRepository.save(schoolClass);
        log.info("Invitation code regenerated for school class with id={}", schoolClass.getSchoolClassId());

        log.debug("Exit regenerateInvitationCode(schoolClassId={})", schoolClassId);
        return schoolClassMapper.toInvitationCodeDto(schoolClass);
    }

}
