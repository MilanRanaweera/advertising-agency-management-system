package com.sliit.aams.communication.service;



import com.sliit.aams.common.exception.ResourceNotFoundException;
import com.sliit.aams.communication.model.*;
import com.sliit.aams.communication.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunicationService {

    private final InquiryRepository inquiryRepo;
    private final ResponseRepository responseRepo;
    private final ConsultationRepository consultationRepo;
    private final DirectMessageRepository messageRepo;
    private final NotificationRepository notificationRepo;

    public List<Inquiry> allInquiries() { return inquiryRepo.findAll(); }

    public List<Inquiry> inquiriesByCustomer(Long cid) { return inquiryRepo.findByCustomerId(cid); }

    public Inquiry getInquiry(Long id) {
        return inquiryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found"));
    }

    public Inquiry createInquiry(Inquiry i) {
        if (i.getStatus() == null || i.getStatus().isBlank()) i.setStatus("OPEN");
        return inquiryRepo.save(i);
    }

    public Inquiry updateInquiryStatus(Long id, String status) {
        Inquiry i = getInquiry(id);
        i.setStatus(status);
        return inquiryRepo.save(i);
    }

    public List<Response> responses(Long inquiryId) { return responseRepo.findByInquiryId(inquiryId); }

    public Response addResponse(Response r) {
        if (r.getResponseDate() == null) r.setResponseDate(java.time.LocalDateTime.now());
        return responseRepo.save(r);
    }

    public List<Consultation> consultationsForCustomer(Long cid) { return consultationRepo.findByCustomerId(cid); }

    public Consultation bookConsultation(Consultation c) { return consultationRepo.save(c); }

    public DirectMessage sendMessage(DirectMessage m) { return messageRepo.save(m); }

    public List<DirectMessage> thread(Long cid, Long oid) {
        return messageRepo.findByCustomerIdAndOfficerId(cid, oid);
    }

    public List<Notification> notifications(Long customerId, Long officerId) {
        return notificationRepo.findByRecipientCustomerIdOrRecipientOfficerId(customerId, officerId);
    }

    public Notification pushNotification(Notification n) { return notificationRepo.save(n); }
}