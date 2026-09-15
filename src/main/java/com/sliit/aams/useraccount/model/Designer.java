package com.sliit.aams.useraccount.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "designer")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Designer {

    @Id @Column(name = "designer_id")
    private Long designerId;

    @OneToOne @MapsId
    @JoinColumn(name = "designer_id")
    private Employee employee;

    @Column(length = 150) private String name;

    @Column(name = "assigned_by_manager_id")
    private Long assignedByManagerId;
}