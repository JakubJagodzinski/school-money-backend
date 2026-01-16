package com.example.schoolmoney.domain.fundmedia;

import com.example.schoolmoney.common.constants.messages.domain.FundMediaMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundMediaFinder {

    private final FundMediaRepository fundMediaRepository;

    @Transactional(readOnly = true)
    public FundMedia getByFundIdAndFundMediaIdOrThrow(UUID fundId, UUID fundMediaId) {
        return fundMediaRepository.findByFund_FundIdAndFundMediaId(fundId, fundMediaId)
                .orElseThrow(() -> {
                    log.warn("Fund media with id={} not found in fund with id={}", fundMediaId, fundId);
                    return new EntityNotFoundException(FundMediaMessages.FUND_MEDIA_NOT_FOUND);
                });
    }

}
