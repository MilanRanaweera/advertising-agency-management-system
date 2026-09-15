package com.sliit.aams.project.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "project")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id") private Long projectId;

    @Column(name = "start_date") private LocalDate startDate;
    @Column(name = "end_date")   private LocalDate endDate;

    @Column(name = "created_by_manager_id") private Long createdByManagerId;
    @Column(name = "designer_id")           private Long designerId;
    @Column(name = "customer_id")           private Long customerId;
    @Column(name = "handover_date")         private LocalDate handoverDate;
    @Column(length = 30)                    private String status = "IN_PROGRESS";
}