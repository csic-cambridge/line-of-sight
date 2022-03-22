package com.costain.cdbb.model;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organisational_objective")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganisationalObjectiveDAO {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "oirs", joinColumns = @JoinColumn(name = "ooId", referencedColumnName = "id"))
    private Set<String> oirs;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "oo_frs",
        joinColumns = {@JoinColumn(name = "oo_id")},
        inverseJoinColumns = {@JoinColumn(name = "fr_id")}
    )
    private Set<FunctionalRequirementDAO> frs;

    @Override
    public String toString() {
        return "FunctionalObjective {" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", oirs='" + oirs + '\'' +
            ", frs='" + frs.stream().map(fr -> fr.getId()).toList() + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrganisationalObjectiveDAO)) return false;
        OrganisationalObjectiveDAO that = (OrganisationalObjectiveDAO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(oirs, that.oirs) &&
            Objects.equals(frs != null ? frs.stream().map(fr -> fr.getId()).collect(Collectors.toSet()) : null,
                that.frs != null ? that.frs.stream().map(fr -> fr.getId()).collect(Collectors.toSet()) : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, oirs,
            frs != null ? frs.stream().map(fr -> fr.getId()).collect(Collectors.toSet()) : null);
    }
}
