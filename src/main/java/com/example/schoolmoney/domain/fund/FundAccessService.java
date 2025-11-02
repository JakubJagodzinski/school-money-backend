package com.example.schoolmoney.domain.fund;

import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.fundoperation.FundOperationRepository;
import com.example.schoolmoney.domain.parent.Parent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FundAccessService {

    private final FundOperationRepository fundOperationRepository;

    private final ChildRepository childRepository;

    public boolean canViewFund(Parent parent, Fund fund) {
        boolean isFundAuthor = fund.getAuthor().getUserId().equals(parent.getUserId());
        boolean isTreasurer = fund.getSchoolClass().getTreasurer().getUserId().equals(parent.getUserId());
        boolean hasContribution = fundOperationRepository.existsByFund_FundIdAndParent_UserId(fund.getFundId(), parent.getUserId());
        boolean hasChildInSchoolClass = childRepository.existsByParent_UserIdAndSchoolClass_SchoolClassId(parent.getUserId(), fund.getSchoolClass().getSchoolClassId());

        return isFundAuthor || isTreasurer || hasContribution || hasChildInSchoolClass;
    }

    public boolean canEditFund(Parent parent, Fund fund) {
        return fund.getSchoolClass().getTreasurer().getUserId().equals(parent.getUserId());
    }

    public boolean canCancelFund(Parent parent, Fund fund) {
        return fund.getSchoolClass().getTreasurer().getUserId().equals(parent.getUserId());
    }

}
