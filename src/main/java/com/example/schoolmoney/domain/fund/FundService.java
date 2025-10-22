package com.example.schoolmoney.domain.fund;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.FundMessages;
import com.example.schoolmoney.common.constants.messages.SchoolClassMessages;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.fund.dto.FundMapper;
import com.example.schoolmoney.domain.fund.dto.request.CreateFundRequestDto;
import com.example.schoolmoney.domain.fund.dto.request.UpdateFundRequestDto;
import com.example.schoolmoney.domain.fund.dto.response.FundResponseDto;
import com.example.schoolmoney.domain.fundoperation.FundOperation;
import com.example.schoolmoney.domain.fundoperation.FundOperationRepository;
import com.example.schoolmoney.domain.fundoperation.FundOperationType;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.domain.schoolclass.SchoolClassRepository;
import com.example.schoolmoney.domain.wallet.Wallet;
import com.example.schoolmoney.domain.wallet.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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

    @Transactional
    public FundResponseDto createFund(CreateFundRequestDto createFundRequestDto) throws EntityNotFoundException, AccessDeniedException {
        log.debug("enter createFund {}", createFundRequestDto);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        SchoolClass schoolClass = schoolClassRepository.findById(createFundRequestDto.getSchoolClassId())
                .orElseThrow(() -> {
                    log.error(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                    return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                });

        if (!schoolClass.getTreasurer().getUserId().equals(userId)) {
            log.warn(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
        }

        Fund fund = Fund
                .builder()
                .author(parent)
                .schoolClass(schoolClass)
                .amountPerChildInCents(createFundRequestDto.getAmountPerChildInCents())
                .title(createFundRequestDto.getTitle())
                .description(createFundRequestDto.getDescription())
                .endsAt(createFundRequestDto.getEndsAt())
                .iban(createFundRequestDto.getIban())
                .build();

        fundRepository.save(fund);
        log.info("fund saved {}", fund);

        log.debug("exit createFund");

        return fundMapper.toDto(fund);
    }

    @Transactional
    public void cancelFund(UUID fundId) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("enter cancelFund {}", fundId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.error(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        if (!fund.getFundStatus().equals(FundStatus.ACTIVE)) {
            log.error(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalStateException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        UUID userId = securityUtils.getCurrentUserId();

        SchoolClass schoolClass = fund.getSchoolClass();

        if (!schoolClass.getTreasurer().getUserId().equals(userId)) {
            log.error(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
        }

        List<FundOperation> fundOperations = fundOperationRepository.findAllByFund_FundId(fundId);

        if (!fundOperations.isEmpty()) {
            long fundTreasurerBalance = calculateFundTreasurerBalance(fundOperations);

            if (fundTreasurerBalance < 0) {
                log.debug("Fund treasurer balance is negative: {}", fundTreasurerBalance);
                throw new IllegalStateException(FundMessages.CANNOT_CANCEL_FUND_BECAUSE_OF_MISSING_FUNDS);
            } else if (fundTreasurerBalance > 0) {
                log.debug("Fund treasurer balance is positive: {}", fundTreasurerBalance);
                throw new IllegalStateException(FundMessages.CANNOT_CANCEL_FUND_BECAUSE_OF_REMAINING_TREASURER_DEPOSITS);
            }
        }

        fund.setFundStatus(FundStatus.CANCELLED);
        fund.setEndedAt(Instant.now());
        fundRepository.save(fund);
        log.info("Fund cancelled {}", fund);

        processParentRefunds(fundOperations);

        log.debug("exit cancelFund");
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

                parentWallet.setBalanceInCents(parentWallet.getBalanceInCents() + fundOperation.getAmountInCents());
                walletRepository.save(parentWallet);
                log.info("Parent wallet updated {}", parentWallet);

                FundOperation parentRefundOperation = FundOperation
                        .builder()
                        .parent(fundOperation.getParent())
                        .child(fundOperation.getChild())
                        .fund(fundOperation.getFund())
                        .wallet(parentWallet)
                        .amountInCents(fundOperation.getAmountInCents())
                        .operationType(FundOperationType.FUND_REFUND)
                        .operationStatus(FinancialOperationStatus.SUCCESS)
                        .build();

                fundOperationRepository.save(parentRefundOperation);
                log.info("Parent refund operation saved {}", parentRefundOperation);
            }
        }
    }

    @Transactional
    public void expireEndedFunds() {
        List<Fund> endedFunds = fundRepository.findAllByEndsAtBeforeAndFundStatus(Instant.now(), FundStatus.ACTIVE);

        for (Fund fund : endedFunds) {
            fund.setFundStatus(FundStatus.FINISHED);
            fund.setEndedAt(Instant.now());
        }

        fundRepository.saveAll(endedFunds);
    }

    public Page<FundResponseDto> getCreatedFunds(Pageable pageable) {
        log.debug("Enter getCreatedFunds(pageable={})", pageable);

        UUID userId = securityUtils.getCurrentUserId();

        Page<Fund> fundPage = fundRepository.findAllByAuthor_UserId(userId, pageable);

        log.debug("Exit getCreatedFunds");
        return fundPage.map(fundMapper::toDto);
    }

    @Transactional
    public FundResponseDto updateFund(UUID fundId, UpdateFundRequestDto updateFundRequestDto) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("enter updateFund {}, {}", fundId, updateFundRequestDto);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.error(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        if (!fund.getFundStatus().equals(FundStatus.ACTIVE)) {
            log.error(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalStateException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        UUID userId = securityUtils.getCurrentUserId();

        SchoolClass schoolClass = fund.getSchoolClass();

        if (!schoolClass.getTreasurer().getUserId().equals(userId)) {
            log.warn(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
        }

        fundMapper.updateEntityFromDto(updateFundRequestDto, fund);
        Fund savedFund = fundRepository.save(fund);
        log.info("fund saved {}", fund);

        log.debug("exit updateFund");
        return fundMapper.toDto(savedFund);
    }

    public Page<FundResponseDto> getSchoolClassAllFunds(UUID schoolClassId, Pageable pageable) throws EntityNotFoundException, AccessDeniedException {
        log.debug("Enter getSchoolClassAllFunds(schoolClassId={}, pageable={})", schoolClassId, pageable);

        UUID userId = securityUtils.getCurrentUserId();

        SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> {
                    log.error(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                    return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                });

        boolean hasAnyChildrenInSchoolClass = childRepository.existsByParent_UserIdAndSchoolClass_SchoolClassId(userId, schoolClassId);
        boolean isTreasurer = schoolClass.getTreasurer().getUserId().equals(userId);

        if (!hasAnyChildrenInSchoolClass && !isTreasurer) {
            log.error(SchoolClassMessages.PARENT_DOES_NOT_HAVE_ACCESS_TO_THIS_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.PARENT_DOES_NOT_HAVE_ACCESS_TO_THIS_CLASS);
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

}
