package com.sliit.aams.useraccount.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_id")
    private Long empId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(unique = true, length = 150)
    private String email;
}