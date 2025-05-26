package com.digitalpassport.service;

import com.digitalpassport.BaseIntegrationTest;
import com.digitalpassport.entity.*;
import com.digitalpassport.exception.persistence.AttemptToUpdateIdException;
import com.digitalpassport.exception.persistence.EntityNotFoundException;
import com.digitalpassport.repository.DocumentRepository;
import com.digitalpassport.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProjectServiceTest extends BaseIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private ProjectService projectService;

    @Test
    void testGetProjects() {
        init();
        List<Project> projects = projectService.getAllProjects();
        Project project = projects.getFirst();
        Set<Document> documents = project.getDocuments();
        assertEquals(1, projects.size());
        assertEquals(1, documents.size());
    }

    @Test
    void testSaveProject() {
        Project expected = getProject();
        projectService.saveProject(expected);
        Project actual = projectRepository.findById(expected.getUuid()).orElseThrow();
        assertProjects(expected, actual);
    }

    @Test
    void testGetProject() {
        assertThrows(EntityNotFoundException.class, () -> projectService.getProject(UUID.randomUUID()));
        Project expected = init();
        Project actual = projectService.getProject(expected.getUuid());
        assertProjects(expected, actual);
    }

    @Test
    void testUpdateProject() {
        Project expected = init();
        assertThrows(AttemptToUpdateIdException.class, () -> projectService.updateProject(UUID.randomUUID(), expected));
        expected.setStatus(ProjectStatus.ENGINEERING);
        Project actual = projectService.updateProject(expected.getUuid(), expected);
        assertProjects(expected, actual);
    }

    @Test
    void testDeleteProject() {
        Project expected = init();
        documentRepository.deleteAll();
        long before = projectRepository.findById(expected.getUuid()).stream().count();
        projectService.deleteProject(expected.getUuid());
        long after = projectRepository.findById(expected.getUuid()).stream().count();
        assertTrue(before > after);
    }

    @Test
    void testGetProjectPage() {
        init();
        List<Project> firstPage = projectService.getProjectPage(0, 1);
        assertEquals(1, firstPage.size());
        List<Project> secondPage = projectService.getProjectPage(1, 1);
        assertEquals(0, secondPage.size());
    }

    private Project init() {
        Project project = getProject();
        projectRepository.save(project);
        Document document = getDocument(project);
        documentRepository.save(document);
        return projectRepository.findById(project.getUuid()).orElseThrow();
    }

    private Project getProject() {
        Project project = new Project();
        project.setName("Name");
        project.setStatus(ProjectStatus.PRE_PROJECT);
        return project;
    }

    private Document getDocument(Project project) {
        Document document = new Document();
        document.setProject(project);
        document.setStatus(DocumentStatus.PROJECT);
        document.setFileName("fileName");
        document.setProvider(Provider.S3);
        document.setPath("/path");
        return document;
    }

    private void assertProjects(Project expected, Project actual) {
        assertEquals(expected.getUuid(), actual.getUuid());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getDocuments(), actual.getDocuments());
    }
}
