package com.example.schoolmoney.scheduler;

import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundProcessingService;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fund.FundStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class ActiveFundsJob {

    private final FundRepository fundRepository;

    private final FundProcessingService fundProcessingService;

    @Scheduled(cron = "0 0 * * * *")
    public void markScheduledFundsAsActive() {
        log.debug("Job to mark scheduled funds as active started");

        List<Fund> scheduledFunds = fundRepository.findAllByStartsAtLessThanEqualAndFundStatus(Instant.now(), FundStatus.SCHEDULED);

        for (Fund fund : scheduledFunds) {
            try {
                fundProcessingService.markFundAsActive(fund);
            } catch (Exception e) {
                log.error("Error marking fund as active: {}", fund.getFundId());
            }
        }

        log.info("Marked {} funds as active", scheduledFunds.size());

        log.debug("Job to mark scheduled funds as active finished");
    }

}
