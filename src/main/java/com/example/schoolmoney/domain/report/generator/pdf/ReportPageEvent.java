package com.example.schoolmoney.domain.report.generator.pdf;

import com.example.schoolmoney.utils.DateToStringConverter;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReportPageEvent extends PdfPageEventHelper {

    private final String reportTitle;
    private final String generatedAtText;

    private final BaseFont baseFont;
    private final Font headerTitleFont;
    private final Font headerGeneratedAtFont;
    private final Font footerFont;

    private PdfTemplate totalPageTemplate;

    public ReportPageEvent(String reportTitle) {
        this.reportTitle = reportTitle + " Financial Report";
        this.generatedAtText = "Generated at " + DateToStringConverter.nowFormatted();

        try {
            this.baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize BaseFont", e);
        }

        this.footerFont = new Font(baseFont, 10, Font.ITALIC);
        this.headerTitleFont = new Font(baseFont, 16, Font.BOLD);
        this.headerGeneratedAtFont = new Font(Font.HELVETICA, 10, Font.ITALIC);
    }

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        totalPageTemplate = writer.getDirectContent().createTemplate(30, 16);
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        float centerX = (document.right() - document.left()) / 2 + document.leftMargin();
        float bottomY = document.bottom() + 5;

        // === HEADER: title ===
        ColumnText.showTextAligned(
                cb,
                Element.ALIGN_CENTER,
                new Phrase(reportTitle, headerTitleFont),
                centerX,
                document.top() + 15, // slightly lower than the top margin
                0
        );

        // === HEADER: generated at ===
        ColumnText.showTextAligned(
                cb,
                Element.ALIGN_CENTER,
                new Phrase(this.generatedAtText, this.headerGeneratedAtFont),
                centerX,
                document.top() + 5,
                0
        );

        // === FOOTER: page numbering ===
        String text = "Page " + writer.getPageNumber() + " of ";
        float textSize = baseFont.getWidthPoint(text, footerFont.getSize());
        float textX = centerX - textSize / 2;

        cb.beginText();
        cb.setFontAndSize(baseFont, footerFont.getSize());
        cb.setTextMatrix(textX, bottomY);
        cb.showText(text);
        cb.endText();

        // placeholder for total pages
        cb.addTemplate(totalPageTemplate, textX + textSize, bottomY);

        // === FOOTER: trademark ===
        ColumnText.showTextAligned(
                cb,
                Element.ALIGN_CENTER,
                new Phrase("SchoolMoney © 2025", footerFont),
                centerX,
                bottomY + 15,
                0
        );
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        totalPageTemplate.beginText();
        totalPageTemplate.setFontAndSize(footerFont.getBaseFont(), footerFont.getSize());
        totalPageTemplate.showText(String.valueOf(writer.getPageNumber() - 1));
        totalPageTemplate.endText();
    }

}
