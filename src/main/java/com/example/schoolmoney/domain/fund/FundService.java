package com.example.schoolmoney.domain.fund;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.child.dto.ChildMapper;
import com.example.schoolmoney.domain.childignoredfund.ChildIgnoredFundRepository;
import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.fund.dto.FundMapper;
import com.example.schoolmoney.domain.fund.dto.request.CreateFundRequestDto;
import com.example.schoolmoney.domain.fund.dto.request.UpdateFundRequestDto;
import com.example.schoolmoney.domain.fund.dto.response.*;
import com.example.schoolmoney.domain.fundoperation.FundOperationFinder;
import com.example.schoolmoney.domain.fundoperation.FundOperationRepository;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentFinder;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.domain.schoolclass.SchoolClassAccessService;
import com.example.schoolmoney.domain.schoolclass.SchoolClassFinder;
import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.finance.FinanceConfiguration;
import com.example.schoolmoney.utils.IbanUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundService {

    private final FundMapper fundMapper;

    private final FundRepository fundRepository;

    private final FundOperationRepository fundOperationRepository;

    private final ChildRepository childRepository;

    private final SecurityUtils securityUtils;

    private final EmailService emailService;

    private final FinanceConfiguration financeConfiguration;

    private final ChildIgnoredFundRepository childIgnoredFundRepository;

    private final ChildMapper childMapper;

    private final FundAccessService fundAccessService;

    private final SchoolClassAccessService schoolClassAccessService;

    private final FundFinder fundFinder;

    private final SchoolClassFinder schoolClassFinder;

    private final FundOperationFinder fundOperationFinder;

    private final ParentFinder parentFinder;

    @Transactional
    public FundResponseDto createFund(CreateFundRequestDto requestDto) {
        log.debug("Enter createFund(requestDto={})", requestDto);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        SchoolClass schoolClass = schoolClassFinder.getByIdOrThrow(requestDto.getSchoolClassId());
        schoolClassAccessService.assertCanViewSchoolClass(parent, schoolClass);
        schoolClassAccessService.assertCanEditSchoolClass(parent, schoolClass);

        Fund fund = Fund.builder()
                .author(parent)
                .schoolClass(schoolClass)
                .amountPerChildInCents(requestDto.getAmountPerChildInCents())
                .currency(financeConfiguration.getCurrency())
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .startsAt(requestDto.getStartsAt())
                .endsAt(requestDto.getEndsAt())
                .iban(IbanUtil.generateRandomPlIban())
                .build();

        Fund savedFund = fundRepository.save(fund);
        log.info("Fund saved with id={}", savedFund.getFundId());

        FundResponseDto responseDto = fundMapper.toDto(savedFund);
        responseDto.setFundProgress(countFundProgress(savedFund.getFundId()));
        responseDto.setFundCurrentBalanceInCents(fundOperationFinder.getFundCurrentBalanceInCents(savedFund.getFundId()));

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        sendFundCreatedEmailsToParents(
                                parent.getFullName(),
                                savedFund.getTitle(),
                                schoolClass
                        );
                    }
                }
        );

        log.debug("Enter createFund(requestDto={})", requestDto);
        return responseDto;
    }

    private void sendFundCreatedEmailsToParents(String authorFullName, String fundTitle, SchoolClass schoolClass) {
        log.debug("Enter sendFundCreatedEmailsToParents(authorFullName={}, fundTitle={}, schoolClass={})", authorFullName, fundTitle, schoolClass);

        List<Parent> schoolClassParentsList = childRepository.findSchoolClassDistinctParents(schoolClass.getSchoolClassId());

        for (Parent schoolClassParent : schoolClassParentsList) {
            emailService.sendFundCreatedEmail(
                    schoolClassParent.getEmail(),
                    schoolClassParent.getFirstName(),
                    authorFullName,
                    fundTitle,
                    schoolClass.getFullName(),
                    schoolClassParent.isNotificationsEnabled()
            );
        }

        log.debug("Exit sendFundCreatedEmailsToParents(authorFullName={}, fundTitle={}, schoolClass={})", authorFullName, fundTitle, schoolClass);
    }

    @Transactional(readOnly = true)
    public FundResponseDto getFundById(UUID fundId) {
        log.debug("Enter getFundById(fundId={})", fundId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Fund fund = fundFinder.getByIdOrThrow(fundId);
        fundAccessService.assertCanViewFund(parent, fund);

        FundResponseDto fundResponseDto = fundMapper.toDto(fund);
        fundResponseDto.setFundProgress(countFundProgress(fundId));
        fundResponseDto.setFundCurrentBalanceInCents(fundOperationFinder.getFundCurrentBalanceInCents(fundId));

        log.debug("Exit getFundById(fundId={})", fundId);
        return fundResponseDto;
    }

    @Transactional(readOnly = true)
    public Page<FundResponseDto> getParentCreatedFunds(Pageable pageable) {
        log.debug("Enter getParentCreatedFunds(pageable={})", pageable);

        UUID userId = securityUtils.getCurrentUserId();
        parentFinder.assertParentExists(userId);

        Page<Fund> fundPage = fundRepository.findAllByAuthor_UserId(userId, pageable);

        Page<FundResponseDto> fundResponseDtoPage = fundPage.map(fundMapper::toDto);

        for (FundResponseDto fund : fundResponseDtoPage.getContent()) {
            fund.setFundProgress(countFundProgress(fund.getFundId()));
            fund.setFundCurrentBalanceInCents(fundOperationFinder.getFundCurrentBalanceInCents(fund.getFundId()));
        }

        log.debug("Exit getParentCreatedFunds(pageable={})", pageable);
        return fundResponseDtoPage;
    }

    @Transactional
    public FundResponseDto updateFund(UUID fundId, UpdateFundRequestDto updateFundRequestDto) {
        log.debug("Enter updateFund(fundId={}, updateFundRequestDto={})", fundId, updateFundRequestDto);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Fund fund = fundFinder.getByIdOrThrow(fundId);
        fundAccessService.assertCanViewFund(parent, fund);
        fundAccessService.assertCanEditFund(parent, fund);
        fundAccessService.assertFundIsNotBlocked(fund);

        fundMapper.updateEntityFromDto(updateFundRequestDto, fund);
        Fund savedFund = fundRepository.save(fund);
        log.info("Fund with id={} updated successfully", savedFund.getFundId());

        log.debug("Exit updateFund(fundId={}, updateFundRequestDto={})", fundId, updateFundRequestDto);
        return fundMapper.toDto(savedFund);
    }

    @Transactional(readOnly = true)
    public Page<FundWithChildrenResponseDto> getSchoolClassAllFunds(UUID schoolClassId, FundStatus status, Pageable pageable) {
        log.debug("Enter getSchoolClassAllFunds(schoolClassId={}, status={}, pageable={})", schoolClassId, status, pageable);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        SchoolClass schoolClass = schoolClassFinder.getByIdOrThrow(schoolClassId);
        schoolClassAccessService.assertCanViewSchoolClass(parent, schoolClass);

        Page<Fund> fundPage;

        if (status != null) {
            fundPage = fundRepository.findAllBySchoolClass_SchoolClassIdAndFundStatus(schoolClassId, status, pageable);
        } else {
            fundPage = fundRepository.findAllBySchoolClass_SchoolClassId(schoolClassId, pageable);
        }

        Page<FundWithChildrenResponseDto> fundWithChildrenResponseDtoPage = fundPage.map(fundMapper::toDtoWithChildren);

        List<Child> parentChildren = childRepository.findAllBySchoolClass_SchoolClassIdAndParent_UserId(schoolClassId, userId);

        for (FundWithChildrenResponseDto fundWithChildrenResponseDto : fundWithChildrenResponseDtoPage.getContent()) {
            updateFundInformation(userId, parentChildren, fundWithChildrenResponseDto);
        }

        log.debug("Exit getSchoolClassAllFunds(schoolClassId={}, status={}, pageable={})", schoolClassId, status, pageable);
        return fundWithChildrenResponseDtoPage;
    }

    @Transactional(readOnly = true)
    public Page<FundWithChildrenResponseDto> getParentChildrenAllFunds(Pageable pageable) {
        log.debug("Enter getParentChildrenAllFunds(pageable={})", pageable);

        UUID userId = securityUtils.getCurrentUserId();
        parentFinder.assertParentExists(userId);

        Page<Fund> parentChildrenFundPage = fundRepository.findAllByParentId(userId, pageable);

        Page<FundWithChildrenResponseDto> fundWithChildrenResponseDtoPage = parentChildrenFundPage.map(fundMapper::toDtoWithChildren);

        List<Child> parentChildren = childRepository.findAllByParent_UserId(userId);

        for (FundWithChildrenResponseDto fundWithChildrenResponseDto : fundWithChildrenResponseDtoPage.getContent()) {
            updateFundInformation(userId, parentChildren, fundWithChildrenResponseDto);
        }

        log.debug("Exit getParentChildrenAllFunds(pageable={})", pageable);
        return fundWithChildrenResponseDtoPage;
    }

    private void updateFundInformation(UUID parentId, List<Child> parentChildren, FundWithChildrenResponseDto fundWithChildrenResponseDto) {
        log.debug("Enter updateFundInformation(parentId={}, fundId={})", parentId, fundWithChildrenResponseDto.getFundId());

        UUID fundId = fundWithChildrenResponseDto.getFundId();

        List<FundChildStatusWithoutParentResponseDto> fundChildStatusWithoutParentResponseDtoList = getFundParentChildrenStatuses(fundId, parentId, parentChildren);
        fundWithChildrenResponseDto.setChildren(fundChildStatusWithoutParentResponseDtoList);
        fundWithChildrenResponseDto.setFundProgress(countFundProgress(fundId));
        fundWithChildrenResponseDto.setFundCurrentBalanceInCents(fundOperationFinder.getFundCurrentBalanceInCents(fundId));
    }

    public FundProgressResponseDto countFundProgress(UUID fundId) {
        log.debug("Enter countFundProgress(fundId={})", fundId);

        Fund fund = fundFinder.getByIdOrThrow(fundId);

        Page<FundChildStatusResponseDto> fundChildStatusResponseDtoPage = getFundChildrenStatuses(fundId, Pageable.unpaged());

        long paidChildrenCount = fundChildStatusResponseDtoPage.getContent().stream()
                .filter(dto -> dto.getStatus() == FundChildStatus.PAID)
                .count();
        long unpaidChildrenCount = fundChildStatusResponseDtoPage.getContent().stream()
                .filter(dto -> dto.getStatus() == FundChildStatus.UNPAID)
                .count();
        long ignoredChildrenCount = fundChildStatusResponseDtoPage.getContent().stream()
                .filter(dto -> dto.getStatus() == FundChildStatus.DECLINED)
                .count();
        long participatingChildrenCount = fundChildStatusResponseDtoPage.getContent().stream()
                .filter(dto -> dto.getStatus() != FundChildStatus.DECLINED)
                .count();

        long amountPerChild = fund.getAmountPerChildInCents();

        long currentAmountInCents = paidChildrenCount * amountPerChild;
        long targetAmountInCents = participatingChildrenCount * amountPerChild;
        long remainingAmountInCents = unpaidChildrenCount * amountPerChild;

        double progressPercentage = participatingChildrenCount == 0
                ? 0.0
                : (100.0 * paidChildrenCount) / participatingChildrenCount;

        log.debug("Exit countFundProgress(fundId={})", fundId);
        return FundProgressResponseDto.builder()
                .currentAmountInCents(currentAmountInCents)
                .targetAmountInCents(targetAmountInCents)
                .remainingAmountInCents(remainingAmountInCents)
                .progressPercentage(progressPercentage)
                .ignoredChildrenCount(ignoredChildrenCount)
                .participatingChildrenCount(participatingChildrenCount)
                .paidChildrenCount(paidChildrenCount)
                .unpaidChildrenCount(unpaidChildrenCount)
                .progressPercentage(progressPercentage)
                .build();
    }

    public List<FundChildStatusWithoutParentResponseDto> getFundParentChildrenStatuses(UUID fundId, UUID parentId, List<Child> parentChildren) {
        log.debug("Enter getFundParentChildrenStatuses(fundId={}, parentId={})", fundId, parentId);

        Set<UUID> fundParentIgnoredChildrenIds = childIgnoredFundRepository.findParentFundIgnoredChildrenIds(fundId, parentId);
        Set<UUID> fundParentPaidChildrenIds = fundOperationRepository.findFundParentPaidChildrenIds(fundId, parentId, FinancialOperationStatus.SUCCESS);

        List<FundChildStatusWithoutParentResponseDto> fundChildStatusWithoutParentResponseDtoList = parentChildren.stream()
                .map(child -> {
                    FundChildStatus fundChildStatus = resolveChildStatus(child.getChildId(), fundParentIgnoredChildrenIds, fundParentPaidChildrenIds);
                    return toStatusWithoutParentResponseDto(child, fundChildStatus);
                })
                .toList();

        log.debug("Exit getFundParentChildrenStatuses(fundId={}, parentId={})", fundId, parentId);
        return fundChildStatusWithoutParentResponseDtoList;
    }

    public Page<FundChildStatusResponseDto> getFundChildrenStatuses(UUID fundId, Pageable pageable) {
        log.debug("Enter getFundChildrenStatuses(fundId={}, pageable={})", fundId, pageable);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Fund fund = fundFinder.getByIdOrThrow(fundId);
        fundAccessService.assertCanViewFund(parent, fund);

        Page<Child> childrenPage = childRepository.findAllBySchoolClass_SchoolClassId(fund.getSchoolClass().getSchoolClassId(), pageable);

        Set<UUID> fundAllIgnoredChildrenIds = childIgnoredFundRepository.findFundAllIgnoredChildrenIds(fundId);
        Set<UUID> fundAllPaidChildrenIds = fundOperationRepository.findFundAllPaidChildrenIds(fundId, FinancialOperationStatus.SUCCESS);

        List<FundChildStatusResponseDto> fundChildStatusResponseDtoList = childrenPage.getContent().stream()
                .map(child -> {
                    FundChildStatus fundChildStatus = resolveChildStatus(child.getChildId(), fundAllIgnoredChildrenIds, fundAllPaidChildrenIds);
                    return toStatusResponseDto(child, fundChildStatus);
                })
                .toList();

        log.debug("Exit getFundChildrenStatuses(fundId={}, pageable={})", fundId, pageable);
        return new PageImpl<>(fundChildStatusResponseDtoList, pageable, childrenPage.getTotalElements());
    }

    private FundChildStatus resolveChildStatus(UUID childId, Set<UUID> ignoredIds, Set<UUID> paidIds) {
        if (ignoredIds.contains(childId)) {
            return FundChildStatus.DECLINED;
        }
        if (paidIds.contains(childId)) {
            return FundChildStatus.PAID;
        }
        return FundChildStatus.UNPAID;
    }

    private FundChildStatusResponseDto toStatusResponseDto(Child child, FundChildStatus status) {
        return FundChildStatusResponseDto.builder()
                .child(childMapper.toWithParentInfoDto(child))
                .status(status)
                .build();
    }

    private FundChildStatusWithoutParentResponseDto toStatusWithoutParentResponseDto(Child child, FundChildStatus status) {
        return FundChildStatusWithoutParentResponseDto.builder()
                .child(childMapper.toShortInfoDto(child))
                .status(status)
                .build();
    }

}
