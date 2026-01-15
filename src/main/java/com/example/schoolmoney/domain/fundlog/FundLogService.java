package com.example.schoolmoney.domain.fundlog;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.UserMessages;
import com.example.schoolmoney.common.constants.messages.domain.FundMessages;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundAccessService;
import com.example.schoolmoney.domain.fund.FundFinder;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundLogService {

    private final FundLogRepository fundLogRepository;

    private final SecurityUtils securityUtils;

    private final FundAccessService fundAccessService;

    private final ParentRepository parentRepository;

    private final FundFinder fundFinder;

    public Page<FundLogView> getFundLogs(UUID fundId, Pageable pageable) throws EntityNotFoundException {
        log.debug("Enter getFundLogs(fundId={}, pageable={})", fundId, pageable);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(UserMessages.USER_NOT_FOUND);
                    return new EntityNotFoundException(UserMessages.USER_NOT_FOUND);
                });
        Fund fund = fundFinder.getByIdOrThrow(fundId);

        fundAccessService.assertCanViewFund(parent, fund);

        Page<FundLogView> fundLogs = fundLogRepository.findFundLogs(fundId, pageable);

        log.debug("Exit getFundLogs(fundId={}, pageable={})", fundId, pageable);
        return fundLogs;
    }

}
