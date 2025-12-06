package com.example.schoolmoney.domain.fundlog;

import java.time.Instant;

public interface FundLogView {

    Instant getTimestamp();

    String getFundTitle();

    String getParentFullName();

    String getChildFullName();

    double getAmountInCents();

    String getCurrency();

    String getOperationType();

    String getOperationStatus();

    String getDescription();

}
