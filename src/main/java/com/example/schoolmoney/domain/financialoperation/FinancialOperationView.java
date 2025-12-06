package com.example.schoolmoney.domain.financialoperation;

import java.time.Instant;

public interface FinancialOperationView {

    Instant getProcessed_at();

    double getAmount_in_cents();

    String getOperation_type();

    String getOperation_status();

}
