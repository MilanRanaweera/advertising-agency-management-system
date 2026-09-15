package com.sliit.aams.communication.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id") private Long notificationId;

    @Column(name = "consultation_id")       private Long consultationId;
    @Column(name = "recipient_customer_id") private Long recipientCustomerId;
    @Column(name = "recipient_officer_id")  private Long recipientOfficerId;
    @Column(nullable = false, length = 50)  private String type;
    @Column(nullable = false, length = 255) private String message;
    @Column(name = "read_status", nullable = false) private Boolean readStatus = false;
    @Column(name = "created_at", nullable = false)  private LocalDateTime createdAt = LocalDateTime.now();
}