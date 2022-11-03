package com.costain.cdbb.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Audited
@Table(name = "deleted_oir")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DeletedOirDAO implements Serializable {
    private static final long serialVersionUID = -1;

    @EmbeddedId
    private DeletedOirPk deletedOirPk;

    @Override
    public String toString() {
        return "Deleted Oir {" +
            "Pk=" + deletedOirPk.toString() +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeletedOirDAO)) return false;
        DeletedOirDAO deletedOirDAO = (DeletedOirDAO) o;
        return Objects.equals(deletedOirPk, deletedOirDAO.deletedOirPk)
            ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(deletedOirPk//, project.getId()
             );
    }
}
