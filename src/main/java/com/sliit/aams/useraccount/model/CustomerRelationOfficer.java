package com.sliit.aams.useraccount.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customer_relation_officer")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomerRelationOfficer {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "officer_id")
    private Long officerId;

    @Column(nullable = false, length = 150) private String name;

    @Column(unique = true, length = 150) private String email;

    @Column(name = "telephone_no", length = 30) private String telephoneNo;
}