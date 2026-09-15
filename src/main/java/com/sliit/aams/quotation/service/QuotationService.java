package com.sliit.aams.quotation.service;



import com.sliit.aams.common.exception.ResourceNotFoundException;
import com.sliit.aams.quotation.dto.CreateQuotationRequest;
import com.sliit.aams.quotation.model.Quotation;
import com.sliit.aams.quotation.model.QuotationItem;
import com.sliit.aams.quotation.repository.QuotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuotationService {

    private final QuotationRepository repo;

    public List<Quotation> all() { return repo.findAll(); }

    public List<Quotation> byCustomer(Long customerId) { return repo.findByCustomerId(customerId); }

    public Quotation get(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found"));
    }

    @Transactional
    public Quotation create(CreateQuotationRequest req, Long repId) {
        Quotation q = Quotation.builder()
                .customerId(req.getCustomerId())
                .preparedByRepId(repId)
                .status("PENDING")
                .build();

        for (CreateQuotationRequest.Item it : req.getItems()) {
            QuotationItem qi = QuotationItem.builder()
                    .quotation(q)
                    .serviceId(it.getServiceId())
                    .quantity(it.getQuantity() == null ? 1 : it.getQuantity())
                    .unitPrice(it.getUnitPrice())
                    .discount(it.getDiscount() == null ? BigDecimal.ZERO : it.getDiscount())
                    .build();
            q.getItems().add(qi);
        }
        return repo.save(q);
    }

    public Quotation updateStatus(Long id, String status) {
        Quotation q = get(id);
        q.setStatus(status);
        return repo.save(q);
    }
}