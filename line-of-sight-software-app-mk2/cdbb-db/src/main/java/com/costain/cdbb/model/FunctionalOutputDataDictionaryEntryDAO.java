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
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name="functional_output_dictionary_entry")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FunctionalOutputDataDictionaryEntryDAO {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @Column(nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID foDictionaryId;

    @Column(nullable = false)
    private String entryId;

    @Column(nullable = false)
    private String text;

    @Override
    public String toString() {
        return "FunctionalOutputDataDictionaryEntry {" +
            "id=" + id +
            ", dictionary id = " + foDictionaryId +
            ", text='" + text + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctionalOutputDataDictionaryEntryDAO)) return false;
        FunctionalOutputDataDictionaryEntryDAO that = (FunctionalOutputDataDictionaryEntryDAO) o;
        return Objects.equals(id, that.id) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }
}
