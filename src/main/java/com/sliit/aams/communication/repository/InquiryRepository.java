package com.sliit.aams.communication.repository;


import com.sliit.aams.communication.model.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByCustomerId(Long cid);
    List<Inquiry> findByOfficerId(Long oid);
}