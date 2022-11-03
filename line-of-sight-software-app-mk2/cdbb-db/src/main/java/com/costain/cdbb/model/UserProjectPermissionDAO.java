package com.costain.cdbb.model;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Audited
@Table(name = "user_project_permission")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
/*
Handles the User(Project)Permission database entity
 */
public class UserProjectPermissionDAO {

    @EmbeddedId
    @Builder.Default
    private UserProjectPermissionId id = new UserProjectPermissionId();

    /*
    User string representation
    */
    @Override
    public String toString() {
        return "UserProjectPermission {" +
            "id = " + id.toString() +
            '}';
    }

    /*
    UserProjectPermissionsDAO equals method
    @param  object to compare
    @return true if two objects are the same permissions user/project
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProjectPermissionDAO uppDAO)) return false;
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
