package com.costain.cdbb.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OrderBy;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Audited
@Table(name = "functional_output")
@Getter
@Setter//(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FunctionalOutputDAO {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")

    private UUID id;

    @Column(name = "projectId", columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID projectId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private FunctionalOutputDataDictionaryEntryDAO dataDictionaryEntry;



    @OneToMany(mappedBy="foDao", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FirsDAO> firs;

    public void setFirs(Set<FirsDAO> _firs) {
        this.firs = _firs;
        this.setFirs();
    }

    public void setFirs() {
        for (FirsDAO firsDao : this.firs) {
            firsDao.setFoDao(this);
        }
    }

    public void setFoDaoInFirs() {
        if (this.firs == null) {
            return;
        }
        for (FirsDAO firsDao : this.firs) {
            firsDao.setFoDao(this);
        }
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "fo_assets",
        joinColumns = {@JoinColumn(name = "fo_id")},
        inverseJoinColumns = {@JoinColumn(name = "asset_id")}
    )
    private Set<AssetDAO> assets;

    @Override
    public String toString() {
        return "FunctionalOutput {" +
            "id=" + id +
            "projectId=" + projectId +
            ", dataDictionaryEntry='" + dataDictionaryEntry + '\'' +
            ", firs='" + firs + '\'' +
            ", assets='" + (assets == null ? "null" : assets.stream().map(asset -> asset.getId()).toList()) + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctionalOutputDAO)) return false;
        FunctionalOutputDAO that = (FunctionalOutputDAO) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(projectId, that.getProjectId()) &&
            Objects.equals(dataDictionaryEntry, that.dataDictionaryEntry) &&
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

    public static FunctionalOutputDAO.FunctionalOutputDAOBuilder builder() {
        return new FunctionalOutputDAO.CustomFoDAOBuilder();
    }

    public static class CustomFoDAOBuilder extends FunctionalOutputDAO.FunctionalOutputDAOBuilder {
        @Override
        public FunctionalOutputDAO build() {
            FunctionalOutputDAO foDao = super.build();
            foDao.setFoDaoInFirs();
            return foDao;
        }
    }
}
