package com.example.schoolmoney.domain.childfund;

import java.time.Instant;
import java.util.UUID;

public interface ChildFundView {

    UUID getChildId();

    UUID getFundId();

    ChildFundStatus getChildStatus();

    Instant getTimestamp();

}
