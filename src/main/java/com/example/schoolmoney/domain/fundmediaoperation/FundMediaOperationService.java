package com.example.schoolmoney.domain.fundmediaoperation;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.FundMessages;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fund.FundService;
import com.example.schoolmoney.domain.fundmediaoperation.dto.FundMediaOperationMapper;
import com.example.schoolmoney.domain.fundmediaoperation.dto.response.FundMediaOperationResponseDto;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.files.FileType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundMediaOperationService {

    private final FundMediaOperationRepository fundMediaOperationRepository;

    private final FundMediaOperationMapper fundMediaOperationMapper;

    private final FundRepository fundRepository;

    private final SecurityUtils securityUtils;

    private final FundService fundService;

    @Transactional
    public void saveFundMediaOperation(Parent parent, UUID fundMediaId, String filename, FileType mediaType, UUID fundId, FundMediaOperationType operationType) {
        log.debug("Enter saveFundMediaOperation");

        FundMediaOperation fundMediaOperation = FundMediaOperation.builder()
                .performedById(parent.getUserId())
                .performedByFullName(parent.getFullName())
                .fundMediaId(fundMediaId)
                .filename(filename)
                .mediaType(mediaType)
                .fundId(fundId)
                .operationType(operationType)
                .build();

        fundMediaOperationRepository.save(fundMediaOperation);
        log.info("Fund media operation saved {}", fundMediaOperation);

        log.debug("Exit saveFundMediaOperation");
    }

    @Transactional
    public Page<FundMediaOperationResponseDto> getFundMediaOperations(UUID fundId, Pageable pageable) throws EntityNotFoundException {
        log.debug("Enter getFundMediaOperations(fundId={})", fundId);

        if (!fundRepository.existsById(fundId)) {
            log.warn(FundMessages.FUND_NOT_FOUND);
            throw new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
        }

        UUID userId = securityUtils.getCurrentUserId();
        if (!fundService.canParentAccessFund(userId, fundId)) {
            log.warn(FundMessages.FUND_NOT_FOUND);
            throw new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
        }

        Page<FundMediaOperation> fundMediaOperationPage = fundMediaOperationRepository.findAllByFundId(fundId, pageable);

        log.debug("Exit getFundMediaOperations");
        return fundMediaOperationPage.map(fundMediaOperationMapper::toDto);
    }

}
