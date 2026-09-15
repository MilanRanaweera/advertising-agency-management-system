package com.sliit.aams.project.service;



import com.sliit.aams.common.exception.ResourceNotFoundException;
import com.sliit.aams.project.model.progress;
import com.sliit.aams.project.model.Project;
import com.sliit.aams.project.model.Prototype;
import com.sliit.aams.project.repository.ProgressRepository;
import com.sliit.aams.project.repository.ProjectRepository;
import com.sliit.aams.project.repository.PrototypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepo;
    private final ProgressRepository progressRepo;
    private final PrototypeRepository prototypeRepo;

    public List<Project> all() { return projectRepo.findAll(); }

    public List<Project> byCustomer(Long cid) { return projectRepo.findByCustomerId(cid); }

    public Project get(Long id) {
        return projectRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    public Project save(Project p) { return projectRepo.save(p); }

    public Project update(Long id, Project in) {
        Project p = get(id);
        if (in.getStartDate() != null) p.setStartDate(in.getStartDate());
        if (in.getEndDate() != null) p.setEndDate(in.getEndDate());
        if (in.getDesignerId() != null) p.setDesignerId(in.getDesignerId());
        if (in.getCustomerId() != null) p.setCustomerId(in.getCustomerId());
        if (in.getHandoverDate() != null) p.setHandoverDate(in.getHandoverDate());
        if (in.getStatus() != null) p.setStatus(in.getStatus());
        return projectRepo.save(p);
    }

    public List<progress> progress(Long projectId) {
        return progressRepo.findByProjectIdOrderByDateTimeDesc(projectId);
    }

    public progress addProgress(progress p) { return progressRepo.save(p); }

    public List<Prototype> prototypes(Long projectId) { return prototypeRepo.findByProjectId(projectId); }

    public Prototype addPrototype(Prototype p) { return prototypeRepo.save(p); }

    public Prototype approvePrototype(Long id, Long customerId) {
        Prototype p = prototypeRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prototype not found"));
        p.setApprovedByCustomerId(customerId);
        p.setApprovedAt(LocalDateTime.now());
        return prototypeRepo.save(p);
    }
}