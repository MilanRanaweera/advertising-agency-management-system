package com.sliit.aams.quotation.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "quotation_item")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class QuotationItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quote_item_id") private Long quoteItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "q_id", nullable = false)
    @JsonIgnore
    private Quotation quotation;

    @Column(name = "service_id", nullable = false)   private Long serviceId;
    @Column(nullable = false)                        private Integer quantity = 1;
    @Column(name = "unit_price", nullable = false)   private BigDecimal unitPrice;
    private BigDecimal discount = BigDecimal.ZERO;
}