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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Audited
@Table(name = "firs")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FirsDAO {
    public final static String NEW_ID = ""; // used in api to indicate a new Firs is to be created

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id; // NOTE: builder Setter uses String parameter(see below)

    @ManyToOne()
    @JoinColumn(name = "fo_id", referencedColumnName = "id")
    private FunctionalOutputDAO foDao;

    @Column(name="firs")
    String firs;

    @Override
    public String toString() {
        return "Firs {" +
            "id=" + id +
            ", fo_id=" + (foDao == null ? "null" : foDao.getId()) +
            ", firs=" + firs +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FirsDAO)) return false;
        FirsDAO firsDAO = (FirsDAO) o;
        return Objects.equals(id, firsDAO.id) &&
            ((foDao == null && firsDAO.getFoDao() == null) ||
            Objects.equals(foDao.getId(), firsDAO.getFoDao().getId())) &&
            Objects.equals(firs, firsDAO.firs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, (foDao == null ? "null" : foDao.getId()), firs);
    }

    public static class FirsDAOBuilder {
        public FirsDAO.FirsDAOBuilder id (String id) {
            this.id = id.equals(NEW_ID) ? null : UUID.fromString(id);
            return this;
        }
    }
}
