package com.fizzah.lucky_draw_system.util;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

public class PdfGenerator {

    public static byte[] generateTablePdf(String title, List<String> headers, List<Map<String, String>> rows) {
        try {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 54, 36);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Title
            if (title != null) {
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
                Paragraph p = new Paragraph(title, titleFont);
                p.setAlignment(Element.ALIGN_CENTER);
                p.setSpacingAfter(10);
                document.add(p);
            }

            PdfPTable table = new PdfPTable(headers.size());
            table.setWidthPercentage(100);

            // add header
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
                // cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // add rows
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            for (Map<String, String> row : rows) {
                for (String h : headers) {
                    String val = row.getOrDefault(h, "");
                    PdfPCell cell = new PdfPCell(new Phrase(val, bodyFont));
                    cell.setPadding(5);
                    table.addCell(cell);
                }
            }

            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate PDF", ex);
        }
    }
}
