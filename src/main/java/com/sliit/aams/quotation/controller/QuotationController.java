package com.sliit.aams.quotation.controller;

import com.sliit.aams.quotation.model.Quotation;
import com.sliit.aams.quotation.service.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Owner: Silva T.T.M. (IT25103316)
 * REST endpoints for the quotation module.
 */
@RestController
@RequestMapping("/api/quotation")
public class QuotationController {

    @Autowired
    private QuotationService quotationService;

    @GetMapping
    public List<Quotation> getAll() {
        return quotationService.findAll();
    }
}
