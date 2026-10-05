package com.axiom.projects;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
  List<Assignment> findByProjectId(Long id);
  List<Assignment> findByDesignerId(Long id);
  void deleteByProjectId(Long id);
}
