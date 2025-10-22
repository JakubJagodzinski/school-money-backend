package com.example.schoolmoney.domain.childignoredfund;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.ChildMessages;
import com.example.schoolmoney.common.constants.messages.FundMessages;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
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
    public void ignoreFundForChild(UUID childId, UUID fundId) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter ignoreFundForChild(childId={}, fundId={}", childId, fundId);

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

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        ChildIgnoredFundId id = new ChildIgnoredFundId(childId, fundId);

        if (childIgnoredFundRepository.existsById(id)) {
            log.warn(FundMessages.FUND_IS_ALREADY_IGNORED_BY_CHILD);
            throw new IllegalStateException(FundMessages.FUND_IS_ALREADY_IGNORED_BY_CHILD);
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
    public void unignoreFundForChild(UUID childId, UUID fundId) throws EntityNotFoundException, IllegalStateException, AccessDeniedException {
        log.debug("Enter unignoreFundForChild(childId={}, fundId={})", childId, fundId);

        UUID userId = securityUtils.getCurrentUserId();

        log.debug("Unignoring fund with fundId={} for child with childId={} by parent with userId={}", fundId, childId, userId);

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.warn(ChildMessages.CHILD_NOT_FOUND);
                    return new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
                });

        if (!child.getParent().getUserId().equals(userId)) {
            log.warn(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
            throw new AccessDeniedException(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
        }

        int deleted = childIgnoredFundRepository.deleteByChild_ChildIdAndFund_FundId(childId, fundId);

        if (deleted == 0) {
            log.warn(FundMessages.FUND_NOT_IGNORED_BY_CHILD);
            throw new IllegalStateException(FundMessages.FUND_NOT_IGNORED_BY_CHILD);
        }

        log.info("Parent with userId={} unignored fund with fundId={} for child with childId={}", userId, fundId, childId);

        log.debug("Exit unignoreFundForChild");
    }

}
