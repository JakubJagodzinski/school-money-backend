package com.example.schoolmoney.domain.report.domain.child;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.ChildMessages;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildAccessService;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.childavatar.ChildAvatarService;
import com.example.schoolmoney.domain.fundoperation.FundOperation;
import com.example.schoolmoney.domain.fundoperation.FundOperationRepository;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.report.ReportFilenameGenerator;
import com.example.schoolmoney.domain.report.domain.child.dto.ChildReportData;
import com.example.schoolmoney.domain.report.domain.child.generator.pdf.ChildReportPdfGenerator;
import com.example.schoolmoney.domain.report.dto.ReportDto;
import com.example.schoolmoney.email.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChildReportService {

    private final FundOperationRepository fundOperationRepository;

    private final ParentRepository parentRepository;

    private final ChildRepository childRepository;

    private final ChildReportPdfGenerator childReportPdfGenerator;

    private final SecurityUtils securityUtils;

    private final EmailService emailService;

    private final ChildAvatarService childAvatarService;

    private final ChildAccessService childAccessService;

    public ReportDto generateChildReport(UUID childId) throws EntityNotFoundException {
        log.debug("Enter generateChildReport(childId={})", childId);

        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.warn(ChildMessages.CHILD_NOT_FOUND);
                    return new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        Parent parent = parentRepository.getReferenceById(userId);

        if (!childAccessService.canAccessChild(parent, child)) {
            log.warn(ChildMessages.CHILD_NOT_FOUND);
            throw new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
        }

        byte[] childReport = childReportPdfGenerator.generateReportPdf(prepareChildReportData(child));

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

        log.debug("Exit generateChildReport()");
        return reportDto;
    }

    private ChildReportData prepareChildReportData(Child child) {
        UUID childId = child.getChildId();

        InputStreamResource childAvatar = childAvatarService.getChildAvatar(childId);
        List<FundOperation> childFundOperationsList = fundOperationRepository.findAllByChild_ChildIdOrderByProcessedAtAsc(childId);
        long childTotalParticipatedFunds = fundOperationRepository.countDistinctFundsByChildId(childId);

        return ChildReportData.builder()
                .child(child)
                .childAvatar(childAvatar)
                .childFundOperationList(childFundOperationsList)
                .childTotalParticipatedFunds(childTotalParticipatedFunds)
                .build();
    }

}
