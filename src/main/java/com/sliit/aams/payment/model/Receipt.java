package com.sliit.aams.payment.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipt")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Receipt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_no") private Long receiptNo;

    @Column(name = "payment_id", nullable = false, unique = true) private Long paymentId;
    @Column(name = "issued_date", nullable = false)               private LocalDateTime issuedDate = LocalDateTime.now();
}