package com.example.schoolmoney.domain.financialoperation;

import java.time.Instant;

public interface FinancialOperationView {

    Instant getStartedAt();

    Instant getProcessedAt();

    long getAmountInCents();

    String getOperationType();

    String getOperationStatus();

}
