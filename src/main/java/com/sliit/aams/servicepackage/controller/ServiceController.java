package com.sliit.aams.servicepackage.controller;

import com.sliit.aams.common.exception.ApiResponse;
import com.sliit.aams.servicepackage.model.serviceItems;
import com.sliit.aams.servicepackage.service.ServicePackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {
    private final ServicePackageService svc;

    @GetMapping
    public ApiResponse<List<serviceItems>> list() { return ApiResponse.ok(svc.allServices()); }

    @GetMapping("/{id}")
    public ApiResponse<serviceItems> one(@PathVariable Long id) { return ApiResponse.ok(svc.getService(id)); }

    @PostMapping
    public ApiResponse<serviceItems> create(@RequestBody serviceItems s) {
        return ApiResponse.ok("Created", svc.saveService(s));
    }

    @PutMapping("/{id}")
    public ApiResponse<serviceItems> update(@PathVariable Long id, @RequestBody serviceItems s) {
        return ApiResponse.ok("Updated", svc.updateService(id, s));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        svc.deleteService(id);
        return ApiResponse.ok("Deleted", null);
    }
}