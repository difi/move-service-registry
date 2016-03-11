package no.difi.meldingsutveksling.serviceregistry;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ServiceRegistryException extends RuntimeException {
    public ServiceRegistryException(Exception e) {
        super(e);
    }
}