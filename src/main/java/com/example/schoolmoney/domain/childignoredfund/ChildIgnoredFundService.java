package com.example.schoolmoney.domain.childignoredfund;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.ChildMessages;
import com.example.schoolmoney.common.constants.messages.FundMessages;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChildIgnoredFundService {

    private final ChildIgnoredFundRepository childIgnoredFundRepository;

    private final ChildRepository childRepository;

    private final FundRepository fundRepository;

    private final SecurityUtils securityUtils;

    @Transactional
    public void ignoreFundForChild(UUID childId, UUID fundId) throws EntityNotFoundException, EntityExistsException, IllegalArgumentException {
        log.debug("Ignoring fund {} for child {}", fundId, childId);

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.error(ChildMessages.CHILD_NOT_FOUND);
                    return new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();

        if (!child.getParent().getUserId().equals(userId)) {
            throw new IllegalArgumentException(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
        }

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.error(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        ChildIgnoredFundId id = new ChildIgnoredFundId(childId, fundId);

        if (childIgnoredFundRepository.existsById(id)) {
            log.warn(FundMessages.FUND_IS_ALREADY_IGNORED_BY_CHILD);
            throw new EntityExistsException(FundMessages.FUND_IS_ALREADY_IGNORED_BY_CHILD);
        }

        ChildIgnoredFund childIgnoredFund = ChildIgnoredFund
                .builder()
                .id(id)
                .child(child)
                .fund(fund)
                .build();

        childIgnoredFundRepository.save(childIgnoredFund);
        log.info("Child ignored fund saved {}", childIgnoredFund);

        log.debug("Exit ignoreFundForChild");
    }

    @Transactional
    public void unignoreFundForChild(UUID childId, UUID fundId) throws EntityNotFoundException, IllegalArgumentException {
        log.debug("Enter unignoreFundForChild");
        UUID userId = securityUtils.getCurrentUserId();

        log.debug("Unignoring fund {} for child {} by user {}", fundId, childId, userId);

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.error(ChildMessages.CHILD_NOT_FOUND);
                    return new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
                });

        if (!child.getParent().getUserId().equals(userId)) {
            log.error(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
            throw new IllegalArgumentException(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
        }

        int deleted = childIgnoredFundRepository.deleteByChild_ChildIdAndFund_FundId(childId, fundId);

        if (deleted == 0) {
            log.error(FundMessages.FUND_NOT_IGNORED_BY_CHILD);
            throw new EntityNotFoundException(FundMessages.FUND_NOT_IGNORED_BY_CHILD);
        }

        log.info("Fund {} unignored for child {}", fundId, childId);

        log.debug("Exit unignoreFundForChild");
    }

}
