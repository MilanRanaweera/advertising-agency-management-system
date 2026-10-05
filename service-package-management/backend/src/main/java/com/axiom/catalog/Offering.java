package com.axiom.catalog;

import com.axiom.core.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "offering")
public class Offering extends BaseEntity {

  @Column(length = 255)
  public String name;

  @Column(length = 255)
  public String kind;

  @Column(length = 10000)
  public String description;

  @Column(length = 10000)
  public String deliverables;

  public java.math.BigDecimal price;

  @Column(length = 255)
  public String status;

  @Column(length = 10000)
  public String feedback;
}
