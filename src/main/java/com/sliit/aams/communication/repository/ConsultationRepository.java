package com.sliit.aams.communication.repository;


import com.sliit.aams.communication.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByCustomerId(Long cid);
}
