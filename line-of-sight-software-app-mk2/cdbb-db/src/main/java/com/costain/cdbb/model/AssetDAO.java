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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Audited
@Table(name = "asset")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AssetDAO {

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
    private AssetDataDictionaryEntryDAO dataDictionaryEntry;

    @OneToMany(mappedBy="assetDao", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AirsDAO> airs;

    public void setAirs(Set<AirsDAO> _airs){
        this.airs = _airs;
        this.setAirs();
    }

    public void setAirs(){
        for (AirsDAO airsDao : this.airs) {
            airsDao.setAssetDao(this);
        }
    }

    public void setAssetDaoInAirs() {
        if (this.airs == null) {
            return;
        }
        for (AirsDAO airsDao : this.airs) {
            airsDao.setAssetDao(this);
        }
    }

    @Override
    public String toString() {
        return "Asset {" +
            "id=" + id +
            ", projectId = " + projectId +
            ", dataDictionaryEntry='" + dataDictionaryEntry + '\'' +
            ", airs=" + airs +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssetDAO)) return false;
        AssetDAO assetDAO = (AssetDAO) o;
        return Objects.equals(id, assetDAO.id) &&
            Objects.equals(projectId, assetDAO.getProjectId()) &&
            Objects.equals(dataDictionaryEntry, assetDAO.dataDictionaryEntry) &&
            Objects.equals(airs, assetDAO.airs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dataDictionaryEntry, airs);
    }

    public static AssetDAOBuilder builder() {
        return new CustomAssetDAOBuilder();
    }

    public static class CustomAssetDAOBuilder extends AssetDAOBuilder{
        @Override
        public AssetDAO build() {
           AssetDAO assetDao = super.build();
           assetDao.setAssetDaoInAirs();
           return assetDao;
        }
    }
}

