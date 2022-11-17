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
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Audited
@Table(name = "user_permission")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
/*
Handles the UserPermission database entity
 */
public class UserPermissionDAO {

    @EmbeddedId
    @Builder.Default
    private UserAndPermissionId id = new UserAndPermissionId();

    /*
    User string representation
    */
    @Override
    public String toString() {
        return "UserPermission {" +
            "id = " + id.toString() +
            '}';
    }

    /*
    UserPermissionsDAO equals method
    @param  object to compare
    @return true if two objects are the same permissions user
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPermissionDAO uppDAO)) return false;
        return Objects.equals(id, uppDAO.id);
    }
    /*
    UserDAO hashCode method
    @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
