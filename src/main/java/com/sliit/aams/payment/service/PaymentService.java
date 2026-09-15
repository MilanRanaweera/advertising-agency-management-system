package com.sliit.aams.payment.service;

import com.sliit.aams.common.exception.ResourceNotFoundException;
import com.sliit.aams.payment.model.Invoice;
import com.sliit.aams.payment.model.Payment;
import com.sliit.aams.payment.model.Receipt;
import com.sliit.aams.payment.repository.InvoiceRepository;
import com.sliit.aams.payment.repository.PaymentRepository;
import com.sliit.aams.payment.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final InvoiceRepository invoiceRepo;
    private final PaymentRepository paymentRepo;
    private final ReceiptRepository receiptRepo;

    public List<Invoice> allInvoices() { return invoiceRepo.findAll(); }

    public Invoice getInvoice(Long id) {
        return invoiceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
    }

    public Invoice createInvoice(Invoice inv) { return invoiceRepo.save(inv); }

    @Transactional
    public Payment pay(Payment p) {
        Invoice inv = getInvoice(p.getInvoiceId());
        Payment saved = paymentRepo.save(p);
        inv.setPaymentStatus("PAID");
        invoiceRepo.save(inv);
        receiptRepo.save(Receipt.builder().paymentId(saved.getPaymentId()).build());
        return saved;
    }

    public List<Payment> paymentsForInvoice(Long invoiceId) {
        return paymentRepo.findByInvoiceId(invoiceId);
    }
}