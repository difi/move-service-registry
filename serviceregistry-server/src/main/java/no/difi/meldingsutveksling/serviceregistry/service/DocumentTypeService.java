package no.difi.meldingsutveksling.serviceregistry.service;

import com.google.common.collect.Sets;
import no.difi.meldingsutveksling.serviceregistry.EntityNotFoundException;
import no.difi.meldingsutveksling.serviceregistry.model.BusinessMessageTypes;
import no.difi.meldingsutveksling.serviceregistry.model.DocumentType;
import no.difi.meldingsutveksling.serviceregistry.persistence.DocumentTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DocumentTypeService {

    private final DocumentTypeRepository repository;

    public DocumentTypeService(DocumentTypeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Optional<DocumentType> findByIdentifier(String identifier) {
        return Optional.ofNullable(repository.findByIdentifier(identifier));
    }

    public Optional<DocumentType> findByBusinessMessageType(BusinessMessageTypes type) {
        return repository.findAll().stream()
                .filter(dt -> dt.getIdentifier().endsWith(type.toString().toLowerCase()))
                .findFirst();
    }

    @Transactional
    public DocumentType add(DocumentType documentType) {
        return repository.save(documentType);
    }

    @Transactional
    public Set<DocumentType> add(Set<DocumentType> documentTypes) {
        Set<DocumentType> persistedDoctypes = Sets.newHashSet();
        for (DocumentType type : documentTypes) {
            Optional<DocumentType> existing = findByIdentifier(type.getIdentifier());
            if (!existing.isPresent()) {
                persistedDoctypes.add(add(type));
            } else {
                persistedDoctypes.add(existing.get());
            }
        }
        return persistedDoctypes;
    }

    @Transactional
    public DocumentType update(String identifier, DocumentType updatedDocumentType) throws EntityNotFoundException {
        DocumentType existing = repository.findByIdentifier(identifier);
        if (null == existing) {
            throw new EntityNotFoundException(updatedDocumentType.getIdentifier());
        }
        if (updatedDocumentType.getProcesses() != null) {
            existing.setProcesses(updatedDocumentType.getProcesses());
        }
        return repository.save(existing);
    }

    @Transactional
    public void delete(DocumentType documentType) {
        repository.delete(documentType);
    }

    @Transactional(readOnly = true)
    public List<DocumentType> findAll() {
        return repository.findAll();
    }
}
