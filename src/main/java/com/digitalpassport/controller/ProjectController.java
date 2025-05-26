package com.digitalpassport.controller;

import com.digitalpassport.entity.Project;
import com.digitalpassport.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Project saveProject(@RequestBody Project newProject) {
        return projectService.saveProject(newProject);
    }

    @GetMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Project getProject(@PathVariable UUID uuid) {
        return projectService.getProject(uuid);
    }

    @PutMapping(value = "/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Project updateProject(@PathVariable UUID uuid, @RequestBody Project newProject) {
        return projectService.updateProject(uuid, newProject);
    }

    @DeleteMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteProject(@PathVariable UUID uuid) {
        projectService.deleteProject(uuid);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/page/{index}/{size}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Project> getProjectPage(@PathVariable Integer index, @PathVariable Integer size) {
        return projectService.getProjectPage(index, size);
    }
}
