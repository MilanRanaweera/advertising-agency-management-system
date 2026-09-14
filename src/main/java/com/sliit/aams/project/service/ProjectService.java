package com.sliit.aams.project.service;

import com.sliit.aams.project.model.Project;
import com.sliit.aams.project.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Owner: Ranaweera D.A.I.M. (IT25100994)
 * TODO: Implement business logic for the project module use cases.
 */
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    public List<Project> findAll() {
        return projectRepository.findAll();
    }
}
