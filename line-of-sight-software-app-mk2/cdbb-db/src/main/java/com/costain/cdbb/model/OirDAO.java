package com.costain.cdbb.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "oirs")
@Getter
//@Setter(AccessLevel.PROTECTED)
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

    @Column(name="oirs")
    String oir;

    @Column(name = "oo_id",  insertable=false, updatable=false)
    String ooId;

    public void setOoId (String ooId) {
        this.ooId = ooId;
    }

    public void setOoDao (OrganisationalObjectiveDAO ooDao) {
        this.ooDao = ooDao;
    }

    @Override
    public String toString() {
        return "Oir {" +
            "id=" + id +
            ", oo_id=" + (ooDao == null ? "null" : ooDao.getId()) +
            ", oir=" + oir +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OirDAO)) return false;
        OirDAO oirDAO = (OirDAO) o;
        return Objects.equals(id, oirDAO.id) &&
            Objects.equals(ooId, oirDAO.ooId) &&
            Objects.equals(oir, oirDAO.oir);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ooId, oir);
    }
}
