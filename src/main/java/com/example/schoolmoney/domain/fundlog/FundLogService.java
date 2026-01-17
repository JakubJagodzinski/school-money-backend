package com.example.schoolmoney.domain.fundlog;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundAccessService;
import com.example.schoolmoney.domain.fund.FundFinder;
import com.example.schoolmoney.domain.fund.FundStatus;
import com.example.schoolmoney.domain.fundlog.dto.response.FundLogResponseDto;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentFinder;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.domain.schoolclass.SchoolClassAccessService;
import com.example.schoolmoney.domain.schoolclass.SchoolClassFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundLogService {

    private final FundLogRepository fundLogRepository;

    private final SecurityUtils securityUtils;

    private final FundAccessService fundAccessService;

    private final FundFinder fundFinder;

    private final ParentFinder parentFinder;

    private final SchoolClassFinder schoolClassFinder;

    private final SchoolClassAccessService schoolClassAccessService;

    @Transactional(readOnly = true)
    public Page<FundLogResponseDto> getFundLogs(UUID fundId, UUID schoolClassId, FundStatus fundStatus, Pageable pageable) throws IllegalArgumentException {
        log.debug("Enter getFundLogs(fundId={}, schoolClassId={}, fundStatus={}, pageable={})", fundId, schoolClassId, fundStatus, pageable);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        if (fundId == null) {
            if (schoolClassId != null) {
                SchoolClass schoolClass = schoolClassFinder.getByIdOrThrow(schoolClassId);
                schoolClassAccessService.assertCanViewSchoolClass(parent, schoolClass);
            } else {
                throw new IllegalArgumentException("Either fundId or schoolClassId must be provided");
            }
        } else {
            Fund fund = fundFinder.getByIdOrThrow(fundId);
            fundAccessService.assertCanViewFund(parent, fund);
        }

        if (fundStatus == null) {
            fundStatus = FundStatus.ACTIVE;
        }

        Page<FundLogView> fundLogViewPage = fundLogRepository.findFundLogs(fundId, schoolClassId, fundStatus.name(), pageable);

        Page<FundLogResponseDto> fundLogResponseDtoPage = fundLogViewPage.map(view -> FundLogResponseDto.builder()
                .timestamp(view.getTimestamp())
                .fundTitle(view.getFund_title())
                .parentFullName(view.getParent_full_name())
                .childFullName(view.getChild_full_name())
                .amountInCents(view.getAmount_in_cents())
                .currency(view.getCurrency())
                .operationStatus(FinancialOperationStatus.valueOf(view.getOperation_status()))
                .operationType(FundLogOperationType.valueOf(view.getOperation_type()))
                .note(view.getNote())
                .build()
        );

        log.debug("Exit getFundLogs(fundId={}, schoolClassId={}, fundStatus={}, pageable={})", fundId, schoolClassId, fundStatus, pageable);
        return fundLogResponseDtoPage;
    }

}
