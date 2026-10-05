package com.axiom.payments;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.io.ByteArrayOutputStream;
import org.springframework.stereotype.Component;

@Component
public class InvoicePdf {
  public byte[] render(Invoice i, boolean receipt) {
    try (var out = new ByteArrayOutputStream(); var stream = getClass().getResourceAsStream("/fonts/DejaVuSans.ttf")) {
      var base = BaseFont.createFont("DejaVuSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, false, stream.readAllBytes(), null);
      var normal = new Font(base, 11);
      var heading = new Font(base, 24, Font.BOLD, new java.awt.Color(17, 32, 52));
      var doc = new Document(PageSize.A4, 48, 48, 48, 48);
      PdfWriter.getInstance(doc, out);
      doc.open();
      doc.add(new Paragraph(receipt ? "PAYMENT RECEIPT" : "INVOICE", heading));
      doc.add(new Paragraph("AXIOM STUDIO  |  #" + i.id, normal));
      doc.add(new Paragraph("Issued: " + i.createdAt.atZone(java.time.ZoneId.of("Asia/Colombo")).toLocalDate(), normal));
      doc.add(new Paragraph("\nProject: " + value(i.projectTitle) + " (#" + i.projectId + ")", normal));
      doc.add(new Paragraph("Customer: " + value(i.customerName) + "\nEmail: " + value(i.customerEmail), normal));
      doc.add(new Paragraph("\nBILL TO\n" + value(i.billingName) + "\n" + value(i.billingAddress) + "\nContact: " + value(i.contactNo), normal));
      var table = new PdfPTable(new float[]{3, 2});
      table.setWidthPercentage(100); table.setSpacingBefore(28); table.setSpacingAfter(24);
      for (String text : new String[]{"Description", "Amount", value(i.projectTitle), "LKR " + new java.text.DecimalFormat("#,##0.00").format(i.amount)}) {
        var cell = new PdfPCell(new Phrase(text, normal)); cell.setPadding(12); table.addCell(cell);
      }
      doc.add(table);
      doc.add(new Paragraph("Total: LKR " + new java.text.DecimalFormat("#,##0.00").format(i.amount), heading));
      if (receipt) doc.add(new Paragraph("\nPayment method: " + value(i.paymentMethod) + "\nReference: " + value(i.paymentReference) + "\nPaid at: " + value(i.paidAt) + "\nDemo payment", normal));
      else doc.add(new Paragraph("\nPlease open your account to select a payment method and pay this invoice.", normal));
      doc.add(new Paragraph("\nThank you for choosing AXIOM.", normal));
      doc.close();
      return out.toByteArray();
    } catch (Exception e) { throw new IllegalStateException("Unable to generate PDF", e); }
  }
  private String value(Object v) { return v == null ? "" : v.toString(); }
}
