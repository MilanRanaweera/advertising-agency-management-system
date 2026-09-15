package com.sliit.aams.servicepackage.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Data @NoArgsConstructor @AllArgsConstructor
public class PackageServiceId implements Serializable {

    @Column(name = "package_id", nullable = false)
    private Long packageId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;
}