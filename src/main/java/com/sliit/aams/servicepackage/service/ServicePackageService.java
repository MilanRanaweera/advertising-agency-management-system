package com.sliit.aams.servicepackage.service;

import com.sliit.aams.servicepackage.model.ServicePackage;
import com.sliit.aams.servicepackage.repository.ServicePackageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Owner: Deshapriya P.A.D.A. (IT25102331)
 * TODO: Implement business logic for the servicepackage module use cases.
 */
@Service
public class ServicePackageService {

    @Autowired
    private ServicePackageRepository servicepackageRepository;

    public List<ServicePackage> findAll() {
        return servicepackageRepository.findAll();
    }
}
