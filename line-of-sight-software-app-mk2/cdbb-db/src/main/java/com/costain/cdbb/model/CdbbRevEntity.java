package com.costain.cdbb.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.DefaultRevisionEntity;

import java.io.Serial;
import java.util.Objects;


@Entity
@RevisionEntity(CdbbRevisionListener.class)
@Table(name = "revinfo")
public class CdbbRevEntity extends DefaultRevisionEntity {
    @Serial
    private static final long serialVersionUID = 1;

    private String userId;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && this.userId.equals(((CdbbRevEntity)o).userId);
    }

    public int hashCode() {
        return super.hashCode() + Objects.hash(this.userId);
    }

    public String toString() {
        return "CdbbRevEntity:" + super.toString() + ", userId=" + this.userId;
    }
}
