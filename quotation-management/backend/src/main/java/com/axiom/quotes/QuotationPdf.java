package com.axiom.quotes;

import com.axiom.users.AppUser;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** A server-generated PDF with flowing tables and repeatable page headers. */
@Component
public class QuotationPdf {
  private static final Color INK = new Color(17, 32, 52);
  private static final Color MUTED = new Color(81, 102, 122);
  private static final Color BLUE = new Color(43, 137, 164);
  private static final Color PINK = new Color(238, 101, 147);
  private static final Color PALE = new Color(239, 245, 249);
  @Value("${axiom.studio.name:AXIOM STUDIO}") private String name;
  @Value("${axiom.studio.address:Colombo, Sri Lanka}") private String address;
  @Value("${axiom.studio.email:studio@example.test}") private String email;
  @Value("${axiom.studio.phone:+94 11 000 0000}") private String phone;
  private final BaseFont regular = loadFont("DejaVuSans.ttf");
  private final BaseFont boldFont = loadFont("DejaVuSans-Bold.ttf");

  private BaseFont loadFont(String filename) {
    try (var stream = getClass().getResourceAsStream("/fonts/" + filename)) {
      if (stream == null) throw new IllegalStateException("Missing PDF font");
      return BaseFont.createFont(filename, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, false, stream.readAllBytes(), null);
    } catch (Exception e) { throw new IllegalStateException("Unable to load PDF font", e); }
  }

  private Font font(float size, boolean bold, Color color) {
    return new Font(bold ? boldFont : regular, size, Font.NORMAL, color);
  }
  private String safe(String text) {
    if (text == null) return "";
    return text.replace('\t', ' ').replaceAll("[\\p{Cntrl}&&[^\\n]]", "");
  }
  private Paragraph paragraph(String text, float size, boolean bold, Color color) {
    var p = new Paragraph(safe(text), font(size, bold, color));
    p.setLeading(size * 1.45f);
    p.setSpacingAfter(10);
    return p;
  }
  private PdfPCell cell(String text, boolean heading, int align) {
    var c = new PdfPCell(new Phrase(safe(text), font(10, heading, heading ? Color.WHITE : INK)));
    c.setPadding(12);
    c.setBorder(Rectangle.NO_BORDER);
    c.setBackgroundColor(heading ? INK : PALE);
    c.setHorizontalAlignment(align);
    return c;
  }
  private String money(BigDecimal amount) {
    return "LKR " + new DecimalFormat("#,##0.00").format(amount);
  }

  public byte[] render(Quotation quote, AppUser customer) {
    try (var out = new ByteArrayOutputStream()) {
      var document = new Document(PageSize.A4, 44, 44, 108, 60);
      var writer = PdfWriter.getInstance(document, out);
      writer.setPageEvent(new PdfPageEventHelper() {
        @Override public void onEndPage(PdfWriter w, Document d) {
          var cb = w.getDirectContent();
          cb.setColorFill(INK); cb.rectangle(0, 760, 595, 82); cb.fill();
          cb.setColorFill(PINK); cb.rectangle(44, 750, 78, 5); cb.fill();
          ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(name, font(22, true, Color.WHITE)), 44, 800, 0);
          ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("CREATIVE IDEAS. CLEAR POSSIBILITIES.", font(8, false, new Color(144, 211, 232))), 44, 780, 0);
          ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase("QUOTATION", font(12, true, Color.WHITE)), 550, 800, 0);
          ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("AXIOM / QUO-" + String.format("%05d", quote.id), font(8, false, MUTED)), 44, 32, 0);
          ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase("Page " + w.getPageNumber(), font(8, false, MUTED)), 550, 32, 0);
        }
      });
      document.addTitle("AXIOM Quotation #" + quote.id);
      document.addAuthor(name);
      document.open();
      document.add(paragraph("QUO-" + String.format("%05d", quote.id), 12, true, BLUE));
      document.add(paragraph(quote.title, 26, true, INK));
      String date = quote.createdAt.atZone(ZoneId.of("Asia/Colombo")).format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
      document.add(paragraph("Issued " + date + "  |  Finance approved", 10, false, MUTED));
      var details = new PdfPTable(2); details.setWidthPercentage(100); details.setSpacingBefore(12); details.setSpacingAfter(22);
      details.addCell(cell("PREPARED FOR\n" + customer.name + "\n" + customer.email + "\n" + safe(customer.address) + "\n" + safe(customer.contactNo), false, Element.ALIGN_LEFT));
      details.addCell(cell("PREPARED BY\n" + name + "\n" + address + "\n" + email + "\n" + phone, false, Element.ALIGN_LEFT));
      document.add(details);
      var lines = new PdfPTable(new float[]{3, 1.4f}); lines.setWidthPercentage(100); lines.setHeaderRows(1); lines.setSplitLate(false);
      lines.addCell(cell("SERVICE / PACKAGE", true, Element.ALIGN_LEFT));
      lines.addCell(cell("AMOUNT", true, Element.ALIGN_RIGHT));
      for (String line : safe(quote.lines).split("\n")) {
        if (line.isBlank()) continue;
        int split = line.lastIndexOf(" — LKR ");
        lines.addCell(cell(split < 0 ? line : line.substring(0, split), false, Element.ALIGN_LEFT));
        String amount = split < 0 ? "Included" : money(new BigDecimal(line.substring(split + 7).trim()));
        lines.addCell(cell(amount, false, Element.ALIGN_RIGHT));
      }
      if (quote.additionalAmount != null && quote.additionalAmount.signum() > 0) {
        lines.addCell(cell("Additional work\n" + quote.additionalDescription, false, Element.ALIGN_LEFT));
        lines.addCell(cell(money(quote.additionalAmount), false, Element.ALIGN_RIGHT));
      }
      document.add(lines);
      var total = new PdfPTable(new float[]{1, 1}); total.setWidthPercentage(100); total.setSpacingBefore(8); total.setSpacingAfter(24);
      total.addCell(cell("TOTAL QUOTATION", true, Element.ALIGN_LEFT));
      total.addCell(cell(money(quote.total), true, Element.ALIGN_RIGHT)); document.add(total);
      document.add(paragraph("YOUR BRIEF", 11, true, BLUE));
      // A one-column table splits long briefs across pages instead of clipping them.
      var brief = new PdfPTable(1); brief.setWidthPercentage(100); brief.setSplitLate(false);
      brief.addCell(cell(quote.requirements, false, Element.ALIGN_LEFT)); document.add(brief);
      document.add(paragraph("Next step", 12, true, INK));
      document.add(paragraph("Review this quotation in your AXIOM account and select Accept quotation & start project. Our Project Manager can then schedule your work and assign the design team.", 10, false, MUTED));
      document.add(paragraph("Thank you for choosing AXIOM.", 12, true, BLUE));
      document.close();
      return out.toByteArray();
    } catch (Exception e) {
      throw new IllegalStateException("Unable to generate quotation PDF", e);
    }
  }
}
