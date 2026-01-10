package com.example.schoolmoney.domain.fund;

import com.example.schoolmoney.common.constants.messages.domain.FundMessages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundFinder {

    private final FundRepository fundRepository;

    @Transactional(readOnly = true)
    public Fund getByIdOrThrow(UUID fundId) {
        return fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });
    }

}
