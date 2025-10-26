package com.example.schoolmoney.domain.report.domain.schoolclass.generator.pdf;

import com.example.schoolmoney.common.constants.messages.domain.FundReportMessages;
import com.example.schoolmoney.domain.report.domain.schoolclass.dto.SchoolClassReportData;
import com.example.schoolmoney.domain.report.dto.ReportData;
import com.example.schoolmoney.domain.report.generator.pdf.ReportPageEvent;
import com.example.schoolmoney.domain.report.generator.pdf.ReportPdfGenerator;
import com.example.schoolmoney.domain.schoolclass.SchoolClass;
import com.example.schoolmoney.utils.DateToStringConverter;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class SchoolClassReportPdfGenerator implements ReportPdfGenerator {

    @Override
    public byte[] generateReportPdf(ReportData reportData) {
        SchoolClassReportData schoolClassReportData = (SchoolClassReportData) reportData;
        SchoolClass schoolClass = schoolClassReportData.getSchoolClass();
        InputStreamResource schoolClassAvatar = schoolClassReportData.getSchoolClassAvatar();

        long schoolClassTotalFunds = schoolClassReportData.getSchoolClassTotalFunds();
        long schoolClassTotalChildren = schoolClassReportData.getSchoolClassTotalChildren();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, out);
            pdfWriter.setPageEvent(new ReportPageEvent(schoolClass.getFullName()));

            document.open();

            document.add(createTitle("General information"));

            Image schoolClassAvatarImage = createImage(schoolClassAvatar, 400, 200);
            if (schoolClassAvatarImage != null) {
                document.add(schoolClassAvatarImage);
            }

            document.add(createSchoolClassInfoTable(schoolClass, schoolClassTotalFunds, schoolClassTotalChildren));

            document.newPage();

            document.add(createTitle("School class funds operations history"));
            // TODO

            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(FundReportMessages.PDF_REPORT_GENERATION_FAILED, e);
        }
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
