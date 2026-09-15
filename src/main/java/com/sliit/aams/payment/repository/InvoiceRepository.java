package com.sliit.aams.payment.repository;


import com.sliit.aams.payment.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByQId(Long qId);
}