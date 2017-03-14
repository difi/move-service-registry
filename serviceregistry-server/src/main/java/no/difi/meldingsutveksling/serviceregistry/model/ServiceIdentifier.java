package no.difi.meldingsutveksling.serviceregistry.model;

/**
 * Identifiers for the individual services available for transport of messages
 */
public enum ServiceIdentifier {
    /**
     * Identifies archive-to-archive transportation
     */
    DPO,
    /**
     * Identifies using Altinn correspondence agency as transport
     */
    DPV,
    FIKS,
     /**
     * Identifies DIFI sikker digital post aka. Digital Post Innbygger
     */
    DPI,
    /**
     * Identifies eInnsyn
     */
    DPE_innsyn,
    DPE_data
}
