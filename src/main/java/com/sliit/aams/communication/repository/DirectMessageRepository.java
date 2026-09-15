package com.sliit.aams.communication.repository;


import com.sliit.aams.communication.model.DirectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
    List<DirectMessage> findByCustomerIdAndOfficerId(Long cid, Long oid);
}