package com.sliit.aams.payment.model;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id") private Long paymentId;

    @Column(name = "invoice_id", nullable = false)              private Long invoiceId;
    @Column(name = "payment_date", nullable = false)            private LocalDateTime paymentDate = LocalDateTime.now();
    @Column(name = "payment_method", nullable = false, length = 30) private String paymentMethod;
    @Column(name = "amount_paid", nullable = false)             private BigDecimal amountPaid;
    @Column(name = "transaction_ref", length = 150)             private String transactionRef;
}