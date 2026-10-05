package com.axiom.communications;

import com.axiom.core.*;
import com.axiom.users.Access;
import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/communications")
public class CommunicationController {

  private final CommunicationService s;
  private final OutboxRepository outbox;
  private final Access access;

  public CommunicationController(
    CommunicationService s,
    OutboxRepository o,
    Access a
  ) {
    this.s = s;
    outbox = o;
    access = a;
  }

  @GetMapping
  List<Conversation> list() {
    return s.list();
  }

  @PostMapping
  Conversation create(@RequestBody Conversation c) {
    return s.save(null, c);
  }

  @PutMapping("/{id}")
  Conversation update(@PathVariable Long id, @RequestBody Conversation c) {
    return s.save(id, c);
  }

  @DeleteMapping("/{id}")
  void delete(@PathVariable Long id) {
    s.delete(id);
  }

  @PostMapping("/{id}/{action}")
  Conversation action(
    @PathVariable Long id,
    @PathVariable String action,
    @RequestBody Map<String, String> b
  ) {
    return s.action(id, action, b.get("reply"));
  }

  @GetMapping("/outbox")
  List<OutboxMail> outbox() {
    access.role(
      "FINANCE_STAFF",
      "FINANCE_MANAGER",
      "COMMUNICATION_STAFF",
      "CUSTOMER_RELATIONS_OFFICER",
      "CUSTOMER"
    );
    return outbox
      .findAll()
      .stream()
      .filter(
        m ->
          !access.is("CUSTOMER") || m.recipient.equals(access.current().email)
      )
      .toList();
  }
}
