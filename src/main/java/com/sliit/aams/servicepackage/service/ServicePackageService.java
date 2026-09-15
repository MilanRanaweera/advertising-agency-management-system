package com.sliit.aams.servicepackage.service;

import com.sliit.aams.common.exception.ResourceNotFoundException;
import com.sliit.aams.servicepackage.model.Package;
import com.sliit.aams.servicepackage.model.PackageFeature;
import com.sliit.aams.servicepackage.model.serviceItems;
import com.sliit.aams.servicepackage.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicePackageService {

    private final ServiceRepository serviceRepo;
    private final PackageRepository packageRepo;
    private final PackageFeatureRepository featureRepo;

    public List<serviceItems> allServices() { return serviceRepo.findAll(); }

    public serviceItems getService(Long id) {
        return serviceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
    }

    public serviceItems saveService(serviceItems s) { return serviceRepo.save(s); }

    public serviceItems updateService(Long id, serviceItems in) {
        serviceItems s = getService(id);
        s.setServiceName(in.getServiceName());
        s.setCategory(in.getCategory());
        s.setBasePrice(in.getBasePrice());
        return serviceRepo.save(s);
    }

    public void deleteService(Long id) { serviceRepo.deleteById(id); }

    public List<Package> allPackages() { return packageRepo.findAll(); }

    public Package getPackage(Long id) {
        return packageRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found"));
    }

    public Package savePackage(Package p) { return packageRepo.save(p); }

    public Package updatePackage(Long id, Package in) {
        Package p = getPackage(id);
        p.setPackageName(in.getPackageName());
        p.setPrice(in.getPrice());
        p.setDuration(in.getDuration());
        if (in.getStatus() != null) p.setStatus(in.getStatus());
        return packageRepo.save(p);
    }

    public void deletePackage(Long id) { packageRepo.deleteById(id); }

    public List<PackageFeature> features(Long packageId) { return featureRepo.findByPackageId(packageId); }

    public PackageFeature addFeature(Long packageId, PackageFeature f) {
        f.setPackageId(packageId);
        return featureRepo.save(f);
    }
}