package com.digitalpassport.service;

import com.digitalpassport.entity.Document;
import com.digitalpassport.exception.persistence.AttemptToUpdateIdException;
import com.digitalpassport.exception.persistence.EntityNotFoundException;
import com.digitalpassport.repository.DocumentRepository;
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
public class DocumentService {
    private final DocumentRepository documentRepository;

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Document saveDocument(Document newDocument) {
        return documentRepository.save(newDocument);
    }

    public Document getDocument(UUID uuid) {
        return documentRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);
    }

    public Document updateDocument(UUID uuid, Document newDocument) {
        if (newDocument.getUuid() != null && !uuid.equals(newDocument.getUuid())) {
            throw new AttemptToUpdateIdException();
        }

        return documentRepository.findById(uuid).map(document -> {
            newDocument.setUuid(document.getUuid());
            return documentRepository.save(newDocument);
        }).orElseThrow(EntityNotFoundException::new);
    }

    public void deleteDocument(UUID uuid) {
        documentRepository.deleteById(uuid);
    }

    public List<Document> getDocumentPage(int index, int size) {
        return documentRepository.findAll(PageRequest.of(index, size, Sort.by("uuid"))).getContent();
    }
}
