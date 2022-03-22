package com.costain.cdbb.model;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.Column;
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
@Table(name = "functional_requirement")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FunctionalRequirementDAO {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "fr_fo",
        joinColumns = {@JoinColumn(name = "fr_id")},
        inverseJoinColumns = {@JoinColumn(name = "fo_id")}
    )
    private Set<FunctionalObjectiveDAO> fos;

    @Override
    public String toString() {
        return "FunctionalRequirement {" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", fos='" + fos.stream().map(fo -> fo.getId()).toList() + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctionalRequirementDAO)) return false;
        FunctionalRequirementDAO that = (FunctionalRequirementDAO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) &&
            Objects.equals(fos != null ? fos.stream().map(fo -> fo.getId()).collect(Collectors.toSet()) : null,
                that.fos != null ? that.fos.stream().map(fo -> fo.getId()).collect(Collectors.toSet()) : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name,
            fos != null ? fos.stream().map(fo -> fo.getId()).collect(Collectors.toSet()) : null);
    }
}
