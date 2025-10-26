package com.example.schoolmoney.domain.report.generator.pdf;

import com.example.schoolmoney.domain.report.dto.ReportData;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.springframework.core.io.InputStreamResource;

public interface ReportPdfGenerator {

    Font FONT_TITLE = new Font(Font.HELVETICA, 14, Font.BOLD);
    Font FONT_LABEL = new Font(Font.HELVETICA, 12, Font.BOLD);
    Font FONT_VALUE = new Font(Font.HELVETICA, 12);
    Font FONT_HEADER_CELL = new Font(Font.HELVETICA, 12, Font.BOLD);
    Font FONT_DATA_CELL = new Font(Font.HELVETICA, 12);

    byte[] generateReportPdf(ReportData reportData);

    default Paragraph createTitle(String title) {
        Paragraph titleParagraph = new Paragraph(title, FONT_TITLE);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        titleParagraph.setSpacingBefore(10);
        titleParagraph.setSpacingAfter(10);

        return titleParagraph;
    }

    default void addRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, FONT_LABEL));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, FONT_VALUE));

        labelCell.setPadding(8);
        valueCell.setPadding(8);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    default void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_HEADER_CELL));
        cell.setPadding(8);
        table.addCell(cell);
    }

    default void addDataCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_DATA_CELL));
        cell.setPadding(8);
        table.addCell(cell);
    }

    default Image createImage(InputStreamResource imageResource, float width, float height) {
        try {
            Image image = Image.getInstance(imageResource.getInputStream().readAllBytes());
            image.scaleToFit(width, height);
            image.setAlignment(Element.ALIGN_CENTER);

            return image;
        } catch (Exception e) {
            return null;
        }
    }

}
