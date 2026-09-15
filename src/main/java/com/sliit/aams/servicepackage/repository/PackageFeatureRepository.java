package com.sliit.aams.servicepackage.repository;

import com.sliit.aams.servicepackage.model.PackageFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PackageFeatureRepository extends JpaRepository<PackageFeature, Long> {
    List<PackageFeature> findByPackageId(Long packageId);
}