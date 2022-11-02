package com.costain.cdbb.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class DeletedOirPk implements Serializable {

    private static final long serialVersionUID = -1;

    protected String oirId;
    protected String projectId;

    public DeletedOirPk() {}

    public DeletedOirPk(String oirId, String projectId) {
        this.projectId = projectId;
        this.oirId = oirId;
    }

    @Override
    public String toString() {
        return "Deleted Oir Primary Key {" +
            "oirId=" + oirId.toString() +
            ", projectId=" + projectId.toString() +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeletedOirPk deletedOirPk)) return false;
        return Objects.equals(oirId.toString(), deletedOirPk.oirId.toString()) &&
            Objects.equals(projectId.toString(), deletedOirPk.projectId.toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(oirId.toString(), projectId.toString());
    }
}
