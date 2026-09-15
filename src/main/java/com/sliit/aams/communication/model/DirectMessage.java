package com.sliit.aams.communication.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "direct_message")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DirectMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id") private Long messageId;

    @Column(name = "customer_id")   private Long customerId;
    @Column(name = "officer_id")    private Long officerId;
    @Column(name = "message_text", nullable = false, length = 1000) private String messageText;
    @Column(name = "date_time", nullable = false) private LocalDateTime dateTime = LocalDateTime.now();
}