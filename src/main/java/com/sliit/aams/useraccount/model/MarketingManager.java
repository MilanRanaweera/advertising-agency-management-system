package com.sliit.aams.useraccount.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "marketing_manager")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MarketingManager {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manager_id")
    private Long managerId;

    @Column(nullable = false, length = 150) private String name;

    @Column(unique = true, length = 150) private String email;
}