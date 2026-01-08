package com.example.schoolmoney.scheduler;

import com.example.schoolmoney.domain.fund.FundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ActiveFundsJob {

    private final FundService fundService;

    @Scheduled(cron = "0 0 * * * *")
    public void markScheduledFundsAsActive() {
        log.debug("Job to mark scheduled funds as active started");

        fundService.markScheduledFundsAsActive();

        log.debug("Job to mark scheduled funds as active finished");
    }

}
