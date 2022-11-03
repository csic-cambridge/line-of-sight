package com.costain.cdbb.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
@Embeddable
@NoArgsConstructor
/*
Provides an embeddable id for UserProjectPermissionDAO
consisting of user, project and ProjectPermission objects
 */
public class UserProjectPermissionId implements Serializable {
    private static final long serialVersionUID = 1L;

    public UserProjectPermissionId (UUID userId, UUID projectId, Integer permissionId) {
        this.userId = userId;
        this.projectId = projectId;
        this.permissionId = permissionId;
    }
    @Column(name="user_id")
    @Type(type = "uuid-char")
    private UUID userId;

    @Column(name="project_id")
    @Type(type = "uuid-char")
    private UUID projectId;

    @Column(name="project_permission_id")
    private Integer permissionId;

    /*
    User string representation
    */
    @Override
    public String toString() {
        return "UserProjectPermissionId {" +
            "userId=" + userId +
            ", projectId=" +projectId +
            ", permissionId=" + permissionId +
            '}';
    }

    /*
    equals method
    @param o    object to compare against
    @return     true if both objects equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProjectPermissionId that = (UserProjectPermissionId) o;
        return userId.equals(that.userId) &&
               projectId.equals(that.projectId) &&
               permissionId.equals(that.permissionId)
        ;
    }

    /*
    hashcode method
    @returns hashcode of object
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, projectId, permissionId);
    }
}
