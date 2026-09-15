package com.sliit.aams.project.repository;


import com.sliit.aams.project.model.Prototype;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PrototypeRepository extends JpaRepository<Prototype, Long> {
    List<Prototype> findByProjectId(Long projectId);
}