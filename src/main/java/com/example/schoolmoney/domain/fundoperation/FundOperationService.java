package com.example.schoolmoney.domain.fundoperation;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.*;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fund.FundStatus;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.wallet.Wallet;
import com.example.schoolmoney.domain.wallet.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void performPayment(UUID fundId, UUID childId, long amountInCents) throws EntityNotFoundException, IllegalArgumentException, IllegalStateException {
        log.debug("enter performPayment for fundId: {}, childId: {}, amountInCents: {}", fundId, childId, amountInCents);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.error(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.error(ChildMessages.CHILD_NOT_FOUND);
                    return new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();

        if (!child.getParent().getUserId().equals(userId)) {
            log.error(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
            throw new IllegalArgumentException(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
        }

        if (fundOperationRepository.existsByFund_FundIdAndParent_UserIdAndChild_ChildIdAndFundOperationTypeAndFundOperationStatus(
                fundId, userId, childId, FundOperationType.PAYMENT, FundOperationStatus.SUCCESS
        )) {
            log.error(FundOperationMessages.PAYMENT_ALREADY_MADE_FOR_THIS_CHILD);
            throw new IllegalArgumentException(FundOperationMessages.PAYMENT_ALREADY_MADE_FOR_THIS_CHILD);
        }

        if (amountInCents < 0) {
            log.error(FundOperationMessages.PAYMENT_AMOUNT_MUST_BE_GREATER_THAN_ZERO);
            throw new IllegalArgumentException(FundOperationMessages.PAYMENT_AMOUNT_MUST_BE_GREATER_THAN_ZERO);
        }

        if (amountInCents != fund.getAmountPerChildInCents()) {
            log.error(FundOperationMessages.PAYMENT_AMOUNT_MUST_MATCH_FUND_AMOUNT);
            throw new IllegalArgumentException(FundOperationMessages.PAYMENT_AMOUNT_MUST_MATCH_FUND_AMOUNT);
        }

        Wallet parentWallet = walletRepository.findByParent_UserId(userId);

        if (parentWallet.getBalanceInCents() < amountInCents) {
            log.error(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
            throw new IllegalStateException(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
        }

        FundOperation fundOperation = FundOperation
                .builder()
                .parent(child.getParent())
                .child(child)
                .fund(fund)
                .wallet(parentWallet)
                .amountInCents(amountInCents)
                .fundOperationType(FundOperationType.PAYMENT)
                .fundOperationStatus(FundOperationStatus.SUCCESS)
                .build();

        fundOperationRepository.save(fundOperation);
        log.info("fund operation saved {}", fundOperation);

        parentWallet.setBalanceInCents(parentWallet.getBalanceInCents() - amountInCents);
        walletRepository.save(parentWallet);
        log.info("wallet updated {}", parentWallet);

        log.debug("exit performPayment");
    }

    @Transactional
    public void depositFunds(UUID fundId, long amountInCents) throws EntityNotFoundException, IllegalArgumentException, IllegalStateException {
        UUID userId = securityUtils.getCurrentUserId();

        log.debug("enter depositFunds for fundId: {}, amountInCents: {}, userId: {}", fundId, amountInCents, userId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.error(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        if (fund.getSchoolClass().getTreasurer().getUserId().equals(userId)) {
            log.error(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
        }

        if (!fund.getFundStatus().equals(FundStatus.ACTIVE)) {
            log.error(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalArgumentException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        long remainingDepositLimitInCents = getFundRemainingDepositLimitInCents(fundId);

        if (amountInCents > remainingDepositLimitInCents) {
            log.error(FundMessages.CANNOT_DEPOSIT_MORE_THAN_WITHDRAWN_AMOUNT);
            throw new IllegalStateException(FundMessages.CANNOT_DEPOSIT_MORE_THAN_WITHDRAWN_AMOUNT);
        }

        Wallet treasurerWallet = walletRepository.findByParent_UserId(userId);

        Parent treasurer = fund.getSchoolClass().getTreasurer();

        FundOperation fundDepositOperation = FundOperation
                .builder()
                .parent(treasurer)
                .fund(fund)
                .wallet(treasurerWallet)
                .amountInCents(amountInCents)
                .fundOperationType(FundOperationType.DEPOSIT)
                .fundOperationStatus(FundOperationStatus.SUCCESS)
                .build();

        fundOperationRepository.save(fundDepositOperation);
        log.info("fund operation saved {}", fundDepositOperation);

        treasurerWallet.setBalanceInCents(treasurerWallet.getBalanceInCents() - amountInCents);
        walletRepository.save(treasurerWallet);
        log.info("wallet updated {}", treasurerWallet);

        log.debug("exit depositFunds");
    }

    @Transactional
    public void withdrawFunds(UUID fundId, long amountInCents) throws EntityNotFoundException, IllegalArgumentException, IllegalStateException {
        UUID userId = securityUtils.getCurrentUserId();

        log.debug("enter withdrawFunds for fundId: {}, amountInCents: {}, userId: {}", fundId, amountInCents, userId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.error(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        if (fund.getSchoolClass().getTreasurer().getUserId().equals(userId)) {
            log.error(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
            throw new IllegalArgumentException(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
        }

        if (!fund.getFundStatus().equals(FundStatus.ACTIVE)) {
            log.error(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalArgumentException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        long fundActualAmountInCents = getFundActualAmountInCents(fundId);

        if (fundActualAmountInCents < amountInCents) {
            log.error(FundMessages.NOT_ENOUGH_BALANCE_IN_FUND);
            throw new IllegalStateException(FundMessages.NOT_ENOUGH_BALANCE_IN_FUND);
        }

        Wallet treasurerWallet = walletRepository.findByParent_UserId(userId);

        Parent treasurer = fund.getSchoolClass().getTreasurer();

        FundOperation fundWithdrawalOperation = FundOperation
                .builder()
                .parent(treasurer)
                .fund(fund)
                .wallet(treasurerWallet)
                .amountInCents(amountInCents)
                .fundOperationType(FundOperationType.WITHDRAWAL)
                .fundOperationStatus(FundOperationStatus.SUCCESS)
                .build();

        fundOperationRepository.save(fundWithdrawalOperation);
        log.info("fund operation saved {}", fundWithdrawalOperation);

        treasurerWallet.setBalanceInCents(treasurerWallet.getBalanceInCents() + amountInCents);
        walletRepository.save(treasurerWallet);
        log.info("wallet updated {}", treasurerWallet);

        log.debug("exit withdrawFunds");
    }

    public long getFundActualAmountInCents(UUID fundId) {
        List<FundOperation> fundOperations = fundOperationRepository.findAllByFund_FundId(fundId);

        long fundActualAmountInCents = 0;

        for (FundOperation fundOperation : fundOperations) {
            if (fundOperation.getFundOperationStatus().equals(FundOperationStatus.SUCCESS)) {
                FundOperationType fundOperationType = fundOperation.getFundOperationType();
                switch (fundOperationType) {
                    case PAYMENT:
                    case DEPOSIT:
                        fundActualAmountInCents += fundOperation.getAmountInCents();
                        break;
                    case WITHDRAWAL:
                    case REFUND:
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
            if (fundOperation.getFundOperationStatus().equals(FundOperationStatus.SUCCESS)) {
                FundOperationType fundOperationType = fundOperation.getFundOperationType();
                switch (fundOperationType) {
                    case DEPOSIT:
                        remainingDepositLimitInCents -= fundOperation.getAmountInCents();
                        break;
                    case WITHDRAWAL:
                        remainingDepositLimitInCents += fundOperation.getAmountInCents();
                        break;
                    default:
                        break;
                }
            }
        }

        return remainingDepositLimitInCents;
    }

}
