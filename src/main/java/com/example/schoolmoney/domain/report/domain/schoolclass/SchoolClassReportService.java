package com.example.schoolmoney.domain.report.domain.schoolclass;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.SchoolClassMessages;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.fundoperation.FundOperationRepository;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.report.ReportFilenameGenerator;
import com.example.schoolmoney.domain.report.domain.schoolclass.dto.SchoolClassReportData;
import com.example.schoolmoney.domain.report.domain.schoolclass.generator.pdf.SchoolClassReportPdfGenerator;
import com.example.schoolmoney.domain.report.dto.ReportDto;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.domain.schoolclass.SchoolClassRepository;
import com.example.schoolmoney.domain.schoolclassavatar.SchoolClassAvatarService;
import com.example.schoolmoney.email.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SchoolClassReportService {

    private final FundOperationRepository fundOperationRepository;

    private final ParentRepository parentRepository;

    private final SchoolClassRepository schoolClassRepository;

    private final SchoolClassReportPdfGenerator schoolClassReportPdfGenerator;

    private final SecurityUtils securityUtils;

    private final EmailService emailService;

    private final SchoolClassAvatarService schoolClassAvatarService;
    private final ChildRepository childRepository;

    @Transactional
    public ReportDto generateSchoolClassReport(UUID schoolClassId) throws EntityNotFoundException, AccessDeniedException {
        SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> {
                    log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                    return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();

        // TODO add check if parent has access to this class

        InputStreamResource schoolClassAvatar = schoolClassAvatarService.getSchoolClassAvatar(schoolClassId);

        // TODO add school class operation list grouped by fund - maybe dict?

        long schoolClassTotalFunds = fundOperationRepository.countDistinctFundsBySchoolClassId(schoolClassId);

        long schoolClassTotalChildren = childRepository.countBySchoolClass_SchoolClassId(schoolClassId);

        SchoolClassReportData schoolClassReportData = SchoolClassReportData.builder()
                .schoolClass(schoolClass)
                .schoolClassAvatar(schoolClassAvatar)
                .schoolClassTotalFunds(schoolClassTotalFunds)
                .schoolClassTotalChildren(schoolClassTotalChildren)
                .build();

        byte[] schoolClassReport = schoolClassReportPdfGenerator.generateReportPdf(schoolClassReportData);

        ReportDto reportDto = ReportDto
                .builder()
                .report(schoolClassReport)
                .reportFileName(ReportFilenameGenerator.generate(schoolClass.getFullName()))
                .build();

        Parent parent = parentRepository.getReferenceById(userId);

        emailService.sendSchoolClassReportEmail(
                parent.getEmail(),
                parent.getFirstName(),
                schoolClass.getFullName(),
                reportDto.getReport(),
                reportDto.getReportFileName()
        );
        return reportDto;
    }

}
