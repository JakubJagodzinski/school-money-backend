package com.example.schoolmoney.domain.childignoredfund;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.FundOperationMessages;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildAccessService;
import com.example.schoolmoney.domain.child.ChildFinder;
import com.example.schoolmoney.domain.financialoperation.FinancialOperationStatus;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundAccessService;
import com.example.schoolmoney.domain.fund.FundFinder;
import com.example.schoolmoney.domain.fundoperation.FundOperationRepository;
import com.example.schoolmoney.domain.fundoperation.FundOperationType;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentFinder;
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

    private final SecurityUtils securityUtils;

    private final ChildAccessService childAccessService;

    private final ChildFinder childFinder;

    private final FundFinder fundFinder;

    private final FundOperationRepository fundOperationRepository;

    private final FundAccessService fundAccessService;

    private final ParentFinder parentFinder;

    @Transactional
    public void ignoreFundForChild(UUID childId, UUID fundId) throws IllegalStateException {
        log.debug("Enter ignoreFundForChild(childId={}, fundId={}", childId, fundId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Child child = childFinder.getByIdOrThrow(childId);
        childAccessService.assertCanAccessChild(parent, child);

        Fund fund = fundFinder.getByIdOrThrow(fundId);
        fundAccessService.assertCanViewFund(parent, fund);

        ChildIgnoredFundId id = new ChildIgnoredFundId(childId, fundId);

        if (childIgnoredFundRepository.existsById(id)) {
            log.warn("Child already ignored fund");
            return;
        }

        if (fundOperationRepository.existsByFund_FundIdAndParent_UserIdAndChild_ChildIdAndOperationTypeAndOperationStatus(
                fundId, userId, childId, FundOperationType.FUND_PAYMENT, FinancialOperationStatus.SUCCESS
        )) {
            log.warn(FundOperationMessages.PAYMENT_ALREADY_MADE_FOR_THIS_CHILD);
            throw new IllegalStateException(FundOperationMessages.PAYMENT_ALREADY_MADE_FOR_THIS_CHILD);
        }

        ChildIgnoredFund childIgnoredFund = ChildIgnoredFund
                .builder()
                .id(id)
                .child(child)
                .fund(fund)
                .build();

        childIgnoredFundRepository.save(childIgnoredFund);
        log.info("Child ignored fund saved with id={}", childIgnoredFund.getId());

        log.debug("Exit ignoreFundForChild(childId={}, fundId={}", childId, fundId);
    }

    @Transactional
    public void unignoreFundForChild(UUID childId, UUID fundId, UUID parentId) {
        log.debug("Enter unignoreFundForChild(childId={}, fundId={}, parentId={})", childId, fundId, parentId);

        Parent parent = parentFinder.getByIdOrThrow(parentId);

        Child child = childFinder.getByIdOrThrow(childId);
        childAccessService.assertCanAccessChild(parent, child);

        childIgnoredFundRepository.deleteByChild_ChildIdAndFund_FundId(childId, fundId);
        log.info("Parent with userId={} unignored fund with fundId={} for child with childId={}", parentId, fundId, childId);

        log.debug("Exit unignoreFundForChild(childId={}, fundId={}, parentId={})", childId, fundId, parentId);
    }

}
