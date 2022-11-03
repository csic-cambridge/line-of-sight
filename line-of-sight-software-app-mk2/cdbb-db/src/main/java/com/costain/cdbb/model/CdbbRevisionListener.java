package com.costain.cdbb.model;

import org.hibernate.envers.RevisionListener;
import org.jboss.logging.NDC;

public class CdbbRevisionListener implements RevisionListener {
    public void newRevision(Object revisionEntity) {
        CdbbRevEntity cdbbRevEntity = (CdbbRevEntity) revisionEntity;
        cdbbRevEntity.setUserId(this.getSavedUserId());
    }

    private String getSavedUserId() {
        return NDC.peek();
    }
}
