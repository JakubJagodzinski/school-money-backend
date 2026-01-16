package com.example.schoolmoney.domain.fund;

import com.example.schoolmoney.common.constants.messages.domain.FundMessages;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.fundoperation.FundOperationRepository;
import com.example.schoolmoney.domain.parent.Parent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundAccessService {

    private final FundOperationRepository fundOperationRepository;

    private final ChildRepository childRepository;

    public void assertCanViewFund(Parent parent, Fund fund) {
        boolean isFundAuthor = fund.getAuthor().getUserId().equals(parent.getUserId());
        boolean isTreasurer = fund.getSchoolClass().getTreasurer().getUserId().equals(parent.getUserId());
        boolean hasContribution = fundOperationRepository.existsByFund_FundIdAndParent_UserId(fund.getFundId(), parent.getUserId());
        boolean hasChildInSchoolClass = childRepository.existsByParent_UserIdAndSchoolClass_SchoolClassId(parent.getUserId(), fund.getSchoolClass().getSchoolClassId());

        boolean canViewFund = isFundAuthor || isTreasurer || hasContribution || hasChildInSchoolClass;

        if (!canViewFund) {
            log.warn("User {} doesn't have access to fund with id {}", parent.getUserId(), fund.getFundId());
            throw new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
        }
    }

    public void assertCanEditFund(Parent parent, Fund fund) {
        boolean canEditFund = fund.getSchoolClass().getTreasurer().getUserId().equals(parent.getUserId());

        if (!canEditFund) {
            log.warn("User {} doesn't have permission to edit fund with id {}", parent.getUserId(), fund.getFundId());
            throw new AccessDeniedException(FundMessages.NO_PERMISSION_TO_EDIT_THIS_FUND);
        }
    }

    public void assertFundIsNotBlocked(Fund fund) {
        if (fund.isBlocked()) {
            log.warn("Fund with id {} is blocked", fund.getFundId());
            throw new IllegalStateException(FundMessages.FUND_IS_BLOCKED);
        }
    }

    public void assertFundIsActive(Fund fund) {
        if (!fund.isActive()) {
            log.warn("Fund with id {} is not active", fund.getFundId());
            throw new IllegalStateException(FundMessages.FUND_IS_NOT_ACTIVE);
        }
    }

    public void assertFundIsNotFinishedAndNotCancelled(Fund fund) {
        if (fund.isFinished() && fund.isCancelled()) {
            log.warn("Fund with id {} is {}", fund.getFundId(), fund.getFundStatus());
            throw new IllegalStateException(FundMessages.FUND_IS_FINISHED_OR_CANCELLED);
        }
    }

}
