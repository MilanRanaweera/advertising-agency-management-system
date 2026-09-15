package com.sliit.aams.payment.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank_transfer_payment")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class BankTransferPayment {

    @Id @Column(name = "payment_id")
    private Long paymentId;

    @OneToOne @MapsId
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "bank_name", length = 100) private String bankName;
    @Column(name = "slip_path", length = 255)  private String slipPath;
}