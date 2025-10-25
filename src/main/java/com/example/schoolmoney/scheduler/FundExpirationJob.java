package com.example.schoolmoney.scheduler;

import com.example.schoolmoney.domain.fund.FundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class FundExpirationJob {

    private final FundService fundService;

    @Scheduled(cron = "0 0 * * * *")
    public void markExpiredFunds() {
        log.debug("Job to mark expired funds started");

        fundService.expireEndedFunds();

        log.debug("Job to mark expired funds finished");
    }

}
