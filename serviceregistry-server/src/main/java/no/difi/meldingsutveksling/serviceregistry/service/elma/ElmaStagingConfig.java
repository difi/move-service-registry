package no.difi.meldingsutveksling.serviceregistry.service.elma;

import no.difi.vefa.peppol.common.model.TransportProfile;
import no.difi.vefa.peppol.lookup.LookupClient;
import no.difi.vefa.peppol.lookup.LookupClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author Glenn Bech
 */
@Profile({"staging"})
@Configuration
public class ElmaStagingConfig {
    public static final String ELMA_ENDPOINT_KEY = "bdxr-transport-altinn";
    private static final TransportProfile TRANSPORT_PROFILE_ALTINN = new TransportProfile(ELMA_ENDPOINT_KEY);

    @Bean
    public TransportProfile getTransportProfile() {
        return TRANSPORT_PROFILE_ALTINN;
    }

    @Bean
    public LookupClient getElmaLookupClient() {
        return LookupClientBuilder.forTest().endpointCertificateValidator(null).providerCertificateValidator(null).build();
    }
}
