package com.example.schoolmoney.domain.fundoperation;

import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.fund.FundStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundOperationFinder {

    private final FundOperationRepository fundOperationRepository;

    @Transactional(readOnly = true)
    public long getFundCurrentBalanceInCents(UUID fundId) {
        return fundOperationRepository.getFundCurrentBalanceInCents(
                fundId,
                FundOperationType.FUND_PAYMENT,
                FundOperationType.FUND_DEPOSIT,
                FundOperationType.FUND_REFUND,
                FundOperationType.FUND_WITHDRAWAL,
                FinancialOperationStatus.SUCCESS
        );
    }

    @Transactional(readOnly = true)
    public long getSchoolClassFundsCurrentBalanceInCents(UUID schoolClassId, FundStatus fundStatus) {
        return fundOperationRepository.getSchoolClassFundsCurrentBalanceInCents(
                schoolClassId,
                fundStatus,
                FundOperationType.FUND_PAYMENT,
                FundOperationType.FUND_DEPOSIT,
                FundOperationType.FUND_REFUND,
                FundOperationType.FUND_WITHDRAWAL,
                FinancialOperationStatus.SUCCESS
        );
    }

}
