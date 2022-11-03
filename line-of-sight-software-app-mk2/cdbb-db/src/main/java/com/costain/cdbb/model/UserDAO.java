package com.costain.cdbb.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user")
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
/*
Handles the User database entity
 */
public class UserDAO {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID userId;

    /*
    emailAddress property - must be unique
     */
    @Column(nullable = false)
    private String emailAddress;

    /*
    isSuperUser property - super user has all permissions
     */
    @Column(nullable = false)
    private boolean isSuperUser;

    public void setIsSuperUser(boolean isSuperUser) {
        this.isSuperUser = isSuperUser;
    }

    //@OneToMany(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    //private Set<UserProjectPermissionDAO> userProjectPermissionDAOs;

    /*
    User string representation
    */
    @Override
    public String toString() {
        return "User {" +
            "id=" + userId +
            ", emailAddress=" + emailAddress +
            ", isSuperUser=" + isSuperUser +
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
        if (!(o instanceof UserDAO userDAO)) return false;
        return Objects.equals(userId, userDAO.userId) &&
            Objects.equals(emailAddress, userDAO.getEmailAddress()) &&
            Objects.equals(isSuperUser, userDAO.isSuperUser)
            ;
    }
    /*
    UserDAO hashCode method
    @return hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, emailAddress, isSuperUser);
    }
}
