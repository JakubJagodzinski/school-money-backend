package com.example.schoolmoney.domain.report.domain.child;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.ChildMessages;
import com.example.schoolmoney.domain.child.Child;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChildReportService {

    private final FundOperationRepository fundOperationRepository;

    private final ParentRepository parentRepository;

    private final ChildRepository childRepository;

    private final ChildReportPdfGenerator childReportPdfGenerator;

    private final SecurityUtils securityUtils;

    private final EmailService emailService;

    private final ChildAvatarService childAvatarService;

    @Transactional
    public ReportDto generateChildReport(UUID childId) throws EntityNotFoundException, AccessDeniedException {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> {
                    log.warn(ChildMessages.CHILD_NOT_FOUND);
                    return new EntityNotFoundException(ChildMessages.CHILD_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        if (!child.getParent().getUserId().equals(userId)) {
            log.warn(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
            throw new AccessDeniedException(ChildMessages.CHILD_DOES_NOT_BELONG_TO_PARENT);
        }

        InputStreamResource childAvatar = childAvatarService.getChildAvatar(childId);

        List<FundOperation> childFundOperationList = fundOperationRepository.findAllByChild_ChildIdOrderByProcessedAtAsc(childId);

        long childTotalParticipatedFunds = fundOperationRepository.countDistinctFundsByChildId(childId);

        ChildReportData childReportData = ChildReportData.builder()
                .child(child)
                .childAvatar(childAvatar)
                .childFundOperationList(childFundOperationList)
                .childTotalParticipatedFunds(childTotalParticipatedFunds)
                .build();

        byte[] childReport = childReportPdfGenerator.generateReportPdf(childReportData);

        ReportDto reportDto = ReportDto
                .builder()
                .report(childReport)
                .reportFileName(ReportFilenameGenerator.generate(child.getFullName()))
                .build();

        Parent parent = parentRepository.getReferenceById(userId);

        emailService.sendChildReportEmail(
                parent.getEmail(),
                parent.getFirstName(),
                child.getFullName(),
                reportDto.getReport(),
                reportDto.getReportFileName()
        );
        return reportDto;
    }

}
