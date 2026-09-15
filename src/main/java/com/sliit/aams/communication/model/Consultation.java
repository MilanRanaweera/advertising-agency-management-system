package com.sliit.aams.communication.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "consultation")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Consultation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consultation_id") private Long consultationId;

    @Column(name = "customer_id", nullable = false)  private Long customerId;
    @Column(name = "officer_id")                     private Long officerId;
    @Column(name = "date_time", nullable = false)    private LocalDateTime dateTime;
    private String agenda;
    @Column(nullable = false, length = 30)           private String status = "SCHEDULED";
}