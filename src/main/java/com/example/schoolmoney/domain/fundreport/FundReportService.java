package com.example.schoolmoney.domain.fundreport;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.FundMessages;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fundoperation.FundOperation;
import com.example.schoolmoney.domain.fundoperation.FundOperationRepository;
import com.example.schoolmoney.domain.fundreport.pdf.FundReportPdfGenerator;
import com.example.schoolmoney.utils.StringSanitizer;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundReportService {

    private final FundRepository fundRepository;

    private final FundOperationRepository fundOperationRepository;

    private final FundReportPdfGenerator fundReportPdfGenerator;

    private final SecurityUtils securityUtils;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

    public byte[] generateFundReport(UUID fundId) throws EntityNotFoundException, AccessDeniedException {
        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        List<FundOperation> fundOperations = fundOperationRepository.findAllByFund_FundIdOrderByProcessedAtAsc(fundId);

        UUID userId = securityUtils.getCurrentUserId();

        // TODO add parent fund access check

        long participatingChildrenCount = fundOperationRepository.countDistinctChildIdsByFundId(fundId);

        return fundReportPdfGenerator.generateFundReportPdf(fund, fundOperations, participatingChildrenCount);
    }

    public String generateReportFilename(UUID fundId) throws EntityNotFoundException {
        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        String timestamp = LocalDateTime.now().format(dateTimeFormatter);

        String filename = String.format(
                "Fund_%s_%s.pdf",
                fund.getTitle().replaceAll("\\s+", "_"),
                timestamp
        );

        return StringSanitizer.sanitizeString(filename);
    }

}
