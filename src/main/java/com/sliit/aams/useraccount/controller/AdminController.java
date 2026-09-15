package com.sliit.aams.useraccount.controller;

import com.sliit.aams.common.exception.ApiResponse;
import com.sliit.aams.useraccount.model.RegisteredCustomer;
import com.sliit.aams.useraccount.model.User;
import com.sliit.aams.useraccount.repository.RegisteredCustomerRepository;
import com.sliit.aams.useraccount.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepo;
    private final RegisteredCustomerRepository customerRepo;
    private final PasswordEncoder encoder;

    /** List all staff (non-customer) */
    @GetMapping("/staff")
    public ApiResponse<List<User>> listStaff() {
        return ApiResponse.ok(userRepo.findByRoleNot("CUSTOMER"));
    }

    /** List all customers */
    @GetMapping("/customers")
    public ApiResponse<List<User>> listCustomers() {
        return ApiResponse.ok(userRepo.findByRole("CUSTOMER"));
    }

    /** Create staff or customer */
    @PostMapping("/users")
    public ApiResponse<User> createUser(@RequestBody @Valid CreateUserRequest req) {
        if (userRepo.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already in use");

        User u = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .passwordHash(encoder.encode(req.getPassword()))
                .role(req.getRole() != null ? req.getRole() : "CUSTOMER")
                .status("ACTIVE")
                .build();
        u = userRepo.saveAndFlush(u);

        // If customer, create profile too
        if ("CUSTOMER".equals(u.getRole())) {
            RegisteredCustomer rc = new RegisteredCustomer();
            rc.setUser(u);
            rc.setCity(req.getCity());
            rc.setPostalCode(req.getPostalCode());
            rc.setStreet(req.getStreet());
            customerRepo.save(rc);
        }
        return ApiResponse.ok("User created", u);
    }

    /** Update user status */
    @PatchMapping("/users/{id}/status")
    public ApiResponse<User> setStatus(@PathVariable Long id, @RequestBody StatusRequest req) {
        User u = userRepo.findById(id).orElseThrow();
        u.setStatus(req.getStatus());
        return ApiResponse.ok("Updated", userRepo.save(u));
    }

    @Data
    public static class CreateUserRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String password;
        private String role;
        private String street;
        private String city;
        private String postalCode;
    }

    @Data
    public static class StatusRequest {
        private String status;
    }
}