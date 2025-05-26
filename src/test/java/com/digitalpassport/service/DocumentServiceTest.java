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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DocumentServiceTest extends BaseIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocumentService documentService;

    @Test
    void testGetDocuments() {
        init();
        List<Document> documents = documentService.getAllDocuments();
        Document document = documents.getFirst();
        Project project = document.getProject();
        assertEquals(1, documents.size());
        assertNotNull(project);
    }

    @Test
    void testSaveDocument() {
        Project project = getProject();
        projectRepository.save(project);
        Document expected = getDocument(project);
        documentService.saveDocument(expected);
        Document actual = documentRepository.findById(expected.getUuid()).orElseThrow();
        assertDocuments(expected, actual);
    }

    @Test
    void testGetDocument() {
        assertThrows(EntityNotFoundException.class, () -> documentService.getDocument(UUID.randomUUID()));
        Document expected = init();
        Document actual = documentService.getDocument(expected.getUuid());
        assertDocuments(expected, actual);
    }

    @Test
    void testUpdateDocument() {
        Document expected = init();
        assertThrows(AttemptToUpdateIdException.class, () -> documentService.updateDocument(UUID.randomUUID(), expected));
        expected.setStatus(DocumentStatus.WORKING);
        Document actual = documentService.updateDocument(expected.getUuid(), expected);
        assertDocuments(expected, actual);
    }

    @Test
    void testDeleteDocument() {
        Document expected = init();
        long before = documentRepository.findById(expected.getUuid()).stream().count();
        documentService.deleteDocument(expected.getUuid());
        long after = documentRepository.findById(expected.getUuid()).stream().count();
        assertTrue(before > after);
    }

    @Test
    void testGetDocumentPage() {
        init();
        List<Document> firstPage = documentService.getDocumentPage(0, 1);
        assertEquals(1, firstPage.size());
        List<Document> secondPage = documentService.getDocumentPage(1, 1);
        assertEquals(0, secondPage.size());
    }

    private Document init() {
        Project project = getProject();
        projectRepository.save(project);
        Document document = getDocument(project);
        return documentRepository.save(document);
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

    private void assertDocuments(Document expected, Document actual) {
        assertEquals(expected.getUuid(), actual.getUuid());
        assertEquals(expected.getProject(), actual.getProject());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getFileName(), actual.getFileName());
        assertEquals(expected.getProvider(), actual.getProvider());
        assertEquals(expected.getPath(), actual.getPath());
    }
}
