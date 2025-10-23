package com.example.schoolmoney.domain.fundreport.pdf;

import com.example.schoolmoney.common.constants.messages.domain.FundReportMessages;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fundoperation.FundOperation;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class FundReportPdfGenerator {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] generateFundReportPdf(Fund fund, List<FundOperation> fundOperations, long participatingChildrenCount) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, out);
            pdfWriter.setPageEvent(new FundReportPageEvent(fund.getTitle()));

            document.open();
            document.add(createFundInfoTable(fund, participatingChildrenCount));
            document.add(createFundOperationsTable(fundOperations));
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(FundReportMessages.PDF_REPORT_GENERATION_FAILED, e);
        }
    }

    private PdfPTable createFundInfoTable(Fund fund, long participatingChildrenCount) {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);

        addRow(table, "Fund title", fund.getTitle());
        addRow(table, "Author", fund.getAuthor().getFullName());
        addRow(table, "School class", fund.getSchoolClass().getFullName());
        addRow(table, "Treasurer", fund.getSchoolClass().getTreasurer().getFullName());
        addRow(table, "Amount per child", String.format("%.2f PLN", fund.getAmountPerChildInCents() / 100.0));
        addRow(table, "Children participating", String.valueOf(participatingChildrenCount));
        addRow(table, "Start date", LocalDateTime.ofInstant(fund.getStartsAt(), ZoneId.of("UTC")).format(dateTimeFormatter));
        addRow(table, "End date", LocalDateTime.ofInstant(fund.getEndsAt(), ZoneId.of("UTC")).format(dateTimeFormatter));
        if (fund.getEndedAt() != null) {
            addRow(table, "Ended at", LocalDateTime.ofInstant(fund.getEndedAt(), ZoneId.of("UTC")).format(dateTimeFormatter));
        }
        addRow(table, "Updated at", LocalDateTime.ofInstant(fund.getUpdatedAt(), ZoneId.of("UTC")).format(dateTimeFormatter));
        addRow(table, "Status", fund.getFundStatus().name());
        addRow(table, "IBAN", fund.getIban());

        if (fund.getDescription() != null && !fund.getDescription().isEmpty()) {
            addRow(table, "Description", fund.getDescription());
        }

        return table;
    }

    private void addRow(PdfPTable table, String label, String value) {
        Font labelFont = new Font(Font.HELVETICA, 12, Font.BOLD);
        Font valueFont = new Font(Font.HELVETICA, 12);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));

        labelCell.setPadding(8);
        valueCell.setPadding(8);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private PdfPTable createFundOperationsTable(List<FundOperation> fundOperations) {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);

        addHeaderCell(table, "Parent");
        addHeaderCell(table, "Child");
        addHeaderCell(table, "Amount");
        addHeaderCell(table, "Type");
        addHeaderCell(table, "Processed At");

        for (FundOperation fundOperation : fundOperations) {
            addDataCell(table, fundOperation.getParent().getFullName());
            addDataCell(table, fundOperation.getChild().getFullName());
            addDataCell(table, String.format("%.2f PLN", fundOperation.getAmountInCents() / 100.0));
            addDataCell(table, fundOperation.getOperationType().name());
            addDataCell(table, LocalDateTime.ofInstant(fundOperation.getProcessedAt(), ZoneId.systemDefault()).format(dateTimeFormatter));
        }

        return table;
    }

    private void addHeaderCell(PdfPTable table, String text) {
        Font font = new Font(Font.HELVETICA, 12, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        table.addCell(cell);
    }

    private void addDataCell(PdfPTable table, String text) {
        Font font = new Font(Font.HELVETICA, 12);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(8);
        table.addCell(cell);
    }

}
