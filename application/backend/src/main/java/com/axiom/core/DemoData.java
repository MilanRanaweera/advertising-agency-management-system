package com.axiom.core;

import com.axiom.catalog.*;
import com.axiom.users.*;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DemoData implements CommandLineRunner {

  private final UserService users;
  private final AppUserRepository repo;
  private final OfferingRepository catalog;
  private final org.springframework.security.crypto.password.PasswordEncoder encoder;

  @Value("${axiom.demo:false}")
  boolean demo;

  public DemoData(UserService u, AppUserRepository r, OfferingRepository c, org.springframework.security.crypto.password.PasswordEncoder e) {
    users = u;
    repo = r;
    catalog = c;
    encoder = e;
  }

  @Override
  public void run(String... args) {
    if (!demo) return;
    for (String role : UserService.ROLES) {
      String email = role.toLowerCase() + "@axiom.test";
      if (repo.findByEmail(email).isEmpty()) users.create(
        role.replace('_', ' '),
        email,
        role.equals("ADMIN") ? "admin@2026" : "AxiomDemo123!",
        role
      );
    }
    // Migrate only the original demo administrator password, never a custom password.
    repo.findByEmail("admin@axiom.test").ifPresent(admin -> {
      if ("ADMIN".equals(admin.role) && encoder.matches("AxiomDemo123!", admin.passwordHash)) {
        admin.passwordHash = encoder.encode("admin@2026");
        repo.save(admin);
      }
    });
    if (catalog.count() == 0) {
      String[] names = {
        "Brand identity",
        "Digital experience",
        "Social campaign",
        "Launch",
        "Growth",
        "Signature",
      };
      int[] prices = { 45000, 120000, 35000, 65000, 95000, 185000 };
      for (int n = 0; n < names.length; n++) {
        var o = new Offering();
        o.name = names[n];
        o.kind = n < 3 ? "Service" : "Package";
        o.description =
          "Creative direction and a tailored design process for your brand.";
        o.deliverables =
          "Discovery, design concepts, two revision rounds, final delivery";
        o.price = BigDecimal.valueOf(prices[n]);
        o.status = "PUBLISHED";
        catalog.save(o);
      }
    }
  }
}
