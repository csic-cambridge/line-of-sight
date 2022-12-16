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
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Audited
@Table(name = "organisational_objective")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganisationalObjectiveDAO {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @OneToMany(mappedBy="ooDao", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @org.hibernate.annotations.OrderBy(clause = "oirs ASC")
    private Collection<OirDAO> oirDaos;


    @OneToMany(mappedBy="oo", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @OrderBy("dateCreated DESC")
    private List<OoVersionDAO> ooVersions;

    public void addLatestOoVersion(OoVersionDAO ooVersion) {
        ooVersions.add(0, ooVersion);
    }

    @Column(nullable = false)
    private boolean isDeleted;

    @Transient
    private String name;

    public String getName() {
        if (ooVersions == null) {
            return name;
        }
        else {
            return ooVersions.get(0).getName();
        }
    }

    public void setOoVersion (OoVersionDAO ooVersion) {
        // used when creating OO
        this.ooVersions = new ArrayList<OoVersionDAO>(Arrays.asList(ooVersion));
    }

    public void setOirs(List<OirDAO>oirDaos) {
        this.oirDaos = oirDaos;
    }
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "Organisational Objective {" +
            "id=" + id +
            ", versions='" + ooVersions + '\'' +
            ", oirs='" + oirDaos + '\'' +
              ", Is deleted = " + isDeleted +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrganisationalObjectiveDAO)) return false;
        OrganisationalObjectiveDAO that = (OrganisationalObjectiveDAO) o;
        return Objects.equals(id, that.id) && Objects.equals(ooVersions, that.ooVersions)
            && Objects.equals(oirDaos, that.oirDaos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ooVersions, oirDaos);
    }
}
