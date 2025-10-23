package com.example.schoolmoney.domain.fundreport.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FundReportPageEvent extends PdfPageEventHelper {

    private final String fundTitle;

    private final BaseFont baseFont;
    private final Font headerFont;
    private final Font footerFont;

    private PdfTemplate totalPageTemplate;

    public FundReportPageEvent(String fundTitle) {
        this.fundTitle = fundTitle;

        try {
            this.baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1250, BaseFont.NOT_EMBEDDED);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize BaseFont", e);
        }

        this.footerFont = new Font(baseFont, 10, Font.ITALIC);
        this.headerFont = new Font(baseFont, 16, Font.BOLD);
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

        // === HEADER ===
        ColumnText.showTextAligned(
                cb,
                Element.ALIGN_CENTER,
                new Phrase("Fund report: " + fundTitle, headerFont),
                centerX,
                document.top() + 15, // slightly lower than the top margin
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
