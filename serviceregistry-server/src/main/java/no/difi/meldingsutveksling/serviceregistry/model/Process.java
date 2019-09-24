package no.difi.meldingsutveksling.serviceregistry.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.Set;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "identifier"))
@Data
@ToString(exclude = "documentTypes")
@EqualsAndHashCode(exclude = "documentTypes")
public class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String identifier;
    private String serviceCode;
    private String serviceEditionCode;

    @Enumerated(EnumType.STRING)
    private ProcessCategory category;

    @ManyToMany
    @JoinTable(
            name = "process_document_type",
            joinColumns = @JoinColumn(name = "proc_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "doctype_id", referencedColumnName = "id")
    )
    @JsonIgnoreProperties("processes")
    private Set<DocumentType> documentTypes;

}
