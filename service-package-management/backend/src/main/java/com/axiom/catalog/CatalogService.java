package com.axiom.catalog;

import com.axiom.core.Rules;
import com.axiom.users.Access;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {

  private final OfferingRepository repo;
  private final Access access;

  public CatalogService(OfferingRepository r, Access a) {
    repo = r;
    access = a;
  }

  public List<Offering> published() {
    return repo
      .findAll()
      .stream()
      .filter(o -> "PUBLISHED".equals(o.status))
      .toList();
  }

  public List<Offering> all() {
    access.role("SERVICE_STAFF", "MARKETING_MANAGER");
    return repo.findAll();
  }

  public Offering save(Long id, Offering in) {
    access.role("SERVICE_STAFF");
    var o = id == null ? new Offering() : Rules.found(repo.findById(id));
    Rules.require(
      id == null || Set.of("DRAFT", "CHANGES").contains(o.status),
      "Return to draft before editing"
    );
    o.name = Rules.text(in.name);
    o.kind = Rules.text(in.kind);
    Rules.require(
      Set.of("Service", "Package", "Promotion").contains(o.kind),
      "Invalid type"
    );
    o.description = Rules.text(in.description);
    o.deliverables = Rules.text(in.deliverables);
    Rules.require(
      in.price != null && in.price.compareTo(BigDecimal.ZERO) > 0,
      "Price must be positive"
    );
    o.price = in.price;
    o.status = "DRAFT";
    return repo.save(o);
  }

  public Offering action(Long id, String action, String feedback) {
    var o = Rules.found(repo.findById(id));
    switch (action) {
      case "submit" -> {
        access.role("SERVICE_STAFF");
        Rules.require(
          Set.of("DRAFT", "CHANGES").contains(o.status),
          "Cannot submit this state"
        );
        o.status = "REVIEW";
      }
      case "approve" -> {
        access.role("MARKETING_MANAGER");
        Rules.require("REVIEW".equals(o.status), "Not in review");
        o.status = "PUBLISHED";
      }
      case "return" -> {
        access.role("MARKETING_MANAGER");
        Rules.require(
          Set.of("REVIEW", "PUBLISHED").contains(o.status),
          "Not reviewable"
        );
        o.feedback = Rules.text(feedback);
        o.status = "CHANGES";
      }
      default -> throw new IllegalArgumentException();
    }
    return repo.save(o);
  }

  public void delete(Long id) {
    access.role("SERVICE_STAFF", "MARKETING_MANAGER");
    var o = Rules.found(repo.findById(id));
    Rules.require(
      access.is("MARKETING_MANAGER") || Set.of("DRAFT", "CHANGES").contains(o.status),
      "Only drafts can be deleted"
    );
    repo.delete(o);
  }
}
