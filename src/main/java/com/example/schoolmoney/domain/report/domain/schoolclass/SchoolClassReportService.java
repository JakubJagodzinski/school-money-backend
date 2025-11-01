package com.example.schoolmoney.domain.report.domain.schoolclass;

import com.example.schoolmoney.auth.access.SecurityUtils;
import com.example.schoolmoney.common.constants.messages.domain.SchoolClassMessages;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.child.ChildRepository;
import com.example.schoolmoney.domain.child.dto.ChildMapper;
import com.example.schoolmoney.domain.child.dto.response.ChildWithParentInfoResponseDto;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.FundRepository;
import com.example.schoolmoney.domain.fund.FundService;
import com.example.schoolmoney.domain.fund.dto.response.FundChildStatusResponseDto;
import com.example.schoolmoney.domain.parent.Parent;
import com.example.schoolmoney.domain.parent.ParentRepository;
import com.example.schoolmoney.domain.report.ReportFilenameGenerator;
import com.example.schoolmoney.domain.report.domain.schoolclass.dto.SchoolClassReportData;
import com.example.schoolmoney.domain.report.domain.schoolclass.generator.pdf.SchoolClassReportPdfGenerator;
import com.example.schoolmoney.domain.report.dto.ReportDto;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.domain.schoolclass.SchoolClassRepository;
import com.example.schoolmoney.domain.schoolclass.SchoolClassService;
import com.example.schoolmoney.domain.schoolclassavatar.SchoolClassAvatarService;
import com.example.schoolmoney.email.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class SchoolClassReportService {

    private final ParentRepository parentRepository;

    private final SchoolClassRepository schoolClassRepository;

    private final SchoolClassReportPdfGenerator schoolClassReportPdfGenerator;

    private final SecurityUtils securityUtils;

    private final EmailService emailService;

    private final SchoolClassAvatarService schoolClassAvatarService;

    private final ChildRepository childRepository;

    private final SchoolClassService schoolClassService;

    private final FundRepository fundRepository;

    private final ChildMapper childMapper;

    private final FundService fundService;

    @Transactional
    public ReportDto generateSchoolClassReport(UUID schoolClassId) throws EntityNotFoundException {
        log.debug("Enter generateSchoolClassReport(schoolClassId={})", schoolClassId);

        SchoolClass schoolClass = schoolClassRepository.findById(schoolClassId)
                .orElseThrow(() -> {
                    log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                    return new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
                });

        UUID userId = securityUtils.getCurrentUserId();
        if (!schoolClassService.canParentAccessSchoolClass(userId, schoolClassId)) {
            log.warn(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
            throw new EntityNotFoundException(SchoolClassMessages.SCHOOL_CLASS_NOT_FOUND);
        }

        InputStreamResource schoolClassAvatar = schoolClassAvatarService.getSchoolClassAvatar(schoolClassId);

        Map<Fund, List<FundChildStatusResponseDto>> schoolClassFundsWithChildrenStatuses = new HashMap<>();

        List<Fund> schoolClassFunds = fundRepository.findAllBySchoolClass_SchoolClassId(schoolClassId);

        for (Fund fund : schoolClassFunds) {
            List<FundChildStatusResponseDto> fundChildStatusResponseDtoList = fundService.getFundChildrenStatuses(fund.getFundId(), Pageable.unpaged()).getContent();
            schoolClassFundsWithChildrenStatuses.put(fund, fundChildStatusResponseDtoList);
        }

        List<Child> schoolClassChildren = childRepository.findAllBySchoolClass_SchoolClassId(schoolClassId);
        List<ChildWithParentInfoResponseDto> schoolClassChildrenWithParentInfo = schoolClassChildren.stream().map(childMapper::toWithParentInfoDto).toList();

        SchoolClassReportData schoolClassReportData = SchoolClassReportData.builder()
                .schoolClass(schoolClass)
                .schoolClassAvatar(schoolClassAvatar)
                .schoolClassFundsWithChildrenStatuses(schoolClassFundsWithChildrenStatuses)
                .schoolClassChildrenWithParentInfo(schoolClassChildrenWithParentInfo)
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
                reportDto.getReportFileName(),
                parent.isNotificationsEnabled()
        );

        log.debug("Exit generateSchoolClassReport()");
        return reportDto;
    }

}
