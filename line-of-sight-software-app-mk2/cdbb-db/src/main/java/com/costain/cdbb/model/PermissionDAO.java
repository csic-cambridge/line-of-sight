package com.costain.cdbb.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "permission")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
/*
Handles the Permission database entity
 */
public class PermissionDAO {

    @Id
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "INT")
    private Integer id;

    /*
    permission property - must be unique
     */
    @Column(nullable = false)
    private String permission;

    @OneToMany(mappedBy = "permission", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<UserProjectPermissionDAO> userProjectPermissionDaos;

    /*
    Permission string representation
    */
    @Override
    public String toString() {
        return "Permission {" +
            "id=" + id +
            ", permission=" + permission +
            '}';
    }

    /*
    PermissionDAO equals method
    @param object to compare
    @return true if two objects are the same permission
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PermissionDAO permissionDao)) return false;
        return Objects.equals(id, permissionDao.id) &&
            Objects.equals(permission, permissionDao.getPermission())
            ;
    }
    /*
    PermissionDAO hashCode method
    @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, permission);
    }
}
