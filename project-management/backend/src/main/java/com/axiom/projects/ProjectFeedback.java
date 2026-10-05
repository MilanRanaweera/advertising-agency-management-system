package com.axiom.projects;

import com.axiom.core.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "project_feedback")
public class ProjectFeedback extends BaseEntity {

  public Long projectId;
  public String authorRole;

  @Column(length = 10000)
  public String message;

  public boolean approved;
}
