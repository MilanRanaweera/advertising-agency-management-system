package com.axiom.quotes;

import com.axiom.core.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "quotation")
public class Quotation extends BaseEntity {

  public Long customerId;

  @Column(length = 255)
  public String title;

  @Column(length = 10000)
  public String requirements;

  @Column(length = 10000)
  public String lines;

  public java.math.BigDecimal total;
  public java.math.BigDecimal additionalAmount = java.math.BigDecimal.ZERO;
  @Column(length = 10000)
  public String additionalDescription;

  @Column(length = 255)
  public String status;

  @Column(length = 10000)
  public String feedback;
}
