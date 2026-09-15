package com.sliit.aams.communication.repository;


import com.sliit.aams.communication.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientCustomerIdOrRecipientOfficerId(Long cid, Long oid);
}