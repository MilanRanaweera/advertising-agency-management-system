package com.axiom.payments;

import com.axiom.core.Rules;
import com.axiom.users.Access;
import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

  private final InvoiceService s;
  private final Access access;

  public InvoiceController(InvoiceService s, Access a) {
    this.s = s;
    access = a;
  }

  public record Request(Long projectId, String billingName, String billingAddress) {}

  public record Billing(String billingName, String billingAddress) {}

  @GetMapping
  List<Invoice> list() {
    return s.list();
  }

  @PostMapping
  Invoice create(@RequestBody Request b) {
    return s.createWithBilling(b.projectId(), b.billingName(), b.billingAddress());
  }

  @PutMapping("/{id}")
  Invoice update(@PathVariable Long id, @RequestBody Billing b) {
    return s.billing(id, b.billingName(), b.billingAddress());
  }

  @PostMapping("/{id}/{action}")
  Invoice action(
    @PathVariable Long id,
    @PathVariable String action,
    @RequestBody Map<String, String> b
  ) {
    return s.action(id, action, b.get("feedback"), b.get("paymentMethod"));
  }

  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    s.delete(id);
  }

  @GetMapping(value = "/{id}/receipt", produces = "application/pdf")
  org.springframework.http.ResponseEntity<byte[]> receipt(@PathVariable Long id) { return download(id, true); }

  @GetMapping(value = "/{id}/document", produces = "application/pdf")
  org.springframework.http.ResponseEntity<byte[]> document(@PathVariable Long id) { return download(id, false); }

  private org.springframework.http.ResponseEntity<byte[]> download(Long id, boolean receipt) {
    return org.springframework.http.ResponseEntity.ok()
      .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
      .header("Content-Disposition", "attachment; filename=" + (receipt ? "receipt-" : "invoice-") + id + ".pdf")
      .body(s.pdf(id, receipt));
  }
}
