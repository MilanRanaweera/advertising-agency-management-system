package com.sliit.aams.quotation.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quotation")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Quotation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "q_id") private Long qId;

    @Column(name = "q_date", nullable = false)              private LocalDateTime qDate = LocalDateTime.now();
    @Column(nullable = false, length = 30)                  private String status = "PENDING";
    @Column(name = "customer_id", nullable = false)         private Long customerId;
    @Column(name = "prepared_by_rep_id")                    private Long preparedByRepId;

    @OneToMany(mappedBy = "quotation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<QuotationItem> items = new ArrayList<>();
}