package com.axiom.quotes;

import com.axiom.catalog.*;
import com.axiom.core.*;
import com.axiom.users.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuotationService {

  private final QuotationRepository repo;
  private final OfferingRepository catalog;
  private final Access access;
  private final MailService mail;
  private final QuotationPdf pdf;
  private final AppUserRepository users;

  public QuotationService(
    QuotationRepository r,
    OfferingRepository c,
    Access a,
    MailService m,
    QuotationPdf pdf,
    AppUserRepository users
  ) {
    repo = r;
    catalog = c;
    access = a;
    mail = m;
    this.pdf = pdf;
    this.users = users;
  }

  public record Line(Long offeringId, int quantity) {}

  public record Brief(String title, String requirements, List<Line> items) {}

  public List<Quotation> list() {
    access.role("QUOTATION_STAFF", "FINANCE_MANAGER", "CUSTOMER", "PROJECT_MANAGER");
    return repo
      .findAll()
      .stream()
      .filter(
        q -> !access.is("CUSTOMER") || q.customerId.equals(access.current().id)
      )
      .toList();
  }

  public Quotation get(Long id) {
    var q = Rules.found(repo.findById(id));
    Rules.access(
      list()
        .stream()
        .anyMatch(x -> x.id.equals(id))
    );
    return q;
  }

  public Quotation create(Brief b) {
    access.role("CUSTOMER");
    Rules.require(
      b.items() != null && !b.items().isEmpty() && b.items().size() <= 30,
      "Select 1–30 offerings"
    );
    var q = new Quotation();
    q.customerId = access.current().id;
    q.title = Rules.text(b.title());
    q.requirements = Rules.text(b.requirements());
    q.total = BigDecimal.ZERO;
    var lines = new StringBuilder();
    for (var l : b.items()) {
      var o = Rules.found(catalog.findById(l.offeringId()));
      Rules.require(
        "PUBLISHED".equals(o.status) && l.quantity() > 0 && l.quantity() <= 100,
        "Invalid selection"
      );
      var amount = o.price.multiply(BigDecimal.valueOf(l.quantity()));
      q.total = q.total.add(amount);
      lines
        .append(o.name)
        .append(" × ")
        .append(l.quantity())
        .append(" — LKR ")
        .append(amount)
        .append("\n");
    }
    q.lines = lines.toString();
    q.status = "REQUESTED";
    return repo.save(q);
  }

  public Quotation edit(Long id, Map<String, String> b) {
    access.role("QUOTATION_STAFF");
    var q = get(id);
    Rules.require(
      Set.of("REQUESTED", "CHANGES").contains(q.status),
      "Cannot edit after submission"
    );
    q.requirements = Rules.text(b.get("requirements"));
    BigDecimal amount;
    try {
      String value = b.get("additionalAmount");
      amount = value == null || value.isBlank() ? BigDecimal.ZERO : new BigDecimal(value);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Enter a valid additional price");
    }
    Rules.require(amount.signum() >= 0 && amount.compareTo(new BigDecimal("100000000")) <= 0 && amount.scale() <= 2, "Additional price must be between 0 and 100,000,000 with at most two decimals");
    q.additionalDescription = amount.signum() > 0 ? Rules.text(b.get("additionalDescription")) : "";
    q.total = q.total.subtract(q.additionalAmount == null ? BigDecimal.ZERO : q.additionalAmount).add(amount);
    q.additionalAmount = amount;
    return repo.save(q);
  }

  public Quotation action(Long id, String action, String feedback) {
    var q = get(id);
    switch (action) {
      case "generate" -> {
        access.role("QUOTATION_STAFF");
        Rules.require(
          Set.of("REQUESTED", "CHANGES").contains(q.status),
          "Not ready to generate"
        );
        q.status = "REVIEW";
      }
      case "approve" -> {
        access.role("FINANCE_MANAGER");
        Rules.require("REVIEW".equals(q.status), "Not in review");
        q.status = "APPROVED";
      }
      case "return" -> {
        access.role("FINANCE_MANAGER");
        Rules.require("REVIEW".equals(q.status), "Not in review");
        q.feedback = Rules.text(feedback);
        q.status = "CHANGES";
      }
      case "send" -> {
        access.role("FINANCE_MANAGER");
        Rules.require(Set.of("APPROVED", "SENT", "ACCEPTED").contains(q.status), "Approve first");
        mail.deliver(q.customerId, "Quotation #" + q.id,
          "<p>Your approved AXIOM quotation is attached. Sign in to review and accept it.</p>",
          document(q), "AXIOM-Quotation-" + q.id + ".pdf");
        if (!"ACCEPTED".equals(q.status)) q.status = "SENT";
      }
      case "accept" -> {
        access.role("CUSTOMER");
        access.owner(q.customerId);
        Rules.require(Set.of("APPROVED", "SENT").contains(q.status), "Quotation is not approved yet");
        q.status = "ACCEPTED";
      }
      default -> throw new IllegalArgumentException();
    }
    return repo.save(q);
  }

  public byte[] document(Quotation q) {
    access.role("CUSTOMER", "QUOTATION_STAFF", "FINANCE_MANAGER");
    if (access.is("CUSTOMER")) access.owner(q.customerId);
    Rules.access(Set.of("APPROVED", "SENT", "ACCEPTED").contains(q.status));
    return pdf.render(q, Rules.found(users.findById(q.customerId)));
  }

  public void delete(Long id) {
    var q = get(id);
    access.role("CUSTOMER", "QUOTATION_STAFF");
    Rules.require(
      "REQUESTED".equals(q.status),
      "Only unprocessed requests can be deleted"
    );
    repo.delete(q);
  }
}
