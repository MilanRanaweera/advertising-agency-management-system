package com.sliit.aams.communication.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "response")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Response {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id") private Long responseId;

    @Column(name = "inquiry_id", nullable = false)            private Long inquiryId;
    @Column(name = "officer_id")                              private Long officerId;
    @Column(name = "response_text", nullable = false, length = 1000) private String responseText;
    @CreationTimestamp
    @Column(name = "response_date", nullable = false)         private LocalDateTime responseDate;
}