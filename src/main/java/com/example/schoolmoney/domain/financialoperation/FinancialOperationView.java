package com.example.schoolmoney.domain.financialoperation;

import java.time.Instant;

public interface FinancialOperationView {

    Instant getProcessedAt();

    double getAmountInCents();

    String getOperationType();

    String getOperationStatus();

}
