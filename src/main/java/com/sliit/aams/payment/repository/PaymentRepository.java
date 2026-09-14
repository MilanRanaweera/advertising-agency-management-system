package com.sliit.aams.payment.repository;

import com.sliit.aams.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // TODO: custom query methods
}
