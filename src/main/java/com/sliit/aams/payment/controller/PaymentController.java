package com.sliit.aams.payment.controller;


import com.sliit.aams.common.exception.ApiResponse;
import com.sliit.aams.payment.model.Invoice;
import com.sliit.aams.payment.model.Payment;
import com.sliit.aams.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService svc;

    @GetMapping("/invoices")
    public ApiResponse<List<Invoice>> invoices() { return ApiResponse.ok(svc.allInvoices()); }

    @GetMapping("/invoices/{id}")
    public ApiResponse<Invoice> invoice(@PathVariable Long id) { return ApiResponse.ok(svc.getInvoice(id)); }

    @PostMapping("/invoices")
    public ApiResponse<Invoice> createInvoice(@RequestBody Invoice inv) {
        return ApiResponse.ok("Invoice created", svc.createInvoice(inv));
    }

    @PostMapping
    public ApiResponse<Payment> pay(@RequestBody Payment p) {
        return ApiResponse.ok("Payment recorded", svc.pay(p));
    }

    @GetMapping("/invoice/{id}")
    public ApiResponse<List<Payment>> byInvoice(@PathVariable Long id) {
        return ApiResponse.ok(svc.paymentsForInvoice(id));
    }
}