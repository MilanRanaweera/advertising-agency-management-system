package com.sliit.aams.project.repository;

import com.sliit.aams.project.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // TODO: custom query methods
}
