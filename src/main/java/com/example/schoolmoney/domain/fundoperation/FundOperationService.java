package com.example.schoolmoney.domain.fundoperation;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.ChildMessages;
import com.example.schoolmoney.common.constants.messages.domain.FundMessages;
import com.example.schoolmoney.common.constants.messages.domain.FundOperationMessages;
import com.example.schoolmoney.common.constants.messages.domain.WalletMessages;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildAccessService;
import com.example.schoolmoney.domain.child.ChildFinder;
import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundAccessService;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fund.FundStatus;
import com.example.schoolmoney.domain.fundoperation.dto.FundOperationMapper;
import com.example.schoolmoney.domain.fundoperation.dto.request.DepositToFundRequestDto;
import com.example.schoolmoney.domain.fundoperation.dto.request.WithdrawFromFundRequestDto;
import com.example.schoolmoney.domain.fundoperation.dto.response.FundOperationResponseDto;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.wallet.Wallet;
import com.example.schoolmoney.domain.wallet.WalletRepository;
import com.example.schoolmoney.email.EmailService;
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
@Transactional(readOnly = true)
@Service
public class FundOperationService {

    private final FundOperationMapper fundOperationMapper;

    private final FundOperationRepository fundOperationRepository;

    private final FundRepository fundRepository;

    private final WalletRepository walletRepository;

    private final SecurityUtils securityUtils;

    private final EmailService emailService;

    private final FundAccessService fundAccessService;

    private final ParentRepository parentRepository;

    private final ChildAccessService childAccessService;

    private final ChildFinder childFinder;

    @Transactional
    public void performPayment(UUID fundId, UUID childId) throws EntityNotFoundException, IllegalStateException {
        log.debug("Enter performPayment(fundId={}, childId={})", fundId, childId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        if (!fundAccessService.canViewFund(parent, fund)) {
            log.warn(FundMessages.FUND_NOT_FOUND);
            throw new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
        }

        Child child = childFinder.getByIdOrThrow(childId);

        if (!childAccessService.canAccessChild(parent, child)) {
            log.warn(ChildMessages.CHILD_NOT_FOUND);
            throw new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
        }

        if (fund.getFundStatus().equals(FundStatus.BLOCKED)) {
            log.warn(FundMessages.FUND_IS_BLOCKED);
            throw new IllegalStateException(FundMessages.FUND_IS_BLOCKED);
        }

        if (!fund.getFundStatus().equals(FundStatus.ACTIVE)) {
            log.warn(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalStateException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        if (fundOperationRepository.existsByFund_FundIdAndParent_UserIdAndChild_ChildIdAndOperationTypeAndOperationStatus(
                fundId, userId, childId, FundOperationType.FUND_PAYMENT, FinancialOperationStatus.SUCCESS
        )) {
            log.warn(FundOperationMessages.PAYMENT_ALREADY_MADE_FOR_THIS_CHILD);
            throw new IllegalStateException(FundOperationMessages.PAYMENT_ALREADY_MADE_FOR_THIS_CHILD);
        }

        Wallet parentWallet = walletRepository.findByParent_UserId(userId);

        if (parentWallet.getAvailableBalanceInCents() < fund.getAmountPerChildInCents()) {
            log.warn(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
            throw new IllegalStateException(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
        }

        parentWallet.decreaseBalanceInCents(fund.getAmountPerChildInCents());
        walletRepository.save(parentWallet);
        log.info("Wallet updated {}", parentWallet);

        FundOperation fundOperation = FundOperation
                .builder()
                .parent(child.getParent())
                .child(child)
                .fund(fund)
                .wallet(parentWallet)
                .amountInCents(fund.getAmountPerChildInCents())
                .currency(fund.getCurrency())
                .operationType(FundOperationType.FUND_PAYMENT)
                .operationStatus(FinancialOperationStatus.SUCCESS)
                .build();

        fundOperationRepository.save(fundOperation);
        log.info("Fund operation saved {}", fundOperation);

        emailService.sendFundPaymentEmail(
                parent.getEmail(),
                parent.getFirstName(),
                fund.getTitle(),
                fund.getSchoolClass().getFullName(),
                child.getFullName(),
                fund.getAmountPerChildInCents(),
                fund.getCurrency(),
                parent.isNotificationsEnabled()
        );

        log.debug("Exit performPayment");
    }

    @Transactional
    public void depositToFund(UUID fundId, DepositToFundRequestDto requestDto) throws EntityNotFoundException, IllegalStateException, IllegalArgumentException, AccessDeniedException {
        log.debug("Enter depositToFund(fundId={}, requestDto={})", fundId, requestDto);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        if (!fundAccessService.canViewFund(parent, fund)) {
            log.warn(FundMessages.FUND_NOT_FOUND);
            throw new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
        }

        if (!fundAccessService.canEditFund(parent, fund)) {
            log.warn(FundMessages.NO_PERMISSION_TO_EDIT_THIS_FUND);
            throw new AccessDeniedException(FundMessages.NO_PERMISSION_TO_EDIT_THIS_FUND);
        }

        if (fund.getFundStatus().equals(FundStatus.BLOCKED)) {
            log.warn(FundMessages.FUND_IS_BLOCKED);
            throw new IllegalStateException(FundMessages.FUND_IS_BLOCKED);
        }

        if (!fund.getFundStatus().equals(FundStatus.ACTIVE)) {
            log.warn(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalStateException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        long amountInCents = requestDto.getAmountInCents();

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

        if (treasurerWallet.getAvailableBalanceInCents() < amountInCents) {
            log.warn(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
            throw new IllegalStateException(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
        }

        treasurerWallet.decreaseBalanceInCents(amountInCents);
        walletRepository.save(treasurerWallet);
        log.info("Wallet updated {}", treasurerWallet);

        FundOperation fundDepositOperation = FundOperation
                .builder()
                .parent(fund.getSchoolClass().getTreasurer())
                .fund(fund)
                .wallet(treasurerWallet)
                .amountInCents(amountInCents)
                .currency(fund.getCurrency())
                .operationType(FundOperationType.FUND_DEPOSIT)
                .operationStatus(FinancialOperationStatus.SUCCESS)
                .note(requestDto.getNote())
                .build();

        fundOperationRepository.save(fundDepositOperation);
        log.info("Fund operation saved {}", fundDepositOperation);

        log.debug("Exit depositToFund");
    }

    @Transactional
    public void withdrawFromFund(UUID fundId, WithdrawFromFundRequestDto requestDto) throws EntityNotFoundException, IllegalArgumentException, IllegalStateException, AccessDeniedException {
        log.debug("Enter withdrawFromFund(fundId={}, requestDto={})", fundId, requestDto);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        if (!fundAccessService.canViewFund(parent, fund)) {
            log.warn(FundMessages.FUND_NOT_FOUND);
            throw new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
        }

        if (!fundAccessService.canEditFund(parent, fund)) {
            log.warn(FundMessages.NO_PERMISSION_TO_EDIT_THIS_FUND);
            throw new AccessDeniedException(FundMessages.NO_PERMISSION_TO_EDIT_THIS_FUND);
        }

        if (fund.getFundStatus().equals(FundStatus.BLOCKED)) {
            log.warn(FundMessages.FUND_IS_BLOCKED);
            throw new IllegalStateException(FundMessages.FUND_IS_BLOCKED);
        }

        if (!fund.getFundStatus().equals(FundStatus.ACTIVE)) {
            log.warn(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalStateException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        long amountInCents = requestDto.getAmountInCents();

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

        treasurerWallet.increaseBalanceInCents(amountInCents);
        walletRepository.save(treasurerWallet);
        log.info("wallet updated {}", treasurerWallet);

        FundOperation fundWithdrawalOperation = FundOperation
                .builder()
                .parent(fund.getSchoolClass().getTreasurer())
                .fund(fund)
                .wallet(treasurerWallet)
                .amountInCents(amountInCents)
                .currency(fund.getCurrency())
                .operationType(FundOperationType.FUND_WITHDRAWAL)
                .operationStatus(FinancialOperationStatus.SUCCESS)
                .note(requestDto.getNote())
                .build();

        fundOperationRepository.save(fundWithdrawalOperation);
        log.info("Fund operation saved {}", fundWithdrawalOperation);

        log.debug("Exit withdrawFromFund");
    }

    private long getFundActualAmountInCents(UUID fundId) {
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

    private long getFundRemainingDepositLimitInCents(UUID fundId) {
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

    public Page<FundOperationResponseDto> getFundAllOperations(UUID fundId, Pageable pageable) throws EntityNotFoundException {
        log.debug("Enter getFundAllOperations(fundId={}, pageable={})", fundId, pageable);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        if (!fundAccessService.canViewFund(parent, fund)) {
            log.warn(FundMessages.FUND_NOT_FOUND);
            throw new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
        }

        Page<FundOperation> fundOperationPage = fundOperationRepository.findAllByFund_FundIdOrderByProcessedAtDesc(fundId, pageable);

        log.debug("Exit getFundAllOperations");
        return fundOperationPage.map(fundOperationMapper::toDto);
    }

}
