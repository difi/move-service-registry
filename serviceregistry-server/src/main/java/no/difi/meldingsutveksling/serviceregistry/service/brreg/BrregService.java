package no.difi.meldingsutveksling.serviceregistry.service.brreg;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import no.difi.meldingsutveksling.serviceregistry.client.brreg.BrregClient;
import no.difi.meldingsutveksling.serviceregistry.domain.BrregEnhet;
import no.difi.meldingsutveksling.serviceregistry.domain.EntityInfo;
import no.difi.meldingsutveksling.serviceregistry.domain.OrganizationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class BrregService {

    private BrregClient brregClient;
    private DatahotellClient datahotellClient;

    @Autowired
    public BrregService(BrregClient brregClient,
                        DatahotellClient datahotellClient) {
        this.brregClient = brregClient;
        this.datahotellClient = datahotellClient;
    }

    @HystrixCommand(fallbackMethod = "getOrgInfoFromDatahotell")
    public Optional<EntityInfo> getOrganizationInfo(String orgnr) throws BrregNotFoundException {
        Optional<BrregEnhet> entity = brregClient.getBrregEnhetByOrgnr(orgnr);
        if (!entity.isPresent()) {
            entity = brregClient.getBrregUnderenhetByOrgnr(orgnr);
        }
        if (!entity.isPresent()) {
            throw new BrregNotFoundException(String.format("Identifier %s not found in brreg", orgnr));
        }

        return entity.map(OrganizationInfo::of);
    }

    public Optional<EntityInfo> getOrgInfoFromDatahotell(String orgnr, Throwable e) throws BrregNotFoundException {
        if (e instanceof BrregNotFoundException) {
            log.warn("Brreg lookup failed, using hotell.difi.no instead: {}", e.getMessage());
        } else {
            log.error("Brreg lookup threw exception, using hotell.difi.no as fallback", e);
        }
        return datahotellClient.getOrganizationInfo(orgnr);
    }
}
