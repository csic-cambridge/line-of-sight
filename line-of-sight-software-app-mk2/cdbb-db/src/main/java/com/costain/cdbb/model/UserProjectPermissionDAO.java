package com.costain.cdbb.model;

import lombok.*;
import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_permission")
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
    private final UserProjectPermissionId id = new UserProjectPermissionId();

    /*
    UserDAO property
    */
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserDAO user;

    /*
    ProjectDAO property
    */
    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private ProjectDAO project;

    /*
    PermissionDAO property
    */
    @ManyToOne
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    private PermissionDAO permission;

    /*
    User string representation
    */
    @Override
    public String toString() {
        return "UserProjectPermission {" +
            "user=" + (this.getUser() == null ? "null" : this.getUser().getId()) +
            ", project=" + (this.getProject() == null ? "null" : this.getProject().getId()) +
            ", permission=" + (getPermission() == null ? "null" : getPermission().getId()) +
            '}';
    }

    /*
    UserDAO equals method
    @param  object to compare
    @return true if two objects are the same user
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProjectPermissionDAO uppDAO)) return false;
        return Objects.equals(this.getUser().getId(), uppDAO.getUser().getId()) &&
            Objects.equals(this.getProject().getId(), uppDAO.getProject().getId()) &&
            Objects.equals(getPermission().getId(), uppDAO.getPermission().getId())
            ;
    }
    /*
    UserDAO hashCode method
    @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.getUser().getId(), this.getProject().getId(), getPermission().getId());
    }
}
