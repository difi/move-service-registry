package no.difi.meldingsutveksling.serviceregistry;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
