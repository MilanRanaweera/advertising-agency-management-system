package com.sliit.aams.project.repository;

import com.sliit.aams.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByCustomerId(Long customerId);
    List<Project> findByDesignerId(Long designerId);
}