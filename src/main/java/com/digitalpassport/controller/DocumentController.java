package com.digitalpassport.controller;

import com.digitalpassport.entity.Document;
import com.digitalpassport.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Document> getAllDocuments() {
        return documentService.getAllDocuments();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Document saveDocument(@RequestBody Document newDocument) {
        return documentService.saveDocument(newDocument);
    }

    @GetMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Document getDocument(@PathVariable UUID uuid) {
        return documentService.getDocument(uuid);
    }

    @PutMapping(value = "/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Document updateDocument(@PathVariable UUID uuid, @RequestBody Document newDocument) {
        return documentService.updateDocument(uuid, newDocument);
    }

    @DeleteMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> deleteDocument(@PathVariable UUID uuid) {
        documentService.deleteDocument(uuid);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/page/{index}/{size}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Document> getDocumentPage(@PathVariable Integer index, @PathVariable Integer size) {
        return documentService.getDocumentPage(index, size);
    }
}
