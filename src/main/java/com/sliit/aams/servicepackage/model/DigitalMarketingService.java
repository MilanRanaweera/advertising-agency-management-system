package com.sliit.aams.servicepackage.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "digital_marketing_service")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DigitalMarketingService {

    @Id @Column(name = "service_id")
    private Long serviceId;

    @OneToOne @MapsId
    @JoinColumn(name = "service_id")
    private serviceItems service;

    @Column(name = "platform_type", length = 100)
    private String platformType;

    @Column(name = "campaign_budget", precision = 12, scale = 2)
    private BigDecimal campaignBudget;
}