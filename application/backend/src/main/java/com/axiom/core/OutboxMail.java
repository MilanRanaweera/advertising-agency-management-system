package com.axiom.core;

import jakarta.persistence.*;

@Entity
@Table(name = "outbox_mail")
public class OutboxMail extends BaseEntity {

  public String recipient;
  public String subject;

  @Column(length = 20000)
  public String body;

  public String status;
}
