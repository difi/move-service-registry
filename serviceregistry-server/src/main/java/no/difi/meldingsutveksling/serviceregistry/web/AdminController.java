package no.difi.meldingsutveksling.serviceregistry.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import no.difi.meldingsutveksling.serviceregistry.EntityNotFoundException;
import no.difi.meldingsutveksling.serviceregistry.model.DocumentType;
import no.difi.meldingsutveksling.serviceregistry.model.Process;
import no.difi.meldingsutveksling.serviceregistry.service.DocumentTypeService;
import no.difi.meldingsutveksling.serviceregistry.service.ProcessService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Api(value = "Administration", tags = {"Administration"})
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class AdminController {

    private final ProcessService processService;
    private final DocumentTypeService documentTypeService;

    public AdminController(ProcessService processService, DocumentTypeService documentTypeService) {
        this.processService = processService;
        this.documentTypeService = documentTypeService;
    }

    @ApiOperation(value = "Get a Process", tags = {"Process", "Administration"})
    @GetMapping(value = "/processes/{identifier:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Process> getProcess(@ApiParam("Process identifier") @PathVariable String identifier) {
        Optional<Process> process = processService.findByIdentifier(identifier);
        return process.isPresent()
                ? ResponseEntity.ok(process.get())
                : ResponseEntity.notFound().build();
    }

    @ApiOperation(value = "Add a process", tags = {"Process", "Administration"})
    @PostMapping(value = "/processes", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity addProcess(@ApiParam("Process data") @RequestBody Process process) {
        try {
            Optional<Process> existingProcess = processService.findByIdentifier(process.getIdentifier());
            if (existingProcess.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            Set<DocumentType> persistedDoctypes = documentTypeService.add(process.getDocumentTypes());
            processService.save(process, persistedDoctypes);
            UriComponents uriComponents = UriComponentsBuilder.fromUriString("/processes")
                    .path(process.getIdentifier())
                    .build();
            return ResponseEntity.created(uriComponents.toUri()).build();
        } catch (Exception e) {
            log.error("Exception during process save", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "Delete a Process", tags = {"Process", "Administration"})
    @DeleteMapping(value = "/processes/{identifier:.+}")
    public ResponseEntity deleteProcess(@ApiParam("Process identifier") @PathVariable String identifier) {
        try {
            Optional<Process> process = processService.findByIdentifier(identifier);
            if (process.isPresent()) {
                processService.delete(process.get());
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "List All Processes", tags = {"Process", "Administration"})
    @GetMapping(value = "/processes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Process>> getProcesses() {
        try {
            return ResponseEntity.ok(processService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @ApiOperation(value = "Update a Process", tags = {"Process", "Administration"})
    @PutMapping(value = "/processes/{processIdentifier:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateProcess(@ApiParam("Process identifier") @PathVariable String processIdentifier,
                                        @ApiParam("Process data") @RequestBody Process processWithValuesForUpdate) {
        try {
            processService.update(processIdentifier, processWithValuesForUpdate);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "Get a Document Type", tags = {"Document Type", "Administration"})
    @GetMapping(value = "/documentTypes/{identifier}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentType> getDocumentType(@ApiParam("Document type identifier") @PathVariable String identifier) {
        Optional<DocumentType> documentType = documentTypeService.findByIdentifier(identifier);
        return documentType.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @ApiOperation(value = "Add a Document Type", tags = {"Document Type", "Administration"})
    @PostMapping(value = "/documentTypes", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity addDocumentType(@ApiParam("Document type data") @RequestBody DocumentType documentType) {
        try {
            Optional<DocumentType> existingDocumentType = documentTypeService.findByIdentifier(documentType.getIdentifier());
            if (existingDocumentType.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            documentTypeService.add(documentType);
            UriComponents uriComponents = UriComponentsBuilder.fromUriString("/documentType")
                    .path(documentType.getIdentifier())
                    .build();
            return ResponseEntity.created(uriComponents.toUri()).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "Delete a Document Type", tags = {"Document Type", "Administration"})
    @DeleteMapping(value = "/documentTypes/{identifier:.+}")
    public ResponseEntity deleteDocumentType(@ApiParam("Document type identifier") @PathVariable String identifier) {
        try {
            Optional<DocumentType> documentType = documentTypeService.findByIdentifier(identifier);
            if (documentType.isPresent()) {
                documentTypeService.delete(documentType.get());
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "Update a Document Type", tags = {"Document Type", "Administration"})
    @PutMapping(value = "/documentTypes/{identifier:.+}")
    public ResponseEntity updateDocumentType(@ApiParam("Document type identifier") @PathVariable String identifier,
                                             @ApiParam("Updated document type") @RequestBody DocumentType documentType) {
        try {
            documentTypeService.update(identifier, documentType);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @ApiOperation(value = "List All Document Types", tags = {"Document Type", "Administration"})
    @GetMapping(value = "/documentTypes", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DocumentType>> getDocumentTypes() {
        try {
            return ResponseEntity.ok(documentTypeService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
