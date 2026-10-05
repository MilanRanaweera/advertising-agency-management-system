package com.axiom.communications;

import com.axiom.core.*;
import com.axiom.users.*;
import java.util.*;
import org.springframework.stereotype.Service;

@Service
@org.springframework.transaction.annotation.Transactional
public class CommunicationService {

  private final ConversationRepository repo;
  private final Access access;

  public CommunicationService(ConversationRepository r, Access a) {
    repo = r;
    access = a;
  }

  public List<Conversation> list() {
    access.role("COMMUNICATION_STAFF", "CUSTOMER_RELATIONS_OFFICER", "CUSTOMER");
    return repo
      .findAll()
      .stream()
      .filter(
        c -> !access.is("CUSTOMER") || c.customerId.equals(access.current().id)
      )
      .toList();
  }

  public Conversation get(Long id) {
    var c = Rules.found(repo.findById(id));
    Rules.access(
      list()
        .stream()
        .anyMatch(x -> x.id.equals(id))
    );
    return c;
  }

  public Conversation save(Long id, Conversation b) {
    access.role("CUSTOMER", "COMMUNICATION_STAFF", "CUSTOMER_RELATIONS_OFFICER");
    var c = id == null ? new Conversation() : get(id);
    if (id == null) {
      c.customerId = access.current().id;
      c.status = "OPEN";
    }
    Rules.require(
      "OPEN".equals(c.status),
      "Closed or confirmed conversations cannot be edited"
    );
    c.subject = Rules.text(b.subject);
    c.message = Rules.text(b.message);
    Rules.require(
      Set.of("MESSAGE", "CONSULTATION").contains(b.kind),
      "Invalid kind"
    );
    c.kind = b.kind;
    c.appointmentAt = b.appointmentAt;
    if ("CONSULTATION".equals(c.kind)) {
      Rules.require(
        c.appointmentAt != null && !c.appointmentAt.isBlank(),
        "Appointment date required"
      );
      Rules.require(
        java.time.LocalDateTime.parse(c.appointmentAt).isAfter(
          java.time.LocalDateTime.now(java.time.ZoneId.of("Asia/Colombo"))
        ),
        "Appointment must be in the future (Sri Lanka time)"
      );
    }
    return repo.save(c);
  }

  public Conversation action(Long id, String action, String reply) {
    var c = get(id);
    switch (action) {
      case "reply" -> {
        access.role("COMMUNICATION_STAFF", "CUSTOMER_RELATIONS_OFFICER", "CUSTOMER");
        Rules.require(Set.of("OPEN", "CONFIRMED").contains(c.status), "Solved conversations cannot receive replies");
        if (access.is("CUSTOMER")) Rules.require(
          (c.reply != null && !c.reply.isBlank()) || c.replies.stream().anyMatch(r -> !"CUSTOMER".equals(r.authorRole)),
          "Wait for a staff response before replying");
        var entry = new Conversation.Reply();
        var author = access.current();
        entry.authorId = author.id;
        entry.authorName = author.name;
        entry.authorRole = author.role;
        entry.body = Rules.text(reply);
        entry.sentAt = java.time.Instant.now();
        c.replies.add(entry);
      }
      case "confirm" -> {
        access.role("CUSTOMER_RELATIONS_OFFICER");
        Rules.require(
          "CONSULTATION".equals(c.kind) && "OPEN".equals(c.status),
          "No pending appointment"
        );
        c.status = "CONFIRMED";
      }
      case "close", "solve" -> {
        access.role("COMMUNICATION_STAFF", "CUSTOMER_RELATIONS_OFFICER");
        Rules.require(Set.of("OPEN", "CONFIRMED").contains(c.status), "Conversation is already solved");
        c.status = "SOLVED";
      }
      default -> throw new IllegalArgumentException();
    }
    return repo.save(c);
  }

  public void delete(Long id) {
    access.role("CUSTOMER", "CUSTOMER_RELATIONS_OFFICER");
    var c = get(id);
    Rules.require("OPEN".equals(c.status), "Only open records can be deleted");
    repo.delete(c);
  }
}
