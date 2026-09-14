package com.sliit.aams.project.controller;

import com.sliit.aams.project.model.Project;
import com.sliit.aams.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Owner: Ranaweera D.A.I.M. (IT25100994)
 * REST endpoints for the project module.
 */
@RestController
@RequestMapping("/api/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public List<Project> getAll() {
        return projectService.findAll();
    }
}
