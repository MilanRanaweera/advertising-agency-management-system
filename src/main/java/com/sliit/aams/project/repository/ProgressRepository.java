package com.sliit.aams.project.repository;



import com.sliit.aams.project.model.progress;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProgressRepository extends JpaRepository<progress, Long> {
    List<progress> findByProjectIdOrderByDateTimeDesc(Long projectId);
}
