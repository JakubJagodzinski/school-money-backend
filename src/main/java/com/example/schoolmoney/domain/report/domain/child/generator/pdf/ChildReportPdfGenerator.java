package com.example.schoolmoney.domain.report.domain.child.generator.pdf;

import com.example.schoolmoney.common.constants.messages.domain.FundReportMessages;
import com.example.schoolmoney.domain.child.Child;
import com.example.schoolmoney.domain.fundoperation.FundOperation;
import com.example.schoolmoney.domain.report.domain.child.dto.ChildReportData;
import com.example.schoolmoney.domain.report.dto.ReportData;
import com.example.schoolmoney.domain.report.generator.pdf.ReportPageEvent;
import com.example.schoolmoney.domain.report.generator.pdf.ReportPdfGenerator;
import com.example.schoolmoney.utils.DateToStringConverter;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ChildReportPdfGenerator implements ReportPdfGenerator {

    @Override
    public byte[] generateReportPdf(ReportData reportData) {
        ChildReportData childReportData = (ChildReportData) reportData;
        Child child = childReportData.getChild();
        InputStreamResource childAvatar = childReportData.getChildAvatar();
        List<FundOperation> childFundOperationList = childReportData.getChildFundOperationList();
        long childTotalParticipatedFunds = childReportData.getChildTotalParticipatedFunds();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, out);
            pdfWriter.setPageEvent(new ReportPageEvent(child.getFullName()));

            document.open();

            document.add(createTitle("Child general information"));

            Image childAvatarImage = createImage(childAvatar, 400, 200);
            if (childAvatarImage != null) {
                document.add(childAvatarImage);
            }

            document.add(createChildInfoTable(child, childTotalParticipatedFunds));

            document.newPage();

            document.add(createTitle("Child fund operations history"));
            document.add(createChildFundOperationsTable(childFundOperationList));

            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(FundReportMessages.PDF_REPORT_GENERATION_FAILED, e);
        }
    }

    private PdfPTable createChildInfoTable(Child child, long childTotalParticipatedFunds) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        addRow(table, "First name", child.getFirstName());
        addRow(table, "Last name", child.getLastName());
        addRow(table, "Birth date", child.getBirthDate().toString());
        addRow(table, "Parent", child.getParent().getFullName());
        addRow(table, "School class", child.getSchoolClass().getFullName());
        addRow(table, "Total participated funds", String.valueOf(childTotalParticipatedFunds));

        return table;
    }

    private PdfPTable createChildFundOperationsTable(List<FundOperation> childFundOperationList) {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);

        addHeaderCell(table, "Fund");
        addHeaderCell(table, "Parent");
        addHeaderCell(table, "Child");
        addHeaderCell(table, "Amount");
        addHeaderCell(table, "Type");
        addHeaderCell(table, "Processed At");

        for (FundOperation childFundOperation : childFundOperationList) {
            addDataCell(table, childFundOperation.getFund().getTitle());
            addDataCell(table, childFundOperation.getParent().getFullName());
            addDataCell(table, childFundOperation.getChild().getFullName());
            addDataCell(table, String.format("%.2f PLN", childFundOperation.getAmountInCents() / 100.0));
            addDataCell(table, childFundOperation.getOperationType().name());
            addDataCell(table, DateToStringConverter.fromInstantToLocal(childFundOperation.getProcessedAt()));
        }

        return table;
    }

}
