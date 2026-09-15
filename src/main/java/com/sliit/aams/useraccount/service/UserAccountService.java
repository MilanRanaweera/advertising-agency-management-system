package com.sliit.aams.useraccount.service;


import com.sliit.aams.common.exception.ResourceNotFoundException;
import com.sliit.aams.useraccount.dto.*;
import com.sliit.aams.useraccount.model.User;
import com.sliit.aams.useraccount.model.RegisteredCustomer;
import com.sliit.aams.useraccount.repository.UserRepository;
import com.sliit.aams.useraccount.repository.RegisteredCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserRepository userRepo;
    private final RegisteredCustomerRepository customerRepo;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already registered");

        User user = User.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role("CUSTOMER")
                .status("ACTIVE")
                .build();
        user = userRepo.saveAndFlush(user);
        RegisteredCustomer cust = new RegisteredCustomer();
        cust.setUser(user);
        cust.setStreet(req.getStreet());
        cust.setCity(req.getCity());
        cust.setPostalCode(req.getPostalCode());
        customerRepo.save(cust);

        return new AuthResponse(user.getUserId(), user.getEmail(), user.getRole(),
                user.getFirstName() + " " + user.getLastName());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash()))
            throw new IllegalArgumentException("Invalid email or password");
        return new AuthResponse(user.getUserId(), user.getEmail(), user.getRole(),
                user.getFirstName() + " " + user.getLastName());
    }

    public User getById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequest req) {
        User user = getById(userId);
        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getLastName()  != null) user.setLastName(req.getLastName());
        if (req.getPhone()     != null) user.setPhone(req.getPhone());
        userRepo.save(user);

        customerRepo.findById(userId).ifPresent(c -> {
            if (req.getStreet()     != null) c.setStreet(req.getStreet());
            if (req.getCity()       != null) c.setCity(req.getCity());
            if (req.getPostalCode() != null) c.setPostalCode(req.getPostalCode());
            customerRepo.save(c);
        });
        return user;
    }
}