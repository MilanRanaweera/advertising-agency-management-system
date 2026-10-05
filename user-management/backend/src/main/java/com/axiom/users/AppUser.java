package com.axiom.users;

import com.axiom.core.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "app_user")
public class AppUser extends BaseEntity {

  @Column(length = 255)
  public String name;

  @Column(length = 255)
  public String email;

  @com.fasterxml.jackson.annotation.JsonIgnore
  @Column(length = 255)
  public String passwordHash;

  @Column(length = 255)
  public String role;

  public String address;
  public String contactNo;
  public String sex;
  public java.time.LocalDate birthDate;

  public boolean active;
  public boolean deleted;
  public int capacity;
}
