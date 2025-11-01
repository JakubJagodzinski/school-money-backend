package com.example.schoolmoney.domain.fund;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.FundMessages;
import com.example.schoolmoney.common.constants.messages.domain.SchoolClassMessages;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.child.dto.ChildMapper;
import com.example.schoolmoney.domain.childignoredfund.ChildIgnoredFundRepository;
import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.fund.dto.FundMapper;
import com.example.schoolmoney.domain.fund.dto.request.CreateFundRequestDto;
import com.example.schoolmoney.domain.fund.dto.request.UpdateFundRequestDto;
import com.example.schoolmoney.domain.fund.dto.response.FundChildStatusResponseDto;
import com.example.schoolmoney.domain.fund.dto.response.FundResponseDto;
import com.example.schoolmoney.domain.fundoperation.FundOperation;
import com.example.schoolmoney.domain.fundoperation.FundOperationRepository;
import com.example.schoolmoney.domain.fundoperation.FundOperationType;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.domain.schoolclass.SchoolClassRepository;
import com.example.schoolmoney.domain.schoolclass.SchoolClassService;
import com.example.schoolmoney.domain.wallet.Wallet;
import com.example.schoolmoney.domain.wallet.WalletRepository;
import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.finance.FinanceConfiguration;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundService {

    private final FundMapper fundMapper;

    private final FundRepository fundRepository;

    private final ParentRepository parentRepository;

    private final SchoolClassRepository schoolClassRepository;

    private final FundOperationRepository fundOperationRepository;

    private final WalletRepository walletRepository;

    private final ChildRepository childRepository;

    private final SecurityUtils securityUtils;

    private final EmailService emailService;

    private final FinanceConfiguration financeConfiguration;

    private final ChildIgnoredFundRepository childIgnoredFundRepository;

    private final ChildMapper childMapper;

    private final SchoolClassService schoolClassService;

    public boolean canParentAccessFund(UUID parentId, UUID fundId) throws EntityNotFoundException {
        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        boolean hasChildInSchoolClass = childRepository.existsByParent_UserIdAndSchoolClass_SchoolClassId(parentId, fund.getSchoolClass().getSchoolClassId());
        boolean isSchoolClassTreasurer = fund.getSchoolClass().getTreasurer() != null && fund.getSchoolClass().getTreasurer().getUserId().equals(parentId);
        boolean isFundAuthor = fund.getAuthor().getUserId().equals(parentId);

        return hasChildInSchoolClass || isSchoolClassTreasurer || isFundAuthor;
    }

    @Transactional
    public FundResponseDto createFund(CreateFundRequestDto createFundRequestDto) throws EntityNotFoundException, AccessDeniedException {
        log.debug("Enter createFund(createFundRequestDto={})", createFundRequestDto);

        UUID userId = securityUtils.getCurrentUserId();


        SchoolClass schoolClass = schoolClassRepository.findById(createFundRequestDto.getSchoolClassId())
                .orElseThrow(() -> {
                    log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                    return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                });

        if (!schoolClassService.canParentAccessSchoolClass(userId, schoolClass.getSchoolClassId())) {
            log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
            throw new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
        }

        if (!schoolClass.getTreasurer().getUserId().equals(userId)) {
            log.warn(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
        }

        Parent fundAuthor = parentRepository.getReferenceById(userId);
        Fund fund = Fund
                .builder()
                .author(fundAuthor)
                .schoolClass(schoolClass)
                .amountPerChildInCents(createFundRequestDto.getAmountPerChildInCents())
                .currency(financeConfiguration.getCurrency())
                .title(createFundRequestDto.getTitle())
                .description(createFundRequestDto.getDescription())
                .endsAt(createFundRequestDto.getEndsAt())
                .iban(createFundRequestDto.getIban())
                .build();

        fundRepository.save(fund);
        log.info("Fund saved {}", fund);

        List<Parent> schoolClassParents = childRepository.findSchoolClassDistinctParents(schoolClass.getSchoolClassId());
        log.debug("Number of parents in school class {}", schoolClassParents.size());

        log.debug("Sending emails to parents in school class");
        for (Parent parent : schoolClassParents) {
            emailService.sendFundCreatedEmail(
                    parent.getEmail(),
                    parent.getFirstName(),
                    fundAuthor.getFullName(),
                    fund.getTitle(),
                    schoolClass.getFullName(),
                    parent.isNotificationsEnabled()
            );
        }
        log.debug("Emails sent");

        log.debug("Exit createFund");
        return fundMapper.toDto(fund);
    }

    @Transactional
    public void cancelFund(UUID fundId) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter cancelFund(fundId={})", fundId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        if (!canParentAccessFund(securityUtils.getCurrentUserId(), fundId)) {
            log.warn(FundMessages.FUND_NOT_FOUND);
            throw new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
        }

        if (!fund.getSchoolClass().getTreasurer().getUserId().equals(userId)) {
            log.warn(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
        }

        if (fund.getFundStatus().equals(FundStatus.BLOCKED)) {
            log.warn(FundMessages.FUND_IS_BLOCKED);
            throw new IllegalStateException(FundMessages.FUND_IS_BLOCKED);
        }

        if (!fund.getFundStatus().equals(FundStatus.ACTIVE)) {
            log.warn(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalStateException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        List<FundOperation> fundOperations = fundOperationRepository.findAllByFund_FundId(fundId);

        if (!fundOperations.isEmpty()) {
            long fundTreasurerBalance = calculateFundTreasurerBalance(fundOperations);

            if (fundTreasurerBalance < 0) {
                log.warn(FundMessages.CANNOT_CANCEL_FUND_BECAUSE_OF_MISSING_FUNDS);
                throw new IllegalStateException(FundMessages.CANNOT_CANCEL_FUND_BECAUSE_OF_MISSING_FUNDS);
            } else if (fundTreasurerBalance > 0) {
                log.warn(FundMessages.CANNOT_CANCEL_FUND_BECAUSE_OF_REMAINING_TREASURER_DEPOSITS);
                throw new IllegalStateException(FundMessages.CANNOT_CANCEL_FUND_BECAUSE_OF_REMAINING_TREASURER_DEPOSITS);
            }
        }

        fund.setFundStatus(FundStatus.CANCELLED);
        fund.setEndedAt(Instant.now());
        fundRepository.save(fund);
        log.info("Fund cancelled {}", fund);

        List<Parent> parents = childRepository.findSchoolClassDistinctParents(fund.getSchoolClass().getSchoolClassId());

        log.debug("Sending emails about cancelled fund to parents in school class");
        for (Parent parent : parents) {
            // TODO check if parent has any child in that class that isn't ignoring the fund
            emailService.sendFundCancelledEmail(
                    parent.getEmail(),
                    parent.getFirstName(),
                    fund.getTitle(),
                    fund.getSchoolClass().getFullName(),
                    parent.isNotificationsEnabled()
            );
        }
        log.debug("Emails sent");

        processParentRefunds(fundOperations);

        log.debug("Exit cancelFund");
    }

    private long calculateFundTreasurerBalance(List<FundOperation> fundOperations) {
        long fundTreasurerBalance = 0;

        for (FundOperation fundOperation : fundOperations) {
            FundOperationType fundOperationType = fundOperation.getOperationType();
            if (fundOperationType.equals(FundOperationType.FUND_DEPOSIT)) {
                fundTreasurerBalance += fundOperation.getAmountInCents();
            } else if (fundOperationType.equals(FundOperationType.FUND_WITHDRAWAL)) {
                fundTreasurerBalance -= fundOperation.getAmountInCents();
            }
        }

        return fundTreasurerBalance;
    }

    private void processParentRefunds(List<FundOperation> fundOperations) {
        for (FundOperation fundOperation : fundOperations) {
            if (fundOperation.getOperationType().equals(FundOperationType.FUND_PAYMENT) && fundOperation.getAmountInCents() > 0) {
                log.debug("Processing refund for fund operation {}", fundOperation);

                Wallet parentWallet = fundOperation.getWallet();

                parentWallet.increaseBalanceInCents(fundOperation.getAmountInCents());
                walletRepository.save(parentWallet);
                log.info("Parent wallet balance updated {}", parentWallet);

                FundOperation parentRefundOperation = FundOperation
                        .builder()
                        .parent(fundOperation.getParent())
                        .child(fundOperation.getChild())
                        .fund(fundOperation.getFund())
                        .wallet(parentWallet)
                        .amountInCents(fundOperation.getAmountInCents())
                        .currency(fundOperation.getCurrency())
                        .operationType(FundOperationType.FUND_REFUND)
                        .operationStatus(FinancialOperationStatus.SUCCESS)
                        .build();

                fundOperationRepository.save(parentRefundOperation);
                log.info("Parent refund operation saved {}", parentRefundOperation);

                Parent parent = parentWallet.getParent();

                emailService.sendFundPaymentRefundEmail(
                        parent.getEmail(),
                        parent.getFirstName(),
                        parentRefundOperation.getFund().getTitle(),
                        parentRefundOperation.getFund().getSchoolClass().getFullName(),
                        parentRefundOperation.getChild().getFullName(),
                        parentRefundOperation.getAmountInCents(),
                        parentRefundOperation.getCurrency(),
                        parent.isNotificationsEnabled()
                );
            }
        }
    }

    @Transactional
    public void markEndedFundsAsFinished() {
        log.debug("Enter markEndedFundsAsFinished");

        List<Fund> endedFunds = fundRepository.findAllByEndsAtBeforeAndFundStatus(Instant.now(), FundStatus.ACTIVE);

        for (Fund fund : endedFunds) {
            fund.setFundStatus(FundStatus.FINISHED);
            fund.setEndedAt(Instant.now());
            log.info("Finished fund with fundId={}", fund);

            List<Parent> parents = childRepository.findSchoolClassDistinctParents(fund.getSchoolClass().getSchoolClassId());

            log.debug("Sending emails about finished fund to parents in school class");
            for (Parent parent : parents) {
                emailService.sendFundFinishedEmail(
                        parent.getEmail(),
                        parent.getFirstName(),
                        fund.getTitle(),
                        fund.getSchoolClass().getFullName(),
                        parent.isNotificationsEnabled()
                );
            }
            log.debug("Emails sent");
        }

        fundRepository.saveAll(endedFunds);
        log.debug("Exit markEndedFundsAsFinished");
    }

    public Page<FundResponseDto> getParentCreatedFunds(Pageable pageable) {
        log.debug("Enter getParentCreatedFunds(pageable={})", pageable);

        UUID userId = securityUtils.getCurrentUserId();

        Page<Fund> fundPage = fundRepository.findAllByAuthor_UserId(userId, pageable);

        log.debug("Exit getCreatedFunds");
        return fundPage.map(fundMapper::toDto);
    }

    @Transactional
    public FundResponseDto updateFund(UUID fundId, UpdateFundRequestDto updateFundRequestDto) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter updateFund(fundId={}, updateFundRequestDto={})", fundId, updateFundRequestDto);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        if (!canParentAccessFund(userId, fundId)) {
            log.warn(FundMessages.FUND_NOT_FOUND);
            throw new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
        }

        if (!fund.getSchoolClass().getTreasurer().getUserId().equals(userId)) {
            log.warn(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
        }

        if (fund.getFundStatus().equals(FundStatus.BLOCKED)) {
            log.warn(FundMessages.FUND_IS_BLOCKED);
            throw new IllegalStateException(FundMessages.FUND_IS_BLOCKED);
        }

        if (!fund.getFundStatus().equals(FundStatus.ACTIVE)) {
            log.warn(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalStateException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        fundMapper.updateEntityFromDto(updateFundRequestDto, fund);
        Fund savedFund = fundRepository.save(fund);
        log.info("Fund saved {}", fund);

        log.debug("Exit getParentCreatedFunds");
        return fundMapper.toDto(savedFund);
    }

    public Page<FundResponseDto> getSchoolClassAllFunds(UUID schoolClassId, Pageable pageable) throws EntityNotFoundException {
        log.debug("Enter getSchoolClassAllFunds(schoolClassId={}, pageable={})", schoolClassId, pageable);

        if (!schoolClassRepository.existsById(schoolClassId)) {
            log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
            throw new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
        }

        UUID userId = securityUtils.getCurrentUserId();
        if (!schoolClassService.canParentAccessSchoolClass(userId, schoolClassId)) {
            log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
            throw new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
        }

        Page<Fund> fundPage = fundRepository.findAllBySchoolClass_SchoolClassId(schoolClassId, pageable);

        log.debug("Exit getSchoolClassAllFunds");
        return fundPage.map(fundMapper::toDto);
    }

    public Page<FundResponseDto> getParentChildrenAllFunds(Pageable pageable) {
        log.debug("Enter getParentChildrenAllFunds(pageable={})", pageable);

        UUID userId = securityUtils.getCurrentUserId();

        Page<Fund> parentChildrenFundPage = fundRepository.findAllByParentId(userId, pageable);

        log.debug("Exit getParentChildrenAllFunds");
        return parentChildrenFundPage.map(fundMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<FundChildStatusResponseDto> getFundChildrenStatuses(UUID fundId, Pageable pageable) throws EntityNotFoundException {
        log.debug("Enter getFundChildrenStatuses(fundId={}, pageable={})", fundId, pageable);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();

        if (!canParentAccessFund(userId, fundId)) {
            log.warn(FundMessages.FUND_NOT_FOUND);
            throw new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
        }

        Page<Child> childrenPage = childRepository.findAllBySchoolClass_SchoolClassId(fund.getSchoolClass().getSchoolClassId(), pageable);

        Set<UUID> ignoredChildrenIds = getIgnoredChildIds(fundId);
        log.debug("Ignored children ids count: {}", ignoredChildrenIds.size());
        Set<UUID> paidChildrenIds = getPaidChildIds(fundId);
        log.debug("Paid children ids count: {}", paidChildrenIds.size());

        List<FundChildStatusResponseDto> fundChildStatusResponseDtoList = childrenPage.getContent().stream()
                .map(child -> {
                    FundChildStatus fundChildStatus = resolveChildStatus(child.getChildId(), ignoredChildrenIds, paidChildrenIds);
                    return toStatusResponseDto(child, fundChildStatus);
                })
                .toList();

        log.debug("Exit getFundChildrenStatuses");
        return new PageImpl<>(fundChildStatusResponseDtoList, pageable, childrenPage.getTotalElements());
    }

    private Set<UUID> getIgnoredChildIds(UUID fundId) {
        return childIgnoredFundRepository.findAllByFund_FundId(fundId).stream()
                .map(c -> c.getChild().getChildId())
                .collect(Collectors.toSet());
    }

    private Set<UUID> getPaidChildIds(UUID fundId) {
        return fundOperationRepository.findAllByFund_FundId(fundId).stream()
                .map(f -> f.getChild().getChildId())
                .collect(Collectors.toSet());
    }

    private FundChildStatus resolveChildStatus(UUID childId, Set<UUID> ignoredIds, Set<UUID> paidIds) {
        if (ignoredIds.contains(childId)) return FundChildStatus.DECLINED;
        if (paidIds.contains(childId)) return FundChildStatus.PAID;
        return FundChildStatus.UNPAID;
    }

    private FundChildStatusResponseDto toStatusResponseDto(Child child, FundChildStatus status) {
        return FundChildStatusResponseDto.builder()
                .child(childMapper.toWithParentInfoDto(child))
                .status(status)
                .build();
    }

}
