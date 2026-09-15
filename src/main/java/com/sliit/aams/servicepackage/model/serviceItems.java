package com.sliit.aams.servicepackage.model;



import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "service")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class serviceItems {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id") private Long serviceId;

    @Column(name = "service_name", nullable = false, length = 150) private String serviceName;
    @Column(length = 100)                                          private String category;
    @Column(name = "base_price", nullable = false)                 private BigDecimal basePrice;
    @Column(name = "managed_by_manager_id")                        private Long managedByManagerId;
}