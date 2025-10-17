package com.example.schoolmoney.scheduler;

import com.example.schoolmoney.domain.fund.FundService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class FundExpirationJob {

    private final FundService fundService;

    @Scheduled(cron = "0 0 * * * *")
    public void markExpiredFunds() {
        fundService.expireEndedFunds();
    }

}
