package com.sliit.aams.servicepackage.controller;

import com.sliit.aams.servicepackage.model.ServicePackage;
import com.sliit.aams.servicepackage.service.ServicePackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Owner: Deshapriya P.A.D.A. (IT25102331)
 * REST endpoints for the servicepackage module.
 */
@RestController
@RequestMapping("/api/servicepackage")
public class ServicePackageController {

    @Autowired
    private ServicePackageService servicepackageService;

    @GetMapping
    public List<ServicePackage> getAll() {
        return servicepackageService.findAll();
    }
}
