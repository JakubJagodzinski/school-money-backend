package com.example.schoolmoney.domain.report;

import com.example.schoolmoney.domain.fund.FundChildStatus;

import java.awt.*;

public final class ChildFundStatusColorSelector {

    private ChildFundStatusColorSelector() {
    }

    public static Color getStatusColor(FundChildStatus fundChildStatus) {
        return switch (fundChildStatus) {
            case PAID -> new Color(33, 145, 34);
            case UNPAID -> new Color(165, 4, 4);
            case DECLINED -> new Color(184, 172, 27);
        };
    }

}
