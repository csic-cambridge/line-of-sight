package com.costain.cdbb.model;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
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

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private AssetDataDictionaryEntryDAO dataDictionaryEntry;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "airs", joinColumns = @JoinColumn(name = "assetId", referencedColumnName = "id"))
    private Set<String> airs;

    @Override
    public String toString() {
        return "Asset {" +
            "id=" + id +
            ", dataDictionaryEntry='" + dataDictionaryEntry + '\'' +
            ", airs=" + airs +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssetDAO)) return false;
        AssetDAO assetDAO = (AssetDAO) o;
        return Objects.equals(id, assetDAO.id) && Objects.equals(dataDictionaryEntry, assetDAO.dataDictionaryEntry) &&
            Objects.equals(airs, assetDAO.airs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dataDictionaryEntry, airs);
    }
}
