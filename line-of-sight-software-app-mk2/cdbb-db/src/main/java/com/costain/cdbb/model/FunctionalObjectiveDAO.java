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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "functional_objective")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FunctionalObjectiveDAO {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private FunctionalObjectiveDataDictionaryEntryDAO dataDictionaryEntry;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "firs", joinColumns = @JoinColumn(name = "foId", referencedColumnName = "id"))
    private Set<String> firs;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "fo_assets",
        joinColumns = {@JoinColumn(name = "fo_id")},
        inverseJoinColumns = {@JoinColumn(name = "asset_id")}
    )
    private Set<AssetDAO> assets;

    @Override
    public String toString() {
        return "FunctionalObjective {" +
            "id=" + id +
            ", dataDictionaryEntry='" + dataDictionaryEntry + '\'' +
            ", firs='" + firs + '\'' +
            ", assets='" + assets.stream().map(asset -> asset.getId()).toList() + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctionalObjectiveDAO)) return false;
        FunctionalObjectiveDAO that = (FunctionalObjectiveDAO) o;
        return Objects.equals(id, that.id) && Objects.equals(dataDictionaryEntry, that.dataDictionaryEntry) &&
            Objects.equals(firs, that.firs) &&
            Objects.equals(assets != null ?
                    assets.stream().map(asset -> asset.getId()).collect(Collectors.toSet()) : null,
                that.assets != null ?
                    that.assets.stream().map(asset -> asset.getId()).collect(Collectors.toSet()) : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dataDictionaryEntry, firs,
            assets != null ? assets.stream().map(asset -> asset.getId()).collect(Collectors.toSet()) : null);
    }
}
