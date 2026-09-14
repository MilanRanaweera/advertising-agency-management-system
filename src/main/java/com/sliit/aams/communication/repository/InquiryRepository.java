package com.sliit.aams.communication.repository;

import com.sliit.aams.communication.model.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    // TODO: custom query methods
}
