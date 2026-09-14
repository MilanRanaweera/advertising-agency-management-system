package com.sliit.aams.useraccount.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * User Account Management
 * JPA entity representing a user account in the system.
 */
@Entity
@Table(name = "useraccount_user")
public class User {

    // User roles
    public enum Role {
        ADMIN,
        CLIENT,
        STAFF
    }

    // Account status
    public enum Status {
        ACTIVE,
        INACTIVE
    }

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // First Name
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    // Last Name
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    // Email - must be unique
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    @Size(max = 120, message = "Email must not exceed 120 characters")
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    // Phone Number
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    @Pattern(
            regexp = "^[+]?[0-9]{7,15}$",
            message = "Please enter a valid phone number"
    )
    @Column(length = 15)
    private String phone;

    // User Role
    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role = Role.CLIENT;

    // Account Status
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Status status = Status.ACTIVE;

    // Password
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must contain at least 8 characters")
    @Column(nullable = false, length = 255)

    // Password can be received from requests,
    // but it will not be returned in JSON responses.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    // Account creation time
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Last update time
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    // Runs automatically before a new user is saved
    @PrePersist
    protected void onCreate() {

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (role == null) {
            role = Role.CLIENT;
        }

        if (status == null) {
            status = Status.ACTIVE;
        }
    }

    // Runs automatically when user data is updated
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    // =========================
    // Getters and Setters
    // =========================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}