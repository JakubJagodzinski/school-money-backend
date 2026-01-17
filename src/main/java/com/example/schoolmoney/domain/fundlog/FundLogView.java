package com.example.schoolmoney.domain.fundlog;

import java.time.Instant;

public interface FundLogView {

    Instant getTimestamp();

    String getFund_title();

    String getParent_full_name();

    String getChild_full_name();

    Long getAmount_in_cents();

    String getCurrency();

    String getOperation_type();

    String getOperation_status();

    String getNote();

}
