package com.sliit.aams.useraccount.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registered_customer")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisteredCustomer {
    @Id @Column(name = "customer_id") private Long customerId;

    @OneToOne @MapsId @JoinColumn(name = "customer_id")
    private User user;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate = LocalDateTime.now();

    private String street;
    private String city;
    @Column(name = "postal_code", length = 20) private String postalCode;
    @Column(name = "managed_by_admin_id")      private Integer managedByAdminId;
}