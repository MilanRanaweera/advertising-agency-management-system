package com.sliit.aams.servicepackage.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "package")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Package {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id") private Long packageId;

    @Column(name = "package_name", nullable = false, length = 150) private String packageName;
    @Column(nullable = false)                                      private BigDecimal price;
    @Column(length = 100)                                          private String duration;
    @Column(nullable = false, length = 30)                         private String status = "ACTIVE";
}