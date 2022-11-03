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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Audited
@Table(name = "functional_requirement")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FunctionalRequirementDAO {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "projectId", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID  projectId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "fr_fo",
        joinColumns = {@JoinColumn(name = "fr_id")},
        inverseJoinColumns = {@JoinColumn(name = "fo_id")}
    )
    private Set<FunctionalOutputDAO> fos;

    @Override
    public String toString() {
        return "FunctionalRequirement {" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", project=" + projectId +
            ", fos='" + (fos == null ? "null" : fos.stream().map(fo -> fo.getId()).toList()) + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctionalRequirementDAO)) return false;
        FunctionalRequirementDAO that = (FunctionalRequirementDAO) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(projectId, that.projectId) &&
               Objects.equals(fos != null ? fos.stream().map(fo -> fo.getId()).collect(Collectors.toSet()) : null,
                that.fos != null ? that.fos.stream().map(fo -> fo.getId()).collect(Collectors.toSet()) : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name,
            fos != null ? fos.stream().map(fo -> fo.getId()).collect(Collectors.toSet()) : null);
    }
}
