package no.difi.meldingsutveksling.serviceregistry.service;

import com.google.common.collect.Sets;
import no.difi.meldingsutveksling.serviceregistry.EntityNotFoundException;
import no.difi.meldingsutveksling.serviceregistry.config.ServiceregistryProperties;
import no.difi.meldingsutveksling.serviceregistry.model.DocumentType;
import no.difi.meldingsutveksling.serviceregistry.model.Process;
import no.difi.meldingsutveksling.serviceregistry.model.ProcessCategory;
import no.difi.meldingsutveksling.serviceregistry.persistence.ProcessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProcessService {

    private final ProcessRepository repository;
    private final ServiceregistryProperties props;
    private final DocumentTypeService documentTypeService;

    public ProcessService(ProcessRepository repository, ServiceregistryProperties props, DocumentTypeService documentTypeService) {
        this.repository = repository;
        this.props = props;
        this.documentTypeService = documentTypeService;
    }

    @Transactional(readOnly = true)
    public Optional<Process> findByIdentifier(String identifier) {
        return Optional.ofNullable(repository.findByIdentifier(identifier));
    }

    @Transactional
    public Process save(Process process, Set<DocumentType> documentTypes) {
        process.setDocumentTypes(documentTypes);
        return save(process);
    }

    @Transactional
    Process save(Process process) {
        return repository.save(process);
    }

    @Transactional
    public Process update(String processIdentifier, Process updatedProcess) throws EntityNotFoundException {
        Optional<Process> optionalProcess = findByIdentifier(processIdentifier);
        if (!optionalProcess.isPresent()) {
            throw new EntityNotFoundException(processIdentifier);
        }
        Process process = optionalProcess.get();
        Set<DocumentType> documentTypes = updatedProcess.getDocumentTypes();
        Set<DocumentType> updatedDocumentTypes = Sets.newHashSet();
        if (documentTypes != null) {
            updatedDocumentTypes = documentTypeService.add(documentTypes);
        }
        if (updatedProcess.getServiceCode() != null) {
            process.setServiceCode(updatedProcess.getServiceCode());
        }
        if (updatedProcess.getServiceEditionCode() != null) {
            process.setServiceEditionCode(updatedProcess.getServiceEditionCode());
        }
        if (updatedProcess.getCategory() != null) {
            process.setCategory(updatedProcess.getCategory());
        }
        return updatedDocumentTypes.isEmpty()
                ? save(process)
                : save(process, updatedDocumentTypes);
    }

    @Transactional
    public void delete(Process process) {
        repository.delete(process);
    }

    @Transactional(readOnly = true)
    public List<Process> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Set<Process> findAll(ProcessCategory processCategory) {
        return repository.findAllByCategory(processCategory);
    }

    @Transactional(readOnly = true)
    public Process getDefaultArkivmeldingProcess() {
        return repository.findByIdentifier(props.getElma().getDefaultProcessIdentifier());
    }
}
