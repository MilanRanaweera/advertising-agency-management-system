package com.sliit.aams.project.controller;



import com.sliit.aams.common.exception.ApiResponse;
import com.sliit.aams.project.model.progress;
import com.sliit.aams.project.model.Project;
import com.sliit.aams.project.model.Prototype;
import com.sliit.aams.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService svc;

    @GetMapping
    public ApiResponse<List<Project>> all() { return ApiResponse.ok(svc.all()); }

    @GetMapping("/{id}")
    public ApiResponse<Project> one(@PathVariable Long id) { return ApiResponse.ok(svc.get(id)); }

    @GetMapping("/my")
    public ApiResponse<List<Project>> my(@RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.ok(svc.byCustomer(userId));
    }

    @PostMapping
    public ApiResponse<Project> create(@RequestBody Project p) {
        return ApiResponse.ok("Project created", svc.save(p));
    }

    @PutMapping("/{id}")
    public ApiResponse<Project> update(@PathVariable Long id, @RequestBody Project p) {
        return ApiResponse.ok("Updated", svc.update(id, p));
    }

    @GetMapping("/{id}/progress")
    public ApiResponse<List<progress>> progress(@PathVariable Long id) {
        return ApiResponse.ok(svc.progress(id));
    }

    @PostMapping("/{id}/progress")
    public ApiResponse<progress> addProgress(@PathVariable Long id,
                                             @RequestBody progress p,
                                             @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        p.setProjectId(id);
        p.setUpdatedByDesignerId(userId);
        return ApiResponse.ok("Progress added", svc.addProgress(p));
    }

    @GetMapping("/{id}/prototypes")
    public ApiResponse<List<Prototype>> prototypes(@PathVariable Long id) {
        return ApiResponse.ok(svc.prototypes(id));
    }

    @PostMapping("/{id}/prototypes")
    public ApiResponse<Prototype> addPrototype(@PathVariable Long id, @RequestBody Prototype p) {
        p.setProjectId(id);
        return ApiResponse.ok("Prototype added", svc.addPrototype(p));
    }

    @PostMapping("/prototypes/{protoId}/approve")
    public ApiResponse<Prototype> approve(@PathVariable Long protoId,
                                          @RequestHeader("X-User-Id") Long userId) {
        return ApiResponse.ok("Approved", svc.approvePrototype(protoId, userId));
    }
}