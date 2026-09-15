package com.sliit.aams.project.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prototype")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Prototype {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prototype_id") private Long prototypeId;

    @Column(name = "project_id", nullable = false) private Long projectId;
    private String comment;

    @Column(name = "approved_by_customer_id") private Long approvedByCustomerId;
    @Column(name = "approved_at")              private LocalDateTime approvedAt;
    
    // Added for file upload support
    @Column(columnDefinition = "TEXT")
    private String fileUrl;
    private String version = "v1.0";
    private String status = "PENDING";
    
    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "upload_date", updatable = false)
    private LocalDateTime uploadDate;
}