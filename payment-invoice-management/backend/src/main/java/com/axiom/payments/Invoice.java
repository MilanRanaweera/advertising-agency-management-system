package com.axiom.payments;

import com.axiom.core.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "invoice")
public class Invoice extends BaseEntity {

  public Long projectId;
  public Long customerId;
  public java.math.BigDecimal amount;

  @Column(length = 255)
  public String billingName;

  @Column(length = 255)
  public String billingAddress;

  @Column(length = 255)
  public String status;

  @Column(length = 10000)
  public String feedback;

  @Column(length = 255)
  public String paymentReference;

  public String projectTitle;
  public String customerName;
  public String customerEmail;
  public String contactNo;
  public boolean deleted;
  public String paymentMethod;
  public java.time.Instant paidAt;
}
