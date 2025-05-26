package com.digitalpassport.service;

import com.digitalpassport.entity.Project;
import com.digitalpassport.exception.persistence.AttemptToUpdateIdException;
import com.digitalpassport.exception.persistence.EntityNotFoundException;
import com.digitalpassport.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public Project saveProject(Project newProject) {
        return projectRepository.save(newProject);
    }

    public Project getProject(UUID uuid) {
        return projectRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);
    }

    public Project updateProject(UUID uuid, Project newProject) {
        if (newProject.getUuid() != null && !uuid.equals(newProject.getUuid())) {
            throw new AttemptToUpdateIdException();
        }

        return projectRepository.findById(uuid).map(project -> {
            newProject.setUuid(project.getUuid());
            return projectRepository.save(newProject);
        }).orElseThrow(EntityNotFoundException::new);
    }

    public void deleteProject(UUID uuid) {
        projectRepository.deleteById(uuid);
    }

    public List<Project> getProjectPage(int index, int size) {
        return projectRepository.findAll(PageRequest.of(index, size, Sort.by("uuid"))).getContent();
    }
}
