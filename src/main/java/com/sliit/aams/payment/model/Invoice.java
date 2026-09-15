package com.sliit.aams.payment.model;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoice")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Invoice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id") private Long invoiceId;

    @Column(name = "q_id", nullable = false)             private Long qId;
    @Column(name = "issue_date", nullable = false)       private LocalDate issueDate = LocalDate.now();
    @Column(name = "due_date")                           private LocalDate dueDate;
    @Column(name = "total_amount", nullable = false)     private BigDecimal totalAmount;
    @Column(name = "payment_status", nullable = false, length = 30) private String paymentStatus = "UNPAID";
}