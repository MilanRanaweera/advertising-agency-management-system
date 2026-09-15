package com.sliit.aams.payment.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "online_card_payment")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class OnlineCardPayment {

    @Id @Column(name = "payment_id")
    private Long paymentId;

    @OneToOne @MapsId
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "card_type", length = 50)      private String cardType;
    @Column(name = "gateway_tx_id", length = 150)  private String gatewayTxId;
}