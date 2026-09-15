package com.sliit.aams.useraccount.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "first_name", nullable = false, length = 100) private String firstName;
    @Column(name = "last_name",  nullable = false, length = 100) private String lastName;
    @Column(nullable = false, unique = true, length = 150)       private String email;
    @Column(length = 30)                                         private String phone;

    @Builder.Default
    @Column(nullable = false, length = 30)                       private String status = "ACTIVE";

    @Column(name = "birth_of_date")                              private LocalDate birthOfDate;
    @Column(name = "password_hash", nullable = false)            private String passwordHash;

    @Builder.Default
    @Column(name = "created_at", nullable = false)               private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(nullable = false, length = 50)                       private String role = "CUSTOMER";
}