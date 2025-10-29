package com.example.schoolmoney.domain.report.domain.fund.generator.pdf;

import com.example.schoolmoney.common.constants.messages.domain.FundReportMessages;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fundoperation.FundOperation;
import com.example.schoolmoney.domain.report.domain.fund.dto.FundReportData;
import com.example.schoolmoney.domain.report.dto.ReportData;
import com.example.schoolmoney.domain.report.generator.pdf.ReportPageEvent;
import com.example.schoolmoney.domain.report.generator.pdf.ReportPdfGenerator;
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

@Slf4j
@Service
public class FundReportPdfGenerator implements ReportPdfGenerator {

    @Override
    public byte[] generateReportPdf(ReportData reportData) {
        log.info("Fund report pdf generator started");

        FundReportData fundReportData = (FundReportData) reportData;
        Fund fund = fundReportData.getFund();
        List<FundOperation> fundOperationList = fundReportData.getFundOperationList();
        long fundParticipatingChildrenCount = fundReportData.getFundParticipatingChildrenCount();
        InputStreamResource fundLogo = fundReportData.getFundLogo();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, out);
            pdfWriter.setPageEvent(new ReportPageEvent(fund.getTitle()));

            document.open();

            document.add(createTitle("General information"));

            Image fundLogoImage = createImage(fundLogo, 400, 200);
            if (fundLogoImage != null) {
                document.add(fundLogoImage);
            }

            document.add(createFundInfoTable(fund, fundParticipatingChildrenCount));

            document.newPage();

            document.add(createTitle("Participants"));
            // TODO add participants table

            document.newPage();

            document.add(createTitle("Fund operations history"));
            document.add(createFundOperationsTable(fundOperationList));

            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            log.error(FundReportMessages.PDF_REPORT_GENERATION_FAILED, e);
            throw new RuntimeException(FundReportMessages.PDF_REPORT_GENERATION_FAILED, e);
        } finally {
            log.info("Fund report pdf generator finished");
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
        addRow(table, "Amount per child", AmountFormatter.format(fund.getAmountPerChildInCents(), fund.getCurrency()));
        addRow(table, "Children participating", String.valueOf(participatingChildrenCount));
        addRow(table, "Start date", DateToStringConverter.fromInstant(fund.getStartsAt()));
        addRow(table, "End date", DateToStringConverter.fromInstant(fund.getEndsAt()));
        if (fund.getEndedAt() != null) {
            addRow(table, "Ended at", DateToStringConverter.fromInstant(fund.getEndedAt()));
        }
        addRow(table, "Updated at", DateToStringConverter.fromInstant(fund.getUpdatedAt()));
        addRow(table, "Status", fund.getFundStatus().name());
        addRow(table, "IBAN", fund.getIban());

        if (fund.getDescription() != null && !fund.getDescription().isEmpty()) {
            addRow(table, "Description", fund.getDescription());
        }

        return table;
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
            addDataCell(table, AmountFormatter.format(fundOperation.getAmountInCents(), fundOperation.getCurrency()));
            addDataCell(table, fundOperation.getOperationType().name());
            addDataCell(table, DateToStringConverter.fromInstantToLocal(fundOperation.getProcessedAt()));
        }

        return table;
    }

}
