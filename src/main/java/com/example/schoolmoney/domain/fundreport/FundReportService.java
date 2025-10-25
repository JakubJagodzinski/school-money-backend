package com.example.schoolmoney.domain.fundreport;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.FundMessages;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fundoperation.FundOperation;
import com.example.schoolmoney.domain.fundoperation.FundOperationRepository;
import com.example.schoolmoney.domain.fundreport.dto.FundReportDto;
import com.example.schoolmoney.domain.fundreport.pdf.FundReportPdfGenerator;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.email.EmailService;
import com.example.schoolmoney.utils.DateToStringConverter;
import com.example.schoolmoney.utils.StringSanitizer;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FundReportService {

    private final FundOperationRepository fundOperationRepository;

    private final FundRepository fundRepository;

    private final ParentRepository parentRepository;

    private final FundReportPdfGenerator fundReportPdfGenerator;

    private final SecurityUtils securityUtils;

    private final EmailService emailService;

    @Transactional
    public FundReportDto generateFundReport(UUID fundId) throws EntityNotFoundException, AccessDeniedException {
        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        List<FundOperation> fundOperations = fundOperationRepository.findAllByFund_FundIdOrderByProcessedAtAsc(fundId);

        UUID userId = securityUtils.getCurrentUserId();

        Parent parent = parentRepository.getReferenceById(userId);

        // TODO add parent fund access check

        long participatingChildrenCount = fundOperationRepository.countDistinctChildIdsByFundId(fundId);

        byte[] fundReport = fundReportPdfGenerator.generateFundReportPdf(fund, fundOperations, participatingChildrenCount);

        FundReportDto fundReportDto = FundReportDto
                .builder()
                .fundReport(fundReport)
                .fundReportFileName(generateReportFilename(fund.getTitle()))
                .build();

        emailService.sendFundReportEmail(parent.getEmail(), parent.getFirstName(), fund.getTitle(), fundReportDto.getFundReport(), fundReportDto.getFundReportFileName());
        return fundReportDto;
    }

    private String generateReportFilename(String fundTitle) {
        String timestamp = DateToStringConverter.nowFileTimestamp();

        String filename = String.format(
                "Fund_%s_%s.pdf",
                fundTitle.replaceAll("\\s+", "_"),
                timestamp
        );

        return StringSanitizer.sanitizeString(filename);
    }

}
