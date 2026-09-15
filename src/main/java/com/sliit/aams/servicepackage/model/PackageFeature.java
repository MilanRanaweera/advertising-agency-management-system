package com.sliit.aams.servicepackage.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "package_feature")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PackageFeature {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feature_id") private Long featureId;

    @Column(name = "package_id", nullable = false)                private Long packageId;
    @Column(name = "feature_name", nullable = false, length = 150) private String featureName;
    @Column(name = "feature_desc", length = 255)                   private String featureDesc;
}