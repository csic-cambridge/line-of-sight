package com.costain.cdbb.model;

import java.util.Objects;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@Table(name="asset_dictionary_entry")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AssetDataDictionaryEntryDAO {

    @Id
    private String id; // arbitrary string key

    @Column(nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID assetDictionaryId;

    @Column(nullable = false)
    private String text;

    @Override
    public String toString() {
        return "AssetDataDictionaryEntry {" +
            "id=" + id +
            ", dictionary id = " + assetDictionaryId +
            ", text='" + text + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssetDataDictionaryEntryDAO)) return false;
        AssetDataDictionaryEntryDAO that = (AssetDataDictionaryEntryDAO) o;
        return Objects.equals(id, that.id) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }
}
