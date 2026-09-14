package com.sliit.aams.payment.controller;

import com.sliit.aams.payment.model.Payment;
import com.sliit.aams.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Owner: Ihjas I.M. (IT25101525)
 * REST endpoints for the payment module.
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public List<Payment> getAll() {
        return paymentService.findAll();
    }
}
