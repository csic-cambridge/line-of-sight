package com.costain.cdbb.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Entity
@Audited
@Table(name = "project_organisational_objective")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
/*

 */
public class ProjectOrganisationalObjectiveDAO {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "oov_id", nullable=false, insertable=true, updatable=true)
    private OoVersionDAO ooVersion; // current oo_version for this project's oo

    @Column(name = "projectId", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID  projectId;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
               name = "poo_frs",
               joinColumns = {@JoinColumn(name = "poo_id")},
               inverseJoinColumns = {@JoinColumn(name = "fr_id")}
    )
    private Set<FunctionalRequirementDAO> frs;

    public void setOoVersion (OoVersionDAO ooVersion) {
        this.ooVersion = ooVersion;
    }

    public void setFrs (Set<FunctionalRequirementDAO> frs) {
        this.frs = frs;
    }

    @Override
    public String toString() {
        return "Project Organisational Objective {" +
            "ProjectId =" + projectId +
            ", oovId='" + (ooVersion == null ? "null" : ooVersion.getId()) + '\'' +
            ", frs='" + (frs == null ? "null" : frs.stream().map(fr -> fr.getId()).toList()) + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectOrganisationalObjectiveDAO that)) return false;
        return Objects.equals(projectId, that.projectId) &&
               Objects.equals(ooVersion, that.ooVersion) &&
                   Objects.equals(frs != null ? frs.stream().map(fr -> fr.getId()).collect(Collectors.toSet()) : null,
                       that.frs != null ? that.frs.stream().map(fr -> fr.getId()).collect(Collectors.toSet()) : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, ooVersion==null ? null : ooVersion.getId(),
            frs != null ? frs.stream().map(fr -> fr.getId()).collect(Collectors.toSet()) : null);
    }
}
