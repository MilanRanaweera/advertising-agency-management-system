package com.axiom.quotes;

import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
public class QuotationController {

  private final QuotationService s;

  public QuotationController(QuotationService s) {
    this.s = s;
  }

  @GetMapping
  List<Quotation> list() {
    return s.list();
  }

  @PostMapping
  Quotation create(@RequestBody QuotationService.Brief b) {
    return s.create(b);
  }

  @PutMapping("/{id}")
  Quotation edit(@PathVariable Long id, @RequestBody Map<String, String> b) {
    return s.edit(id, b);
  }

  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    s.delete(id);
  }

  @PostMapping("/{id}/{action}")
  Quotation action(
    @PathVariable Long id,
    @PathVariable String action,
    @RequestBody Map<String, String> b
  ) {
    return s.action(id, action, b.get("feedback"));
  }

  @GetMapping(value = "/{id}/document", produces = "application/pdf")
  org.springframework.http.ResponseEntity<byte[]> document(@PathVariable Long id) {
    var q = s.get(id);
    return org.springframework.http.ResponseEntity.ok()
      .header("Content-Disposition", "attachment; filename=AXIOM-Quotation-" + id + ".pdf")
      .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
      .body(s.document(q));
  }
}
