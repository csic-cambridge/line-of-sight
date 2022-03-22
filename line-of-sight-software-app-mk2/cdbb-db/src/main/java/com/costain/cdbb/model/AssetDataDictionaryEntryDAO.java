package com.costain.cdbb.model;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="asset_dictionary")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AssetDataDictionaryEntryDAO {

    @Id
    private String id;

    @Column(nullable = false)
    private String text;

    @Override
    public String toString() {
        return "AssetDataDictionaryEntry {" +
            "id=" + id +
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
