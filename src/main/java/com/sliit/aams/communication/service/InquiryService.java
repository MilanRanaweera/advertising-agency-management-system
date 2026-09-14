package com.sliit.aams.communication.service;

import com.sliit.aams.communication.model.Inquiry;
import com.sliit.aams.communication.repository.InquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Owner: Igalawithana N.C. (IT25100243)
 * TODO: Implement business logic for the communication module use cases.
 */
@Service
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    public List<Inquiry> findAll() {
        return inquiryRepository.findAll();
    }
}
