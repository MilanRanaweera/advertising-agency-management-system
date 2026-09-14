package com.sliit.aams.quotation.service;

import com.sliit.aams.quotation.model.Quotation;
import com.sliit.aams.quotation.repository.QuotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Owner: Silva T.T.M. (IT25103316)
 * TODO: Implement business logic for the quotation module use cases.
 */
@Service
public class QuotationService {

    @Autowired
    private QuotationRepository quotationRepository;

    public List<Quotation> findAll() {
        return quotationRepository.findAll();
    }
}
