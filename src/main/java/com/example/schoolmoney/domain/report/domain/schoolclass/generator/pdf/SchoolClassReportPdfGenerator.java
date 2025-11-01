package com.example.schoolmoney.domain.report.domain.schoolclass.generator.pdf;

import com.example.schoolmoney.common.constants.messages.domain.FundReportMessages;
import com.example.schoolmoney.domain.child.dto.response.ChildWithParentInfoResponseDto;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.dto.response.FundChildStatusResponseDto;
import com.example.schoolmoney.domain.parent.dto.response.ParentResponseDto;
import com.example.schoolmoney.domain.report.domain.schoolclass.dto.SchoolClassReportData;
import com.example.schoolmoney.domain.report.dto.ReportData;
import com.example.schoolmoney.domain.report.generator.pdf.ReportPageEvent;
import com.example.schoolmoney.domain.report.generator.pdf.ReportPdfGenerator;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.utils.AmountFormatter;
import com.example.schoolmoney.utils.DateToStringConverter;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SchoolClassReportPdfGenerator implements ReportPdfGenerator {

    @Override
    public byte[] generateReportPdf(ReportData reportData) {
        log.info("School class report pdf generator started");

        SchoolClassReportData schoolClassReportData = (SchoolClassReportData) reportData;
        SchoolClass schoolClass = schoolClassReportData.getSchoolClass();
        InputStreamResource schoolClassAvatar = schoolClassReportData.getSchoolClassAvatar();
        Map<Fund, List<FundChildStatusResponseDto>> fundsWithChildrenStatuses = schoolClassReportData.getSchoolClassFundsWithChildrenStatuses();
        long schoolClassTotalFunds = fundsWithChildrenStatuses.size();
        List<ChildWithParentInfoResponseDto> schoolClassChildrenWithParentInfo = schoolClassReportData.getSchoolClassChildrenWithParentInfo();
        long schoolClassTotalChildren = schoolClassChildrenWithParentInfo.size();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, out);
            pdfWriter.setPageEvent(new ReportPageEvent(schoolClass.getFullName()));

            document.open();

            document.add(createTitle("School class general information"));

            Image schoolClassAvatarImage = createImage(schoolClassAvatar, 400, 200);
            if (schoolClassAvatarImage != null) {
                document.add(schoolClassAvatarImage);
            }

            document.add(createSchoolClassInfoTable(schoolClass, schoolClassTotalFunds, schoolClassTotalChildren));
            document.newPage();

            document.add(createTitle("Children members"));
            document.add(createSchoolClassChildrenInfoTable(schoolClassChildrenWithParentInfo));
            document.newPage();

            document.add(createTitle("School class funds"));
            for (Map.Entry<Fund, List<FundChildStatusResponseDto>> entry : fundsWithChildrenStatuses.entrySet()) {
                Fund fund = entry.getKey();
                document.add(createTitle(fund.getTitle() + " Fund"));
                document.add(createFundGeneralInfoTable(fund));
                document.add(createTitle("Fund children statuses"));
                document.add(createFundChildrenStatusesTable(entry.getValue()));
                document.newPage();
            }

            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            log.error(FundReportMessages.PDF_REPORT_GENERATION_FAILED, e);
            throw new RuntimeException(FundReportMessages.PDF_REPORT_GENERATION_FAILED, e);
        } finally {
            log.info("School class report pdf generator finished");
        }
    }

    private PdfPTable createFundChildrenStatusesTable(List<FundChildStatusResponseDto> fundChildStatusResponseDtoList) {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        addHeaderCell(table, "Child");
        addHeaderCell(table, "Parent");
        addHeaderCell(table, "Status");

        for (FundChildStatusResponseDto fundChildStatusResponseDto : fundChildStatusResponseDtoList) {
            ChildWithParentInfoResponseDto child = fundChildStatusResponseDto.getChild();
            ParentResponseDto parent = child.getParent();

            String childFullName = child.getFirstName() + " " + child.getLastName();
            String parentFullName = parent.getFirstName() + " " + parent.getLastName();

            addDataCell(table, childFullName);
            addDataCell(table, parentFullName);
            addDataCell(table, fundChildStatusResponseDto.getStatus().name());
        }

        return table;
    }

    private PdfPTable createSchoolClassChildrenInfoTable(List<ChildWithParentInfoResponseDto> children) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        addHeaderCell(table, "Child");
        addHeaderCell(table, "Parent");

        for (ChildWithParentInfoResponseDto child : children) {
            String childFullName = child.getFirstName() + " " + child.getLastName();
            String parentFullName = child.getParent().getFirstName() + " " + child.getParent().getLastName();

            addDataCell(table, childFullName);
            addDataCell(table, parentFullName);
        }

        return table;
    }

    private PdfPTable createFundGeneralInfoTable(Fund fund) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        addRow(table, "Fund title", fund.getTitle());
        addRow(table, "Author", fund.getAuthor().getFullName());
        addRow(table, "Amount per child", AmountFormatter.format(fund.getAmountPerChildInCents(), fund.getCurrency()));
        addRow(table, "Start date", DateToStringConverter.fromInstant(fund.getStartsAt()));
        if (fund.getEndedAt() != null) {
            addRow(table, "Ended at", DateToStringConverter.fromInstant(fund.getEndedAt()));
        } else {
            addRow(table, "End date", DateToStringConverter.fromInstant(fund.getEndsAt()));
        }
        addRow(table, "Status", fund.getFundStatus().name());
        addRow(table, "IBAN", fund.getIban());

        return table;
    }

    private PdfPTable createSchoolClassInfoTable(SchoolClass schoolClass, long schoolClassTotalFunds, long schoolClassTotalChildren) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        addRow(table, "School class name", schoolClass.getSchoolClassName());
        addRow(table, "School class year", schoolClass.getSchoolClassYear());
        addRow(table, "Created at", DateToStringConverter.fromInstant(schoolClass.getCreatedAt()));
        addRow(table, "Treasurer", schoolClass.getTreasurer().getFullName());
        addRow(table, "Total funds", String.valueOf(schoolClassTotalFunds));
        addRow(table, "Total children", String.valueOf(schoolClassTotalChildren));

        return table;
    }

}
