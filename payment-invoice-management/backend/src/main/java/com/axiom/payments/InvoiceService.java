package com.axiom.payments;

import com.axiom.core.*;
import com.axiom.projects.*;
import com.axiom.quotes.*;
import com.axiom.users.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InvoiceService {

  private final InvoiceRepository repo;
  private final ProjectRepository projects;
  private final QuotationRepository quotes;
  private final Access access;
  private final PaymentGateway gateway;
  private final MailService mail;
  private final AppUserRepository users;
  private final InvoicePdf pdf;

  public InvoiceService(
    InvoiceRepository r,
    ProjectRepository p,
    QuotationRepository q,
    Access a,
    PaymentGateway g,
    MailService m, AppUserRepository u, InvoicePdf pdf
  ) {
    repo = r;
    projects = p;
    quotes = q;
    access = a;
    gateway = g;
    mail = m;
    users = u;
    this.pdf = pdf;
  }

  public List<Invoice> list() {
    access.role("FINANCE_STAFF", "FINANCE_MANAGER", "CUSTOMER");
    return repo
      .findAll()
      .stream()
      .filter(i -> !i.deleted || (access.is("CUSTOMER") && "PAID".equals(i.status)))
      .filter(
        i -> !access.is("CUSTOMER") || (i.customerId.equals(access.current().id) && Set.of("SENT", "PAID").contains(i.status))
      )
      .toList();
  }

  public Invoice get(Long id) {
    var i = Rules.found(repo.findById(id));
    Rules.access(
      list()
        .stream()
        .anyMatch(x -> x.id.equals(id))
    );
    return i;
  }

  public Invoice createWithBilling(Long projectId, String name, String address) {
    var invoice = create(projectId);
    return billing(invoice.id, name, address);
  }

  public Invoice create(Long projectId) {
    access.role("FINANCE_STAFF");
    var p = Rules.found(projects.findById(projectId));
    Rules.require("COMPLETED".equals(p.status), "Project Manager must complete the project first");
    Rules.require(repo.findAll().stream().noneMatch(i -> i.projectId.equals(projectId)), "Invoice already exists");
    return prepareCompleted(p);
  }

  public Invoice prepareCompleted(Project p) {
    access.role("PROJECT_MANAGER", "FINANCE_STAFF");
    Rules.require("COMPLETED".equals(p.status), "Project is not completed");
    var existing = repo.findAll().stream().filter(i -> i.projectId.equals(p.id)).findFirst();
    if (existing.isPresent()) return existing.get();
    var customer = Rules.found(users.findById(p.customerId));
    var i = new Invoice();
    i.projectId = p.id;
    i.customerId = p.customerId;
    i.projectTitle = p.title;
    i.customerName = customer.name;
    i.customerEmail = customer.email;
    i.billingName = customer.name;
    i.billingAddress = customer.address;
    i.contactNo = customer.contactNo;
    i.amount = Rules.found(quotes.findById(p.quotationId)).total;
    i.status = "DRAFT";
    return repo.save(i);
  }

  public Invoice billing(Long id, String name, String address) {
    var i = get(id);
    access.role("FINANCE_STAFF");
    Rules.require(
      Set.of("DRAFT", "UNPAID", "CHANGES").contains(i.status),
      "Billing cannot be edited in this state"
    );
    Rules.require(name != null && name.length() <= 255 && address != null && address.length() <= 255, "Billing name and address must be at most 255 characters");
    i.billingName = Rules.text(name);
    i.billingAddress = Rules.text(address);
    return repo.save(i);
  }

  public Invoice action(Long id, String action, String feedback, String method) {
    var i = get(id);
    switch (action) {
      case "approve", "resubmit" -> {
        access.role("FINANCE_STAFF");
        Rules.require(Set.of("DRAFT", "UNPAID", "CHANGES").contains(i.status), "Invoice is not awaiting billing review");
        Rules.require("COMPLETED".equals(Rules.found(projects.findById(i.projectId)).status), "Complete the project first");
        Rules.text(i.billingName); Rules.text(i.billingAddress);
        i.feedback = null;
        i.status = "REVIEW";
      }
      case "generate" -> {
        access.role("FINANCE_MANAGER");
        Rules.require("REVIEW".equals(i.status), "Finance staff must approve billing first");
        i.status = "GENERATED";
      }
      case "return" -> {
        access.role("FINANCE_MANAGER");
        Rules.require(Set.of("REVIEW", "GENERATED").contains(i.status), "Invoice is not in review");
        i.feedback = Rules.text(feedback);
        i.status = "CHANGES";
      }
      case "send" -> {
        access.role("FINANCE_MANAGER");
        Rules.require("GENERATED".equals(i.status), "Generate the invoice before sending");
        mail.deliver(i.customerId, "Invoice #" + i.id, document(i), pdf.render(i, false), "invoice-" + i.id + ".pdf");
        i.status = "SENT";
      }
      case "pay" -> {
        access.role("CUSTOMER");
        Rules.require("SENT".equals(i.status), "Payment requires a sent, unpaid invoice");
        Rules.require(method != null && Set.of("DEMO_CARD", "DEMO_BANK_TRANSFER", "VISA", "MASTERCARD", "BANK_TRANSFER").contains(method), "Select a demo payment method");
        i.paymentReference = gateway.pay(i.id, i.amount);
        i.paymentMethod = method;
        i.paidAt = java.time.Instant.now();
        i.status = "PAID";
        mail.deliver(i.customerId, "Payment receipt #" + i.id, receipt(i), pdf.render(i, true), "receipt-" + i.id + ".pdf");
      }
      default -> throw new IllegalArgumentException();
    }
    return repo.save(i);
  }

  public byte[] pdf(Long id, boolean receipt) {
    var i = get(id);
    Rules.require(receipt ? "PAID".equals(i.status) : Set.of("GENERATED", "SENT", "PAID").contains(i.status),
      receipt ? "Pay the invoice first" : "Generate the invoice first");
    return pdf.render(i, receipt);
  }

  public String receipt(Invoice i) {
    Rules.require("PAID".equals(i.status), "Receipt is available after payment");
    return Documents.render("Payment receipt (demo)", i.id, title(i),
      "Billing address: " + i.billingAddress + "\nPayment method: " + i.paymentMethod +
      "\nPayment reference: " + i.paymentReference + "\nPaid at: " + i.paidAt,
      i.amount, i.billingName);
  }

  private String title(Invoice i) {
    return i.projectTitle == null ? "Project #" + i.projectId : i.projectTitle + " (#" + i.projectId + ")";
  }

  public String document(Invoice i) {
    return Documents.render(
      "Invoice",
      i.id,
      title(i),
      "Billing address: " +
        i.billingAddress +
        "\nPayment reference: " +
        i.paymentReference,
      i.amount,
      i.billingName
    );
  }

  public void delete(Long id) {
    access.role("FINANCE_STAFF", "FINANCE_MANAGER");
    var i = get(id);
    if (access.is("FINANCE_MANAGER")) {
      i.deleted = true;
      repo.save(i);
    } else {
      Rules.require(Set.of("DRAFT", "UNPAID", "CHANGES").contains(i.status), "Only draft or returned billing records can be deleted");
      repo.delete(i);
    }
  }
}
