package com.sliit.aams.quotation.controller;

import com.sliit.aams.common.exception.ApiResponse;
import com.sliit.aams.quotation.dto.CreateQuotationRequest;
import com.sliit.aams.quotation.model.Quotation;
import com.sliit.aams.quotation.service.QuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
public class QuotationController {
    private final QuotationService svc;

    @GetMapping
    public ApiResponse<List<Quotation>> all() { return ApiResponse.ok(svc.all()); }

    @GetMapping("/{id}")
    public ApiResponse<Quotation> one(@PathVariable Long id) { return ApiResponse.ok(svc.get(id)); }

    @GetMapping("/my")
    public ApiResponse<List<Quotation>> my(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.ok(svc.byCustomer(userId));
    }

    @PostMapping
    public ApiResponse<Quotation> create(@RequestBody CreateQuotationRequest req,
                                         @RequestHeader(value = "X-User-Id", required = false) Long repId) {
        return ApiResponse.ok("Quotation created", svc.create(req, repId));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Quotation> status(@PathVariable Long id, @RequestBody Map<String,String> body) {
        return ApiResponse.ok("Status updated", svc.updateStatus(id, body.get("status")));
    }
}