package com.sliit.aams.useraccount.repository;


import com.sliit.aams.useraccount.model.RegisteredCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisteredCustomerRepository extends JpaRepository<RegisteredCustomer, Long> {}