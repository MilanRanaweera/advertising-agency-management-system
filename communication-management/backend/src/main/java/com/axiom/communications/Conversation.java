package com.axiom.communications;

import com.axiom.core.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "conversation")
public class Conversation extends BaseEntity {

  public Long customerId;

  @Column(length = 255)
  public String subject;

  @Column(length = 10000)
  public String message;

  @Column(length = 10000)
  public String reply;

  @Column(length = 255)
  public String kind;

  @Column(length = 255)
  public String appointmentAt;

  @Column(length = 255)
  public String status;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "conversation_reply", joinColumns = @JoinColumn(name = "conversation_id"))
  @OrderColumn(name = "reply_order")
  public java.util.List<Reply> replies = new java.util.ArrayList<>();

  @Embeddable
  public static class Reply {
    public Long authorId;
    public String authorName;
    public String authorRole;
    @Column(length = 10000)
    public String body;
    public java.time.Instant sentAt;
  }
}
