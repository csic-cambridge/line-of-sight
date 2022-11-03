package com.costain.cdbb.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "project")
@Getter
@Setter//(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectDAO {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "assetDdId", referencedColumnName = "id")
    private AssetDataDictionaryDAO assetDataDictionary;

    @OneToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "foDdId", referencedColumnName = "id")
    private FunctionalOutputDataDictionaryDAO foDataDictionary;

    @OneToMany(mappedBy = "id.projectId", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private List<UserProjectPermissionDAO> userProjectPermissionDao;

    @OneToMany(mappedBy = "projectId", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<ProjectOrganisationalObjectiveDAO> projectOrganisationalObjectiveDaos;

    @OneToMany(mappedBy = "projectId", cascade = { CascadeType.PERSIST, CascadeType.MERGE})
    private Set<FunctionalRequirementDAO> functionRequirementDaos;

    @Override
    public String toString() {
        return "Project {" +
            "id=" + id +
            ", name=" + name +
            ", assetDataDictionary='" + assetDataDictionary + '\'' +
            ", functionalOutputDataDictionary='" + foDataDictionary +
            ", projectOrganisationalObjectiveDAOs = " + (projectOrganisationalObjectiveDaos ==null
                                                       ? "null"
                                                        : projectOrganisationalObjectiveDaos.size()) +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectDAO)) return false;
        ProjectDAO projectDAO = (ProjectDAO) o;
        return Objects.equals(id, projectDAO.id) &&
            Objects.equals(name, projectDAO.getName()) &&
            Objects.equals(assetDataDictionary, projectDAO.assetDataDictionary) &&
            Objects.equals(foDataDictionary, projectDAO.foDataDictionary)
            ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, assetDataDictionary, foDataDictionary);
    }
}
