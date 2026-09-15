package com.sliit.aams.quotation.repository;


import com.sliit.aams.quotation.model.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    List<Quotation> findByCustomerId(Long customerId);
    List<Quotation> findByStatus(String status);
}