package no.difi.meldingsutveksling.serviceregistry.businesslogic;

import com.google.common.collect.Sets;
import no.difi.meldingsutveksling.serviceregistry.model.OrganizationInfo;
import no.difi.meldingsutveksling.serviceregistry.model.OrganizationType;
import no.difi.meldingsutveksling.serviceregistry.model.OrganizationTypes;

import java.util.Set;
import java.util.function.Predicate;

public class ServiceRecordPredicates {
    public static Predicate<OrganizationInfo> usesPostTilVirksomhet() {

        Set<OrganizationType> privateOrganizationTypes = new OrganizationTypes().privateOrganization();
        return o -> privateOrganizationTypes.contains(o.getOrganizationType());
    }

    public static Predicate<OrganizationInfo> usesFormidlingstjenesten() {
        Set<OrganizationType> publicOrganizationTypes = new OrganizationTypes().publicOrganization();
        return o -> publicOrganizationTypes.contains(o.getOrganizationType());
    }

}
