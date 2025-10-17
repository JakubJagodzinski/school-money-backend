package com.example.schoolmoney.domain.fundoperation;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.ChildMessages;
import com.example.schoolmoney.common.constants.messages.FundMessages;
import com.example.schoolmoney.common.constants.messages.FundOperationMessages;
import com.example.schoolmoney.common.constants.messages.WalletMessages;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.wallet.Wallet;
import com.example.schoolmoney.domain.wallet.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        Wallet wallet = walletRepository.findByParent_UserId(userId);

        if (wallet.getBalanceInCents() < amountInCents) {
            log.error(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
            throw new IllegalStateException(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
        }

        FundOperation fundOperation = FundOperation
                .builder()
                .parent(child.getParent())
                .child(child)
                .fund(fund)
                .wallet(wallet)
                .amountInCents(amountInCents)
                .fundOperationType(FundOperationType.PAYMENT)
                .fundOperationStatus(FundOperationStatus.SUCCESS)
                .build();

        fundOperationRepository.save(fundOperation);
        log.info("fund operation saved {}", fundOperation);

        wallet.setBalanceInCents(wallet.getBalanceInCents() - amountInCents);
        walletRepository.save(wallet);
        log.info("wallet updated {}", wallet);

        log.debug("exit performPayment");
    }

}
