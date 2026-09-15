package com.sliit.aams.servicepackage.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "package_service")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PackageService {

    @EmbeddedId
    private PackageServiceId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("packageId")
    @JoinColumn(name = "package_id")
    private Package pkg;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("serviceId")
    @JoinColumn(name = "service_id")
    private serviceItems service;
}