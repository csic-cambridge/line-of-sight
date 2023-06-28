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
@Table(name = "oirs")
@Getter
@Setter //(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OirDAO {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne()
    @JoinColumn(name = "oo_id", referencedColumnName = "id")
    private OrganisationalObjectiveDAO ooDao;

    @ManyToMany
    @JoinTable(
        name = "oirs_airs",
        joinColumns = @JoinColumn(name = "oirs_id"),
        inverseJoinColumns = @JoinColumn(name = "airs_id"))
    Set<AirsDAO> airs;


    @Column(name="oirs")
    String oirs;

    @Column(name = "oo_id", insertable=false, updatable=false)
    String ooId;

    public void setOoDao (OrganisationalObjectiveDAO ooDao) {
        this.ooDao = ooDao;
    }
    public void setAirs( Set<AirsDAO> airs) {
        this.airs = airs;
    }

    @Override
    public String toString() {
        return "OirDAO {" +
            "id=" + id +
            ", oo_id=" + (ooDao == null ? "null" : ooDao.getId()) +
            ", oir=" + oirs +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OirDAO)) return false;
        OirDAO oirDAO = (OirDAO) o;
        return Objects.equals(id, oirDAO.id) &&
            Objects.equals(ooId, oirDAO.ooId) &&
            Objects.equals(oirs, oirDAO.oirs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ooId, oirs);
    }
}
