package no.difi.meldingsutveksling.serviceregistry.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

public class OrganizationInfo implements Serializable {

    private ServiceIdentifier primaryServiceIdentifier;
    private String organisationNumber;
    private String organizationName;
    private OrganizationType organizationType;
    private static final long serialVersionUID = 7526471155622776555L;

    // Needed by the JSON marshaller?
    public OrganizationInfo() {
    }

    /**
     * @param primaryServiceIdentifier name/id of preferred transport service
     * @param organisationNumber of the recipient organization
     * @param organizationName as name implies
     * @param organizationType or organization form as defined in BRREG
     */
    public OrganizationInfo(ServiceIdentifier primaryServiceIdentifier, String organisationNumber, String organizationName, OrganizationType organizationType) {
        this.primaryServiceIdentifier = primaryServiceIdentifier;
        this.organisationNumber = organisationNumber;
        this.organizationName = organizationName;
        this.organizationType = organizationType;
    }

    public ServiceIdentifier getPrimaryServiceIdentifier() {
        return primaryServiceIdentifier;
    }

    public void setPrimaryServiceIdentifier(ServiceIdentifier primaryServiceIdentifier) {
        this.primaryServiceIdentifier = primaryServiceIdentifier;
    }

    public void setOrganisationNumber(String organisationNumber) {
        this.organisationNumber = organisationNumber;
    }

    public String getOrganisationNumber() {
        return organisationNumber;
    }

    public String getOrganizationName() {
        return this.organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public static class Builder {
        private OrganizationInfo organizationInfo = new OrganizationInfo();

        public Builder() {}

        /**
         * @return a new instance of OrganizationInfo
         */
        public OrganizationInfo build() {
            return organizationInfo;
        }

        public Builder withPrimaryServiceIdentifier(ServiceIdentifier primaryServiceIdentifier) {
            organizationInfo.setPrimaryServiceIdentifier(primaryServiceIdentifier);
            return this;
        }

        public Builder withOrganizationNumber(String organisationNumber) {
            organizationInfo.setOrganisationNumber(organisationNumber);
            return this;
        }

        public Builder withOrganizationName(String organizationName) {
            organizationInfo.setOrganizationName(organizationName);
            return this;
        }

        public Builder setOrganizationType(OrganizationType organizationType) {
            organizationInfo.organizationType = organizationType;
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationInfo that = (OrganizationInfo) o;
        return primaryServiceIdentifier == that.primaryServiceIdentifier &&
                Objects.equal(organisationNumber, that.organisationNumber) &&
                Objects.equal(organizationName, that.organizationName) &&
                Objects.equal(organizationType, that.organizationType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(primaryServiceIdentifier, organisationNumber, organizationName, organizationType);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("primaryServiceIdentifier", primaryServiceIdentifier)
                .add("organisationNumber", organisationNumber)
                .add("organizationName", organizationName)
                .add("organizationType", organizationType)
                .toString();
    }
}