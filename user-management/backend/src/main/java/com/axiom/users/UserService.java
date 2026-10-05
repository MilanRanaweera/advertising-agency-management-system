package com.axiom.users;

import com.axiom.core.Rules;
import java.util.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@org.springframework.transaction.annotation.Transactional
public class UserService {

  public static final Set<String> ROLES = Set.of(
    "ADMIN",
    "SERVICE_STAFF",
    "MARKETING_MANAGER",
    "QUOTATION_STAFF",
    "FINANCE_MANAGER",
    "DESIGNER",
    "PROJECT_MANAGER",
    "FINANCE_STAFF",
    "COMMUNICATION_STAFF",
    "CUSTOMER_RELATIONS_OFFICER",
    "CUSTOMER"
  );
  private final AppUserRepository repo;
  private final PasswordEncoder encoder;

  public UserService(AppUserRepository r, PasswordEncoder e) {
    repo = r;
    encoder = e;
  }

  public AppUser create(
    String name,
    String email,
    String password,
    String role
  ) {
    Rules.require(ROLES.contains(role), "Unknown role");
    Rules.require(
      password != null && password.length() >= 10,
      "Password must contain at least 10 characters"
    );
    var u = new AppUser();
    u.name = Rules.text(name);
    u.email = Rules.text(email).toLowerCase();
    Rules.require(
      u.email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+"),
      "Invalid email"
    );
    u.passwordHash = encoder.encode(password);
    u.role = role;
    u.active = true;
    u.capacity = 3;
    return repo.save(u);
  }

  public AppUser register(String name, String email, String password, String role,
      String address, String contactNo, String sex, java.time.LocalDate birthDate) {
    var u = create(name, email, password, role);
    profile(u, address, contactNo, sex, birthDate);
    return repo.save(u);
  }

  private void profile(AppUser u, String address, String contactNo, String sex, java.time.LocalDate birthDate) {
    Rules.require(address == null || address.length() <= 255, "Address must be at most 255 characters");
    Rules.require(contactNo == null || contactNo.isBlank() || contactNo.matches("[+0-9() .-]{7,30}"), "Enter a valid contact number");
    Rules.require(sex == null || sex.isBlank() || Set.of("M", "F").contains(sex), "Select M or F");
    Rules.require(birthDate == null || !birthDate.isAfter(java.time.LocalDate.now()), "Birth date cannot be in the future");
    u.address = address == null ? null : address.trim();
    u.contactNo = contactNo == null ? null : contactNo.trim();
    u.sex = sex;
    u.birthDate = birthDate;
  }

  public List<AppUser> list() {
    return repo.findAll().stream().filter(u -> !u.deleted).toList();
  }

  public AppUser update(Long id, AppUser input, Long actor) {
    var u = Rules.found(repo.findById(id));
    Rules.require(!u.deleted, "Account has been deleted");
    Rules.require(ROLES.contains(input.role), "Unknown role");
    Rules.require(
      !id.equals(actor) || ("ADMIN".equals(input.role) && input.active),
      "Do not disable or demote your own admin account"
    );
    u.name = Rules.text(input.name);
    u.role = input.role;
    u.active = input.active;
    u.capacity = Math.max(1, input.capacity);
    profile(u, input.address, input.contactNo, input.sex, input.birthDate);
    return repo.save(u);
  }

  public void delete(Long id, Long actor) {
    Rules.require(!id.equals(actor), "Cannot delete yourself");
    var u = Rules.found(repo.findById(id));
    try {
      repo.delete(u);
      repo.flush();
    } catch (org.springframework.dao.DataIntegrityViolationException e) {
      throw new org.springframework.web.server.ResponseStatusException(
        org.springframework.http.HttpStatus.CONFLICT,
        "Cannot delete this account because it is linked to business records. Deactivate it using Edit instead.",
        e
      );
    }
  }

  public AppUser capacity(Long id, int capacity) {
    var u = Rules.found(repo.findById(id));
    Rules.require(!u.deleted && u.active && "DESIGNER".equals(u.role), "Select an active designer");
    Rules.require(capacity >= 1 && capacity <= 100, "Capacity must be 1–100");
    u.capacity = capacity;
    return repo.save(u);
  }
}
