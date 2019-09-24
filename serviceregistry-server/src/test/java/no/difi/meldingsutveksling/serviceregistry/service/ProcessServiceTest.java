package no.difi.meldingsutveksling.serviceregistry.service;

import no.difi.meldingsutveksling.serviceregistry.EntityNotFoundException;
import no.difi.meldingsutveksling.serviceregistry.model.DocumentType;
import no.difi.meldingsutveksling.serviceregistry.model.Process;
import no.difi.meldingsutveksling.serviceregistry.model.ProcessCategory;
import no.difi.meldingsutveksling.serviceregistry.persistence.ProcessRepository;
import org.assertj.core.util.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProcessServiceTest {

    @InjectMocks
    private ProcessService target;

    @Mock
    private ProcessRepository repositoryMock;

    @Mock
    private DocumentTypeService documentTypeServiceMock;

    @Test(expected = EntityNotFoundException.class)
    public void update_ProcessNotFound_ShouldThrow() throws EntityNotFoundException {
        when(repositoryMock.findByIdentifier(anyString())).thenReturn(null);
        Process updatedProcess = createProcess("n/a", "n/a", null, null);

        Process result = target.update("process", updatedProcess);

        assertNull(result);
        verify(repositoryMock, never()).save(any(Process.class));
    }

    @Test
    public void update_ChangeServiceCode_ShouldBeSaved() throws EntityNotFoundException {
        Process existingProcessMock = mock(Process.class);
        when(repositoryMock.findByIdentifier(anyString())).thenReturn(existingProcessMock);
        String newCode = "newCode";
        Process updatedProcess = createProcess(newCode, null, null, null);
        when(repositoryMock.save(existingProcessMock)).thenReturn(existingProcessMock);

        Process result = target.update("process", updatedProcess);

        assertNotNull(result);
        verify(repositoryMock).save(any(Process.class));
        verify(existingProcessMock).setServiceCode(newCode);
    }

    @Test
    public void update_ChangeServiceEditionCode_ShouldBeSaved() throws EntityNotFoundException {
        Process existingProcessMock = mock(Process.class);
        when(repositoryMock.findByIdentifier(anyString())).thenReturn(existingProcessMock);
        String newCode = "newCode";
        Process updatedProcess = createProcess(null, newCode, null, null);
        when(repositoryMock.save(existingProcessMock)).thenReturn(existingProcessMock);

        Process result = target.update("process", updatedProcess);

        assertNotNull(result);
        verify(repositoryMock).save(any(Process.class));
        verify(existingProcessMock).setServiceEditionCode(newCode);
    }

    @Test
    public void update_ChangeCategory_ShouldBeSaved() throws EntityNotFoundException {
        Process existingProcessMock = mock(Process.class);
        when(repositoryMock.findByIdentifier(anyString())).thenReturn(existingProcessMock);
        String newCode = "newCode";
        ProcessCategory newCategory = ProcessCategory.ARKIVMELDING;
        Process updatedProcess = createProcess(null, newCode, null, newCategory);
        when(repositoryMock.save(existingProcessMock)).thenReturn(existingProcessMock);

        Process result = target.update("process", updatedProcess);

        assertNotNull(result);
        verify(repositoryMock).save(any(Process.class));
        verify(existingProcessMock).setCategory(newCategory);
    }

    @Test
    public void update_ChangeDocumentTypes_ShouldBeSaved() throws EntityNotFoundException {
        Process existingProcessMock = mock(Process.class);
        when(repositoryMock.findByIdentifier(any())).thenReturn(existingProcessMock);
        Set<DocumentType> newTypes = Sets.newHashSet();
        DocumentType documentTypeMock = mock(DocumentType.class);
        newTypes.add(documentTypeMock);
        Process updatedProcess = createProcess(null, null, newTypes, null);
        when(repositoryMock.save(existingProcessMock)).thenReturn(existingProcessMock);

        Process result = target.update("process", updatedProcess);

        assertNotNull(result);
        verify(repositoryMock).save(any(Process.class));
    }

    @Test
    public void update_ProcessContainsNonExistingDocumentType_ShouldAddDocumentTypeAndSucceed() throws EntityNotFoundException {
        Process existingProcessMock = mock(Process.class);
        when(repositoryMock.findByIdentifier(anyString())).thenReturn(existingProcessMock);
        Set<DocumentType> newTypes = Sets.newHashSet();
        DocumentType documentTypeMock = mock(DocumentType.class);
        newTypes.add(documentTypeMock);
        Process updatedProcess = createProcess(null, null, newTypes, null);
        when(repositoryMock.save(existingProcessMock)).thenReturn(existingProcessMock);

        Process result = target.update("process", updatedProcess);

        assertNotNull(result);
        verify(documentTypeServiceMock).add(anySet());
    }

    private static Process createProcess(String serviceCode, String serviceEditionCode, Set<DocumentType> documentTypes, ProcessCategory category) {
        Process updatedProcess = new Process();
        updatedProcess.setServiceCode(serviceCode);
        updatedProcess.setServiceEditionCode(serviceEditionCode);
        updatedProcess.setCategory(category);
        updatedProcess.setDocumentTypes(documentTypes);
        return updatedProcess;
    }
}
