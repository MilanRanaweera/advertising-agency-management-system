package com.sliit.aams.config;

import com.sliit.aams.useraccount.model.RegisteredCustomer;
import com.sliit.aams.useraccount.model.User;
import com.sliit.aams.useraccount.repository.RegisteredCustomerRepository;
import com.sliit.aams.useraccount.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepository userRepo;
    private final RegisteredCustomerRepository customerRepo;

    @Value("${app.upload.dir:src/main/resources/static/uploads}")
    private String uploadDir;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ApplicationRunner seedAdmin(PasswordEncoder encoder) {
        return args -> {
            // Create upload directories
            new File(uploadDir + "/prototypes").mkdirs();
            new File(uploadDir + "/slips").mkdirs();

            // Seed admin
            if (!userRepo.existsByEmail("admin@aams.com")) {
                User admin = User.builder()
                        .firstName("System")
                        .lastName("Administrator")
                        .email("admin@aams.com")
                        .passwordHash(encoder.encode("admin@123"))
                        .role("ADMIN")
                        .status("ACTIVE")
                        .build();
                userRepo.save(admin);
                System.out.println("[AAMS] Admin seeded: admin@aams.com / admin@123");
            }

            // Seed demo staff users for each role
            String[][] staff = {
                {"Manager", "Silva",      "manager@aams.com",   "manager@123",  "MANAGER"},
                {"Alex",    "Fernando",   "designer@aams.com",  "designer@123", "DESIGNER"},
                {"Priya",   "Mendis",     "mkt@aams.com",       "mkt@123",      "MKT_MANAGER"},
                {"Kasun",   "Rajapaksha", "sales@aams.com",     "sales@123",    "SALES_REP"},
                {"Nimali",  "Perera",     "officer@aams.com",   "officer@123",  "OFFICER"}
            };
            for (String[] s : staff) {
                if (!userRepo.existsByEmail(s[2])) {
                    User u = User.builder()
                            .firstName(s[0]).lastName(s[1])
                            .email(s[2])
                            .passwordHash(encoder.encode(s[3]))
                            .role(s[4]).status("ACTIVE")
                            .build();
                    userRepo.save(u);
                    System.out.println("[AAMS] Staff seeded: " + s[2]);
                }
            }

            // Seed demo customer
            if (!userRepo.existsByEmail("customer@demo.com")) {
                User u = User.builder()
                        .firstName("Demo").lastName("Customer")
                        .email("customer@demo.com")
                        .passwordHash(encoder.encode("customer@123"))
                        .role("CUSTOMER").status("ACTIVE")
                        .build();
                u = userRepo.saveAndFlush(u);
                RegisteredCustomer rc = new RegisteredCustomer();
                rc.setUser(u);
                rc.setCity("Colombo");
                rc.setPostalCode("10001");
                customerRepo.save(rc);
                System.out.println("[AAMS] Demo customer seeded: customer@demo.com / customer@123");
            }
        };
    }
}