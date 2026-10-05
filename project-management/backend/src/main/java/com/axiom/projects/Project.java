package com.axiom.projects;

import com.axiom.core.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "project")
public class Project extends BaseEntity {

  public Long quotationId;
  public Long customerId;

  @Column(length = 255)
  public String title;

  @Column(length = 255)
  public String status;

  public int progress;
  public boolean prototypeApproved;
  public boolean clientApproved;
}
