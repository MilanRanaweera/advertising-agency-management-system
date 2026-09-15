package com.sliit.aams.servicepackage.controller;

import com.sliit.aams.common.exception.ApiResponse;
import com.sliit.aams.servicepackage.model.Package;
import com.sliit.aams.servicepackage.model.PackageFeature;
import com.sliit.aams.servicepackage.service.ServicePackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class PackageController {
    private final ServicePackageService svc;

    @GetMapping
    public ApiResponse<List<Package>> list() { return ApiResponse.ok(svc.allPackages()); }

    @GetMapping("/{id}")
    public ApiResponse<Package> one(@PathVariable Long id) { return ApiResponse.ok(svc.getPackage(id)); }

    @PostMapping
    public ApiResponse<Package> create(@RequestBody Package p) {
        return ApiResponse.ok("Created", svc.savePackage(p));
    }

    @PutMapping("/{id}")
    public ApiResponse<Package> update(@PathVariable Long id, @RequestBody Package p) {
        return ApiResponse.ok("Updated", svc.updatePackage(id, p));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        svc.deletePackage(id);
        return ApiResponse.ok("Deleted", null);
    }

    @GetMapping("/{id}/features")
    public ApiResponse<List<PackageFeature>> features(@PathVariable Long id) {
        return ApiResponse.ok(svc.features(id));
    }

    @PostMapping("/{id}/features")
    public ApiResponse<PackageFeature> addFeature(@PathVariable Long id, @RequestBody PackageFeature f) {
        return ApiResponse.ok("Feature added", svc.addFeature(id, f));
    }
}