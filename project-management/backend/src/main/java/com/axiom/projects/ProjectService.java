package com.axiom.projects;

import com.axiom.core.*;
import com.axiom.quotes.*;
import com.axiom.users.*;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProjectService {

  private final ProjectRepository repo;
  private final QuotationRepository quotes;
  private final AssignmentRepository assignments;
  private final ProjectFeedbackRepository feedback;
  private final Access access;
  private final AppUserRepository users;
  private final AssetRepository assets;
  private final com.axiom.payments.InvoiceService invoices;

  public ProjectService(
    ProjectRepository r,
    QuotationRepository q,
    AssignmentRepository a,
    ProjectFeedbackRepository f,
    Access x,
    AppUserRepository u,
    AssetRepository ar,
    com.axiom.payments.InvoiceService invoiceService
  ) {
    repo = r;
    quotes = q;
    assignments = a;
    feedback = f;
    access = x;
    users = u;
    assets = ar;
    invoices = invoiceService;
  }

  public List<Project> list() {
    access.role(
      "PROJECT_MANAGER",
      "DESIGNER",
      "CUSTOMER",
      "FINANCE_STAFF",
      "FINANCE_MANAGER",
      "COMMUNICATION_STAFF",
      "CUSTOMER_RELATIONS_OFFICER"
    );
    return repo
      .findAll()
      .stream()
      .filter(
        p -> !access.is("CUSTOMER") || p.customerId.equals(access.current().id)
      )
      .filter(
        p ->
          !access.is("DESIGNER") ||
          assignments
            .findByProjectId(p.id)
            .stream()
            .anyMatch(a -> a.designerId.equals(access.current().id))
      )
      .toList();
  }

  public Project get(Long id) {
    var p = Rules.found(repo.findById(id));
    Rules.access(
      list()
        .stream()
        .anyMatch(x -> x.id.equals(id))
    );
    return p;
  }

  public Project create(Long quotationId) {
    access.role("PROJECT_MANAGER");
    var q = Rules.found(quotes.findById(quotationId));
    Rules.require(
      "ACCEPTED".equals(q.status),
      "Customer must accept quotation"
    );
    Rules.require(
      repo
        .findAll()
        .stream()
        .noneMatch(p -> p.quotationId.equals(quotationId)),
      "Project already exists"
    );
    var p = new Project();
    p.quotationId = q.id;
    p.customerId = q.customerId;
    p.title = q.title;
    p.status = "PLANNED";
    return repo.save(p);
  }

  public Project assign(Long id, List<Long> ids) {
    access.role("PROJECT_MANAGER");
    var p = get(id);
    Rules.require(!"COMPLETED".equals(p.status), "Project is completed");
    Rules.require(ids != null && !ids.isEmpty(), "Select designers");
    for (Long uid : new HashSet<>(ids)) {
      var u = Rules.found(users.findById(uid));
      Rules.require(
        u.active && "DESIGNER".equals(u.role),
        "Select an active designer"
      );
      long load = assignments
        .findByDesignerId(uid)
        .stream()
        .filter(a -> !a.projectId.equals(id))
        .filter(a ->
          !"COMPLETED".equals(Rules.found(repo.findById(a.projectId)).status)
        )
        .count();
      Rules.require(
        load < u.capacity,
        "Designer " + u.name + " is at capacity"
      );
    }
    assignments.deleteByProjectId(id);
    assignments.flush();
    for (Long uid : new HashSet<>(ids)) {
      var a = new Assignment();
      a.projectId = id;
      a.designerId = uid;
      assignments.save(a);
    }
    if ("PLANNED".equals(p.status)) p.status = "ACTIVE";
    return repo.save(p);
  }

  public List<Assignment> team(Long id) {
    get(id);
    return assignments.findByProjectId(id);
  }

  public Project progress(Long id, int value) {
    access.role("DESIGNER");
    var p = get(id);
    Rules.require(value >= 0 && value <= 100, "Progress must be 0–100");
    Rules.require(!"COMPLETED".equals(p.status), "Project completed");
    p.progress = value;
    return repo.save(p);
  }

  public Project review(Long id, boolean approved, String message) {
    var p = get(id);
    access.role("CUSTOMER", "PROJECT_MANAGER");
    var f = new ProjectFeedback();
    f.projectId = id;
    f.authorRole = access.current().role;
    f.message = Rules.text(message);
    f.approved = approved;
    if (access.is("CUSTOMER")) {
      Rules.require(
        "CLIENT_REVIEW".equals(p.status),
        "No prototype awaiting review"
      );
      p.clientApproved = approved;
      p.status = "DIRECTOR_REVIEW";
    } else {
      Rules.require(
        "DIRECTOR_REVIEW".equals(p.status),
        "Wait for customer review"
      );
      Rules.require(
        !approved || p.clientApproved,
        "Customer requested revisions"
      );
      p.prototypeApproved = approved;
      p.status = approved ? "ACTIVE" : "REVISION";
    }
    feedback.save(f);
    return repo.save(p);
  }

  public List<ProjectFeedback> feedback(Long id) {
    get(id);
    return feedback
      .findByProjectId(id)
      .stream()
      .filter(f -> !access.is("CUSTOMER") || "CUSTOMER".equals(f.authorRole))
      .toList();
  }

  public void delete(Long id) {
    access.role("PROJECT_MANAGER");
    var p = get(id);
    Rules.require(
      "PLANNED".equals(p.status),
      "Only unstarted projects can be deleted"
    );
    repo.delete(p);
  }

  public Project complete(Long id) {
    access.role("PROJECT_MANAGER");
    var p = get(id);
    Rules.require("FINAL_REVIEW".equals(p.status) && p.progress == 100 && p.prototypeApproved,
      "Final design and 100% progress are required before completion");
    Rules.require(assets.findByProjectId(id).stream().anyMatch(a -> "ORIGINAL".equals(a.kind)),
      "Upload the final design first");
    p.status = "COMPLETED";
    repo.save(p);
    invoices.prepareCompleted(p);
    return p;
  }
}
