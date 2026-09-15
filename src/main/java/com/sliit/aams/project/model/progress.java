package com.sliit.aams.project.model;



import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "progress")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class progress {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id") private Long progressId;

    @Column(name = "project_id", nullable = false) private Long projectId;
    private String comment;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "date_time", nullable = false, updatable = false)
    private LocalDateTime dateTime;

    @Column(name = "updated_by_designer_id")
    private Long updatedByDesignerId;
}
