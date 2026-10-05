package com.axiom.projects;

import com.axiom.core.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(
  name = "assignment",
  uniqueConstraints = @UniqueConstraint(
    columnNames = { "project_id", "designer_id" }
  )
)
public class Assignment extends BaseEntity {

  public Long projectId;
  public Long designerId;
}
