package com.sliit.aams.communication.repository;


import com.sliit.aams.communication.model.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    List<Response> findByInquiryId(Long inquiryId);
}