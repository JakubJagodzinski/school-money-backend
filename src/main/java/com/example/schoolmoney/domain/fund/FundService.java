package com.example.schoolmoney.domain.fund;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.FundMessages;
import com.example.schoolmoney.common.constants.messages.SchoolClassMessages;
import com.example.schoolmoney.common.constants.messages.WalletMessages;
import com.example.schoolmoney.domain.fund.dto.FundMapper;
import com.example.schoolmoney.domain.fund.dto.request.CreateFundRequestDto;
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

    private final SecurityUtils securityUtils;

    @Transactional
    public FundResponseDto createFund(CreateFundRequestDto createFundRequestDto) throws EntityNotFoundException, IllegalArgumentException {
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
            throw new IllegalArgumentException(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
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

    // TODO split it
    @Transactional
    public void cancelFund(UUID fundId) throws EntityNotFoundException, IllegalArgumentException, IllegalStateException {
        log.debug("enter cancelFund {}", fundId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.error(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        if (!fund.getFundStatus().equals(FundStatus.ACTIVE)) {
            log.error(FundMessages.FUND_IS_NOT_ACTIVE);
            throw new IllegalArgumentException(FundMessages.FUND_IS_NOT_ACTIVE);
        }

        UUID userId = securityUtils.getCurrentUserId();

        SchoolClass schoolClass = fund.getSchoolClass();

        if (!schoolClass.getTreasurer().getUserId().equals(userId)) {
            log.error(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
            throw new IllegalArgumentException(SchoolClassMessages.PARENT_NOT_TREASURER_OF_THIS_SCHOOL_CLASS);
        }

        List<FundOperation> fundOperations = fundOperationRepository.findAllByFund_FundId(fundId);

        if (!fundOperations.isEmpty()) {
            long resultAmountInCents = 0;

            for (FundOperation fundOperation : fundOperations) {
                if (fundOperation.getFundOperationType().equals(FundOperationType.DEPOSIT)) {
                    resultAmountInCents += fundOperation.getAmountInCents();
                } else if (fundOperation.getFundOperationType().equals(FundOperationType.WITHDRAWAL)) {
                    resultAmountInCents -= fundOperation.getAmountInCents();
                }
            }

            log.debug("Treasurer total withdrawal amount {}", resultAmountInCents);

            if (resultAmountInCents < 0) {
                Wallet treasurerWallet = walletRepository.findByParent_UserId(userId);

                if (treasurerWallet.getBalanceInCents() < resultAmountInCents) {
                    log.error(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
                    throw new IllegalStateException(WalletMessages.INSUFFICIENT_WALLET_BALANCE);
                }

                FundOperation treasurerFundDepositOperation = FundOperation
                        .builder()
                        .parent(fund.getAuthor())
                        .fund(fund)
                        .wallet(treasurerWallet)
                        .amountInCents(Math.abs(resultAmountInCents))
                        .fundOperationType(FundOperationType.DEPOSIT)
                        .build();

                fundOperationRepository.save(treasurerFundDepositOperation);
                log.info("Fund deposit operation saved {}", treasurerFundDepositOperation);

                treasurerWallet.setBalanceInCents(treasurerWallet.getBalanceInCents() - Math.abs(resultAmountInCents));
                walletRepository.save(treasurerWallet);
                log.info("Treasurer wallet updated {}", treasurerWallet);
            }
        } else {
            log.info("No fund operations found for fund {}", fundId);
        }

        fund.setFundStatus(FundStatus.CANCELLED);
        fundRepository.save(fund);
        log.info("Fund cancelled {}", fund);

        for (FundOperation fundOperation : fundOperations) {
            if (fundOperation.getFundOperationType().equals(FundOperationType.PAYMENT) && fundOperation.getAmountInCents() > 0) {
                log.debug("Processing refund for fund operation {}", fundOperation);

                Wallet parentWallet = walletRepository.findByParent_UserId(fundOperation.getParent().getUserId());

                parentWallet.setBalanceInCents(parentWallet.getBalanceInCents() + fundOperation.getAmountInCents());
                walletRepository.save(parentWallet);
                log.info("Parent wallet updated {}", parentWallet);

                FundOperation parentRefundOperation = FundOperation
                        .builder()
                        .parent(fundOperation.getParent())
                        .child(fundOperation.getChild())
                        .fund(fund)
                        .wallet(parentWallet)
                        .amountInCents(fundOperation.getAmountInCents())
                        .fundOperationType(FundOperationType.REFUND)
                        .build();

                fundOperationRepository.save(parentRefundOperation);
                log.info("Parent refund operation saved {}", parentRefundOperation);
            }
        }

        log.debug("exit cancelFund");
    }

    @Transactional
    public void expireEndedFunds() {
        List<Fund> endedFunds = fundRepository.findAllByEndsAtBeforeAndFundStatus(Instant.now(), FundStatus.ACTIVE);

        for (Fund fund : endedFunds) {
            fund.setFundStatus(FundStatus.FINISHED);
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

}
