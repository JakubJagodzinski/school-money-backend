package com.example.schoolmoney.domain.report.domain.fund.generator.pdf;

import com.example.schoolmoney.common.constants.messages.domain.FundReportMessages;
import com.example.schoolmoney.domain.child.dto.response.ChildWithParentInfoResponseDto;
import com.example.schoolmoney.domain.fund.Fund;
import com.example.schoolmoney.domain.fund.dto.response.FundChildStatusResponseDto;
import com.example.schoolmoney.domain.fundmediaoperation.FundMediaOperation;
import com.example.schoolmoney.domain.fundoperation.FundOperation;
import com.example.schoolmoney.domain.parent.dto.response.ParentResponseDto;
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
        List<FundChildStatusResponseDto> fundChildrenStatuses = fundReportData.getFundChildrenStatuses();
        List<FundMediaOperation> fundMediaOperations = fundReportData.getFundMediaOperations();
        long fundParticipatingChildrenCount = fundChildrenStatuses.size();
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
            document.add(createParticipantsTable(fundChildrenStatuses));

            document.newPage();

            document.add(createTitle("Fund operations history"));
            document.add(createFundOperationsTable(fundOperationList));

            document.newPage();

            document.add(createTitle("Fund media operations history"));
            document.add(createFundMediaOperationsTable(fundMediaOperations));

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

    private PdfPTable createParticipantsTable(List<FundChildStatusResponseDto> fundChildStatusResponseDtoList) {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);

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

    private PdfPTable createFundMediaOperationsTable(List<FundMediaOperation> fundMediaOperations) {
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(20);

        addHeaderCell(table, "Operation");
        addHeaderCell(table, "Processed At");
        addHeaderCell(table, "Media type");
        addHeaderCell(table, "Filename");
        addHeaderCell(table, "Performed by");

        for (FundMediaOperation fundMediaOperation : fundMediaOperations) {
            addDataCell(table, fundMediaOperation.getOperationType().name());
            addDataCell(table, DateToStringConverter.fromInstantToLocal(fundMediaOperation.getProcessedAt()));
            addDataCell(table, fundMediaOperation.getMediaType().name());
            addDataCell(table, fundMediaOperation.getFilename());
            addDataCell(table, fundMediaOperation.getPerformedByFullName());
        }

        return table;
    }

}
