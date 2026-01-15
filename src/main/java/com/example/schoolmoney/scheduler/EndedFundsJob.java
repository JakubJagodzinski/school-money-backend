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
public class EndedFundsJob {

    private final FundRepository fundRepository;

    private final FundProcessingService fundProcessingService;

    @Scheduled(cron = "0 0 * * * *")
    public void markEndedFundsAsFinished() {
        log.debug("Job to mark ended funds as finished started");

            List<Fund> endedFundsList = fundRepository.findAllByEndsAtBeforeAndFundStatus(Instant.now(), FundStatus.ACTIVE);

        for (Fund endedfund : endedFundsList) {
            try {
                fundProcessingService.markFundAsFinished(endedfund);
            } catch (Exception e) {
                log.error("Error marking fund as finished: {}", endedfund.getFundId());
            }
        }

        log.info("Marked {} funds as finished", endedFundsList.size());

        log.debug("Job to mark ended funds as finished finished");
    }

}
