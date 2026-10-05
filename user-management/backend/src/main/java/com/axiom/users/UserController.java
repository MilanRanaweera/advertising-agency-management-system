package com.axiom.users;

import java.util.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

  private final UserService users;
  private final Access access;

  public UserController(UserService u, Access a) {
    users = u;
    access = a;
  }

  public record Registration(
    String name,
    String email,
    String password,
    String role, String address, String contactNo, String sex, java.time.LocalDate birthDate
  ) {}

  @PostMapping("/auth/register")
  AppUser register(@RequestBody Registration r) {
    return users.register(r.name(), r.email(), r.password(), "CUSTOMER", r.address(), r.contactNo(), r.sex(), r.birthDate());
  }

  @GetMapping("/auth/me")
  AppUser me() {
    return access.current();
  }

  @GetMapping("/users")
  List<AppUser> list() {
    access.role("ADMIN");
    return users.list();
  }

  @GetMapping("/users/designers")
  List<AppUser> designers() {
    access.role("PROJECT_MANAGER");
    return users
      .list()
      .stream()
      .filter(u -> u.active && u.role.equals("DESIGNER"))
      .toList();
  }

  @PostMapping("/users")
  AppUser create(@RequestBody Registration r) {
    access.role("ADMIN");
    return users.register(r.name(), r.email(), r.password(), r.role(), r.address(), r.contactNo(), r.sex(), r.birthDate());
  }

  @PutMapping("/users/designers/{id}/capacity")
  AppUser capacity(@PathVariable Long id, @RequestBody Map<String, Integer> b) {
    access.role("PROJECT_MANAGER");
    return users.capacity(id, b.getOrDefault("capacity", 0));
  }

  @PutMapping("/users/{id}")
  AppUser update(@PathVariable Long id, @RequestBody AppUser u) {
    access.role("ADMIN");
    return users.update(id, u, access.current().id);
  }

  @DeleteMapping("/users/{id}")
  void delete(@PathVariable Long id) {
    access.role("ADMIN");
    users.delete(id, access.current().id);
  }
}
