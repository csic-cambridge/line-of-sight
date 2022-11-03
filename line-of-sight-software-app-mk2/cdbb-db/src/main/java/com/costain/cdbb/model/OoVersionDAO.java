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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Audited
@Table(name = "oo_version")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
/*
Handles the OOVersion database entity
 */
public class OoVersionDAO {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    /*
    name property
     */
    @Column(nullable = false)
    private String name;

    /*
    dateCreated property
     */
    @Column(insertable = false, updatable = false)
    private Timestamp dateCreated;

    @ManyToOne()
    @JoinColumn(name = "oo_id", referencedColumnName = "id")
    private OrganisationalObjectiveDAO oo;

    @OneToMany(mappedBy = "ooVersion", cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    private List<ProjectOrganisationalObjectiveDAO> poos;

    /*
    OOVersion string representation
    */
    @Override
    public String toString() {
        return "OOVersion {" +
            "id=" + id +
            ", ooId = " + (oo == null ? "null" : oo.getId())  +
            ", name = " + name +
            ", dateCreated=" + dateCreated +
            '}';
    }

    /*
    OOVersion equals method
    @param  object to compare
    @return true if two objects are the same OOVersion
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OoVersionDAO ooVersionDAO)) return false;
        return Objects.equals(id, ooVersionDAO.id) &&
            Objects.equals(oo, ooVersionDAO.getOo()) &&
            Objects.equals(dateCreated, ooVersionDAO.dateCreated)
            ;
    }
    /*
    OOVersion hashCode method
    @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, oo, dateCreated);
    }
}
