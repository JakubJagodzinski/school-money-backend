package com.example.schoolmoney.domain.report.domain.fund;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.FundMessages;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fund.FundService;
import com.example.schoolmoney.domain.fund.dto.response.FundChildStatusResponseDto;
import com.example.schoolmoney.domain.fundlogo.FundLogoService;
import com.example.schoolmoney.domain.fundmediaoperation.FundMediaOperation;
import com.example.schoolmoney.domain.fundmediaoperation.FundMediaOperationRepository;
import com.example.schoolmoney.domain.fundoperation.FundOperation;
import com.example.schoolmoney.domain.fundoperation.FundOperationRepository;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.report.ReportFilenameGenerator;
import com.example.schoolmoney.domain.report.domain.fund.dto.FundReportData;
import com.example.schoolmoney.domain.report.domain.fund.generator.pdf.FundReportPdfGenerator;
import com.example.schoolmoney.domain.report.dto.ReportDto;
import com.example.schoolmoney.email.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
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

    private final FundLogoService fundLogoService;

    private final FundMediaOperationRepository fundMediaOperationRepository;

    private final FundService fundService;

    @Transactional
    public ReportDto generateFundReport(UUID fundId) throws EntityNotFoundException {
        log.debug("Enter generateFundReport(fundId={})", fundId);

        Fund fund = fundRepository.findById(fundId)
                .orElseThrow(() -> {
                    log.warn(FundMessages.FUND_NOT_FOUND);
                    return new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        if (!fundService.canParentAccessFund(userId, fundId)) {
            log.warn(FundMessages.FUND_NOT_FOUND);
            throw new EntityNotFoundException(FundMessages.FUND_NOT_FOUND);
        }

        InputStreamResource fundLogo = fundLogoService.getFundLogo(fundId);

        List<FundOperation> fundOperationList = fundOperationRepository.findAllByFund_FundIdOrderByProcessedAtAsc(fundId);

        List<FundMediaOperation> fundMediaOperations = fundMediaOperationRepository.findAllByFundIdOrderByProcessedAtDesc(fundId);

        List<FundChildStatusResponseDto> fundChildrenStatuses = fundService.getFundChildrenStatuses(fundId, Pageable.unpaged()).getContent();

        FundReportData fundReportData = FundReportData.builder()
                .fund(fund)
                .fundLogo(fundLogo)
                .fundOperationList(fundOperationList)
                .fundChildrenStatuses(fundChildrenStatuses)
                .fundMediaOperations(fundMediaOperations)
                .build();

        byte[] fundReport = fundReportPdfGenerator.generateReportPdf(fundReportData);

        ReportDto reportDto = ReportDto
                .builder()
                .report(fundReport)
                .reportFileName(ReportFilenameGenerator.generate(fund.getTitle()))
                .build();

        Parent parent = parentRepository.getReferenceById(userId);

        emailService.sendFundReportEmail(
                parent.getEmail(),
                parent.getFirstName(),
                fund.getTitle(),
                reportDto.getReport(),
                reportDto.getReportFileName(),
                parent.isNotificationsEnabled()
        );

        log.debug("Exit generateFundReport()");
        return reportDto;
    }

}
