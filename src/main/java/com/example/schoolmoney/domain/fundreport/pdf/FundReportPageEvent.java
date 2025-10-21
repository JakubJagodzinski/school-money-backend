package com.example.schoolmoney.domain.fundreport.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class FundReportPageEvent extends PdfPageEventHelper {

    private final String fundTitle;

    private final Font headerFont = new Font(Font.HELVETICA, 16, Font.BOLD);

    private final Font footerFont = new Font(Font.HELVETICA, 10, Font.ITALIC);

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();

        float horizontalCenter = (document.right() - document.left()) / 2 + document.leftMargin();

        // Header
        ColumnText.showTextAligned(
                cb,
                Element.ALIGN_CENTER,
                new Phrase("Fund report: " + fundTitle, headerFont),
                horizontalCenter,
                document.top() + 20,
                0
        );

        // Footer
        ColumnText.showTextAligned(
                cb,
                Element.ALIGN_CENTER,
                new Phrase("Report generated at " + LocalDateTime.now().format(dateTimeFormatter), footerFont),
                horizontalCenter,
                document.bottom() - 20,
                0
        );

        ColumnText.showTextAligned(
                cb,
                Element.ALIGN_CENTER,
                new Phrase("SchoolMoney © 2025", footerFont),
                horizontalCenter,
                document.bottom() - 35,
                0
        );
    }

}
