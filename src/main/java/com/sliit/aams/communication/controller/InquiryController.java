package com.sliit.aams.communication.controller;

import com.sliit.aams.communication.model.Inquiry;
import com.sliit.aams.communication.service.InquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Owner: Igalawithana N.C. (IT25100243)
 * REST endpoints for the communication module.
 */
@RestController
@RequestMapping("/api/communication")
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    @GetMapping
    public List<Inquiry> getAll() {
        return inquiryService.findAll();
    }
}
