package com.axiom.projects;

import com.axiom.core.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "asset")
public class Asset extends BaseEntity {

  public Long projectId;
  public String kind;
  public String originalName;

  @com.fasterxml.jackson.annotation.JsonIgnore
  public String storageName;

  public String contentType;
}
