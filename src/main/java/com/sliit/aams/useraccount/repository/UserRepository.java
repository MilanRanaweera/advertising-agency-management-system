package com.sliit.aams.useraccount.repository;

import com.sliit.aams.useraccount.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // TODO: custom query methods
}
