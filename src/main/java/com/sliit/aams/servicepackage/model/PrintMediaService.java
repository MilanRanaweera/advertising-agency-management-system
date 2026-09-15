package com.sliit.aams.servicepackage.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "print_media_service")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class PrintMediaService {

    @Id @Column(name = "service_id")
    private Long serviceId;

    @OneToOne @MapsId
    @JoinColumn(name = "service_id")
    private serviceItems service;

    @Column(name = "print_type", length = 100) private String printType;
    @Column(length = 100)                       private String dimension;
}