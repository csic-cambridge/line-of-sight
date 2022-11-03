package com.costain.cdbb.model;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Data
@Embeddable
@NoArgsConstructor
/*
Provides an embeddable id for UserPermissionDAO
consisting of user and Permission objects
 */
public class UserPermissionId implements Serializable {
    private static final long serialVersionUID = 1L;

    public UserPermissionId(UUID userId, Integer permissionId) {
        this.userId = userId;
        this.permissionId = permissionId;
    }
    @Column(name="user_id")
    @Type(type = "uuid-char")
    private UUID userId;

    @Column(name="permission_id")
    private Integer permissionId;

    /*
    User string representation
    */
    @Override
    public String toString() {
        return "UserPermissionId {" +
            "userId=" + userId +
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
        UserPermissionId that = (UserPermissionId) o;
        return userId.equals(that.userId) &&
               permissionId.equals(that.permissionId)
        ;
    }

    /*
    hashcode method
    @returns hashcode of object
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, permissionId);
    }
}
