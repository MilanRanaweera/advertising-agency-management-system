package com.sliit.aams.communication.controller;

import com.sliit.aams.common.exception.ApiResponse;
import com.sliit.aams.communication.model.*;
import com.sliit.aams.communication.service.CommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communication")
@RequiredArgsConstructor
public class CommunicationController {

    private final CommunicationService svc;

    // ----- Inquiries -----
    @GetMapping("/inquiries")
    public ApiResponse<List<Inquiry>> allInquiries() { return ApiResponse.ok(svc.allInquiries()); }

    @GetMapping("/inquiries/my")
    public ApiResponse<List<Inquiry>> myInquiries(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.ok(svc.inquiriesByCustomer(userId));
    }

    @GetMapping("/inquiries/{id}")
    public ApiResponse<Inquiry> inquiry(@PathVariable Long id) { return ApiResponse.ok(svc.getInquiry(id)); }

    @PostMapping("/inquiries")
    public ApiResponse<Inquiry> createInquiry(@RequestBody Inquiry i,
                                              @RequestHeader("X-User-Id") Long userId) {
        i.setCustomerId(userId);
        return ApiResponse.ok("Inquiry submitted", svc.createInquiry(i));
    }

    @PatchMapping("/inquiries/{id}/status")
    public ApiResponse<Inquiry> updateInquiryStatus(@PathVariable Long id,
                                                    @RequestBody Map<String,String> b) {
        return ApiResponse.ok("Updated", svc.updateInquiryStatus(id, b.get("status")));
    }

    // ----- Responses -----
    @GetMapping("/inquiries/{id}/responses")
    public ApiResponse<List<Response>> responses(@PathVariable Long id) {
        return ApiResponse.ok(svc.responses(id));
    }

    @PostMapping("/inquiries/{id}/responses")
    public ApiResponse<Response> addResponse(@PathVariable Long id,
                                             @RequestBody Response r,
                                             @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        r.setInquiryId(id);
        r.setOfficerId(userId);
        return ApiResponse.ok("Response added", svc.addResponse(r));
    }

    // ----- Consultations -----
    @GetMapping("/consultations/my")
    public ApiResponse<List<Consultation>> myConsultations(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.ok(svc.consultationsForCustomer(userId));
    }

    @PostMapping("/consultations")
    public ApiResponse<Consultation> book(@RequestBody Consultation c,
                                          @RequestHeader("X-User-Id") Long userId) {
        c.setCustomerId(userId);
        return ApiResponse.ok("Consultation booked", svc.bookConsultation(c));
    }

    // ----- Direct messages -----
    @PostMapping("/messages")
    public ApiResponse<DirectMessage> sendMessage(@RequestBody DirectMessage m) {
        return ApiResponse.ok("Message sent", svc.sendMessage(m));
    }

    @GetMapping("/messages")
    public ApiResponse<List<DirectMessage>> thread(@RequestParam Long customerId,
                                                   @RequestParam Long officerId) {
        return ApiResponse.ok(svc.thread(customerId, officerId));
    }

    // ----- Notifications -----
    @GetMapping("/notifications")
    public ApiResponse<List<Notification>> notifications(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.ok(svc.notifications(userId, userId));
    }

    @PostMapping("/notifications")
    public ApiResponse<Notification> push(@RequestBody Notification n) {
        return ApiResponse.ok("Notification sent", svc.pushNotification(n));
    }
}