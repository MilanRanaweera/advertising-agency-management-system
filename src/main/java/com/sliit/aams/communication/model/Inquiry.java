package com.sliit.aams.communication.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "inquiry")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Inquiry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id") private Long inquiryId;

    @Column(name = "customer_id", nullable = false)  private Long customerId;
    @Column(name = "officer_id")                     private Long officerId;
    @Column(nullable = false, length = 200)          private String subject;
    @Builder.Default
    @Column(nullable = false, length = 30)           private String status = "OPEN";
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)   private LocalDateTime createdAt;
}