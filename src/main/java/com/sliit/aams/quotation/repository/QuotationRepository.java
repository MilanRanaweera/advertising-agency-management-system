package com.sliit.aams.quotation.repository;

import com.sliit.aams.quotation.model.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    // TODO: custom query methods
}
