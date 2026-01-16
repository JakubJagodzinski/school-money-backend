package com.example.schoolmoney.domain.report.domain.child;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildAccessService;
import com.example.schoolmoney.domain.child.ChildFinder;
import com.example.schoolmoney.domain.childavatar.ChildAvatarService;
import com.example.schoolmoney.domain.fundoperation.FundOperation;
import com.example.schoolmoney.domain.fundoperation.FundOperationRepository;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentFinder;
import com.example.schoolmoney.domain.report.ReportFilenameGenerator;
import com.example.schoolmoney.domain.report.domain.child.dto.ChildReportData;
import com.example.schoolmoney.domain.report.domain.child.generator.pdf.ChildReportPdfGenerator;
import com.example.schoolmoney.domain.report.dto.ReportDto;
import com.example.schoolmoney.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChildReportService {

    private final FundOperationRepository fundOperationRepository;

    private final ChildReportPdfGenerator childReportPdfGenerator;

    private final SecurityUtils securityUtils;

    private final EmailService emailService;

    private final ChildAvatarService childAvatarService;

    private final ChildAccessService childAccessService;

    private final ChildFinder childFinder;

    private final ParentFinder parentFinder;

    @Transactional(readOnly = true)
    public ReportDto generateChildReport(UUID childId) {
        log.debug("Enter generateChildReport(childId={})", childId);

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentFinder.getByIdOrThrow(userId);

        Child child = childFinder.getByIdOrThrow(childId);
        childAccessService.assertCanAccessChild(parent, child);

        ChildReportData childReportData = prepareChildReportData(child);
        byte[] childReport = childReportPdfGenerator.generateReportPdf(childReportData);

        ReportDto reportDto = ReportDto
                .builder()
                .report(childReport)
                .reportFileName(ReportFilenameGenerator.generate(child.getFullName()))
                .build();

        emailService.sendChildReportEmail(
                parent.getEmail(),
                parent.getFirstName(),
                child.getFullName(),
                reportDto.getReport(),
                reportDto.getReportFileName(),
                parent.isNotificationsEnabled()
        );

        log.debug("Exit generateChildReport(childId={})", childId);
        return reportDto;
    }

    private ChildReportData prepareChildReportData(Child child) {
        log.debug("Enter prepareChildReportData(child={})", child);

        UUID childId = child.getChildId();

        InputStreamResource childAvatar = childAvatarService.getChildAvatar(childId);
        List<FundOperation> childFundOperationsList = fundOperationRepository.findAllByChild_ChildIdOrderByProcessedAtAsc(childId);
        long childTotalParticipatedFunds = fundOperationRepository.countDistinctFundsByChildId(childId);

        log.debug("Exit prepareChildReportData(child={})", child);
        return ChildReportData.builder()
                .child(child)
                .childAvatar(childAvatar)
                .childFundOperationList(childFundOperationsList)
                .childTotalParticipatedFunds(childTotalParticipatedFunds)
                .build();
    }

}
