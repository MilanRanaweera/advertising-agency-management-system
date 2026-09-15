package com.sliit.aams.useraccount.repository;



import com.sliit.aams.useraccount.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    java.util.List<User> findByRoleNot(String role);
    java.util.List<User> findByRole(String role);
}