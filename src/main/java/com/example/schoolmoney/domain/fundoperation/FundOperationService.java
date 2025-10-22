package com.example.schoolmoney.domain.fundoperation;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.*;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fund.FundStatus;
import com.example.schoolmoney.domain.wallet.Wallet;
import com.example.schoolmoney.domain.wallet.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundOperationService {

    private final FundOperationRepository fundOperationRepository;

    private final FundRepository fundRepository;

    private final WalletRepository walletRepository;

    private final ChildRepository childRepository;

    private final SecurityUtils securityUtils;

    @Transactional
    public void performPayment(UUID fundId, UUID childId, long amountInCents) throws EntityNotFoundException, IllegalArgumentException, IllegalStateException, AccessDeniedException {
        log.debug("Enter performPayment(fundId={}, childId={}, amountInCents={})", fundId, childId, amountInCents);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

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

        if (fundOperationRepository.existsByFund_FundIdAndParent_UserIdAndChild_ChildIdAndOperationTypeAndOperationStatus(
                fundId, userId, childId, FundOperationType.FUND_PAYMENT, FinancialOperationStatus.SUCCESS
        )) {
            log.warn(FundOperationMessages.PAYMENT_ALREADY_MADE_FOR_THIS_CHILD);
            throw new IllegalStateException(FundOperationMessages.PAYMENT_ALREADY_MADE_FOR_THIS_CHILD);
        }

        if (amountInCents < 0) {
            log.warn(FundOperationMessages.PAYMENT_AMOUNT_MUST_BE_GREATER_THAN_ZERO);
            throw new IllegalArgumentException(FundOperationMessages.PAYMENT_AMOUNT_MUST_BE_GREATER_THAN_ZERO);
        }

        if (amountInCents != fund.getAmountPerChildInCents()) {
            log.warn(FundOperationMessages.PAYMENT_AMOUNT_MUST_MATCH_FUND_AMOUNT);
            throw new IllegalArgumentException(FundOperationMessages.PAYMENT_AMOUNT_MUST_MATCH_FUND_AMOUNT);
        }

        Wallet parentWallet = walletRepository.findByParent_UserId(userId);

        if (parentWallet.getBalanceInCents() < amountInCents) {
            log.warn(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
            throw new IllegalStateException(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
        }

        parentWallet.setBalanceInCents(parentWallet.getBalanceInCents() - amountInCents);
        walletRepository.save(parentWallet);
        log.info("Wallet updated {}", parentWallet);

        FundOperation fundOperation = FundOperation
                .builder()
                .parent(child.getParent())
                .child(child)
                .fund(fund)
                .wallet(parentWallet)
                .amountInCents(amountInCents)
                .operationType(FundOperationType.FUND_PAYMENT)
                .operationStatus(FinancialOperationStatus.SUCCESS)
                .build();

        fundOperationRepository.save(fundOperation);
        log.info("Fund operation saved {}", fundOperation);

        log.debug("Exit performPayment");
    }

    @Transactional
    public void depositToFund(UUID fundId, long amountInCents) throws EntityNotFoundException, IllegalStateException, IllegalArgumentException, AccessDeniedException {
        log.debug("Enter depositToFund(fundId={}, amountInCents={})", fundId, amountInCents);

        UUID userId = securityUtils.getCurrentUserId();

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        if (!fund.getSchoolClass().getTreasurer().getUserId().equals(userId)) {
            log.warn(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
        }

        if (!fund.getFundStatus().equals(FundStatus.ACTIVE)) {
            log.warn(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalStateException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        if (amountInCents < 0) {
            log.warn(FundOperationMessages.DEPOSIT_AMOUNT_MUST_BE_GREATER_THAN_ZERO);
            throw new IllegalArgumentException(FundOperationMessages.DEPOSIT_AMOUNT_MUST_BE_GREATER_THAN_ZERO);
        }

        long remainingDepositLimitInCents = getFundRemainingDepositLimitInCents(fundId);

        if (amountInCents > remainingDepositLimitInCents) {
            log.warn(FundMessages.CANNOT_DEPOSIT_MORE_THAN_WITHDRAWN_AMOUNT);
            throw new IllegalStateException(FundMessages.CANNOT_DEPOSIT_MORE_THAN_WITHDRAWN_AMOUNT);
        }

        Wallet treasurerWallet = walletRepository.findByParent_UserId(userId);

        treasurerWallet.setBalanceInCents(treasurerWallet.getBalanceInCents() - amountInCents);
        walletRepository.save(treasurerWallet);
        log.info("Wallet updated {}", treasurerWallet);

        FundOperation fundDepositOperation = FundOperation
                .builder()
                .parent(fund.getSchoolClass().getTreasurer())
                .fund(fund)
                .wallet(treasurerWallet)
                .amountInCents(amountInCents)
                .operationType(FundOperationType.FUND_DEPOSIT)
                .operationStatus(FinancialOperationStatus.SUCCESS)
                .build();

        fundOperationRepository.save(fundDepositOperation);
        log.info("Fund operation saved {}", fundDepositOperation);

        log.debug("Exit depositToFund");
    }

    @Transactional
    public void withdrawFromFund(UUID fundId, long amountInCents) throws EntityNotFoundException, IllegalArgumentException, IllegalStateException, AccessDeniedException {
        log.debug("Enter withdrawFromFund(fundId={}, amountInCents={})", fundId, amountInCents);

        UUID userId = securityUtils.getCurrentUserId();

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        if (!fund.getSchoolClass().getTreasurer().getUserId().equals(userId)) {
            log.warn(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
            throw new AccessDeniedException(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
        }

        if (!fund.getFundStatus().equals(FundStatus.ACTIVE)) {
            log.warn(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalStateException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        if (amountInCents < 0) {
            log.warn(FundOperationMessages.WITHDRAWAL_AMOUNT_MUST_BE_GREATER_THAN_ZERO);
            throw new IllegalArgumentException(FundOperationMessages.WITHDRAWAL_AMOUNT_MUST_BE_GREATER_THAN_ZERO);
        }

        long fundActualAmountInCents = getFundActualAmountInCents(fundId);

        if (fundActualAmountInCents < amountInCents) {
            log.warn(FundMessages.NOT_ENOUGH_BALANCE_IN_FUND);
            throw new IllegalStateException(FundMessages.NOT_ENOUGH_BALANCE_IN_FUND);
        }

        Wallet treasurerWallet = walletRepository.findByParent_UserId(userId);

        treasurerWallet.setBalanceInCents(treasurerWallet.getBalanceInCents() + amountInCents);
        walletRepository.save(treasurerWallet);
        log.info("wallet updated {}", treasurerWallet);

        FundOperation fundWithdrawalOperation = FundOperation
                .builder()
                .parent(fund.getSchoolClass().getTreasurer())
                .fund(fund)
                .wallet(treasurerWallet)
                .amountInCents(amountInCents)
                .operationType(FundOperationType.FUND_WITHDRAWAL)
                .operationStatus(FinancialOperationStatus.SUCCESS)
                .build();

        fundOperationRepository.save(fundWithdrawalOperation);
        log.info("Fund operation saved {}", fundWithdrawalOperation);

        log.debug("Exit withdrawFromFund");
    }

    public long getFundActualAmountInCents(UUID fundId) {
        List<FundOperation> fundOperations = fundOperationRepository.findAllByFund_FundId(fundId);

        long fundActualAmountInCents = 0;

        for (FundOperation fundOperation : fundOperations) {
            if (fundOperation.getOperationStatus().equals(FinancialOperationStatus.SUCCESS)) {
                FundOperationType fundOperationType = fundOperation.getOperationType();
                switch (fundOperationType) {
                    case FUND_PAYMENT:
                    case FUND_DEPOSIT:
                        fundActualAmountInCents += fundOperation.getAmountInCents();
                        break;
                    case FUND_WITHDRAWAL:
                    case FUND_REFUND:
                        fundActualAmountInCents -= fundOperation.getAmountInCents();
                        break;
                    default:
                        break;
                }
            }
        }

        return fundActualAmountInCents;
    }

    public long getFundRemainingDepositLimitInCents(UUID fundId) {
        List<FundOperation> fundOperations = fundOperationRepository.findAllByFund_FundId(fundId);

        long remainingDepositLimitInCents = 0;

        for (FundOperation fundOperation : fundOperations) {
            if (fundOperation.getOperationStatus().equals(FinancialOperationStatus.SUCCESS)) {
                FundOperationType fundOperationType = fundOperation.getOperationType();
                if (fundOperationType.equals(FundOperationType.FUND_DEPOSIT)) {
                    remainingDepositLimitInCents -= fundOperation.getAmountInCents();
                } else if (fundOperationType.equals(FundOperationType.FUND_WITHDRAWAL)) {
                    remainingDepositLimitInCents += fundOperation.getAmountInCents();
                }
            }
        }

        return remainingDepositLimitInCents;
    }

}
