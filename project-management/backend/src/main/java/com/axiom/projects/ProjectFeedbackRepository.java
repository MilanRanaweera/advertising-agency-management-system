package com.axiom.projects;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectFeedbackRepository
  extends JpaRepository<ProjectFeedback, Long> {
  List<ProjectFeedback> findByProjectId(Long id);
}
