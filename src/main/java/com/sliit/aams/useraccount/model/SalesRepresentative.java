package com.sliit.aams.useraccount.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sales_representative")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SalesRepresentative {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rep_id")
    private Long repId;

    @Column(nullable = false, length = 150) private String name;

    @Column(unique = true, length = 150) private String email;
}