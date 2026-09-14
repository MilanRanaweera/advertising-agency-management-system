package com.sliit.aams.payment.service;

import com.sliit.aams.payment.model.Payment;
import com.sliit.aams.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Owner: Ihjas I.M. (IT25101525)
 * TODO: Implement business logic for the payment module use cases.
 */
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }
}
