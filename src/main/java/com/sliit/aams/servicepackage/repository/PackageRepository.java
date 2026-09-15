package com.sliit.aams.servicepackage.repository;


import com.sliit.aams.servicepackage.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<Package, Long> {}