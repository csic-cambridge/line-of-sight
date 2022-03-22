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
@Table(name="functional_objective_dictionary")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FunctionalObjectiveDataDictionaryEntryDAO {

    @Id
    private String id;

    @Column(nullable = false)
    private String text;

    @Override
    public String toString() {
        return "FunctionalObjectiveDataDictionaryEntry {" +
            "id=" + id +
            ", text='" + text + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FunctionalObjectiveDataDictionaryEntryDAO)) return false;
        FunctionalObjectiveDataDictionaryEntryDAO that = (FunctionalObjectiveDataDictionaryEntryDAO) o;
        return Objects.equals(id, that.id) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }
}
