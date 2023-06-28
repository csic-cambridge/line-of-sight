package com.costain.cdbb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;



@Entity
@Audited
@Table(name = "airs")
@Getter
@Setter//(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AirsDAO {
    public final static String NEW_ID = ""; // used in api to indicate a new Airs is to be created
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;  // NOTE: builder Setter uses String parameter(see below)

    @ManyToOne
    @JoinColumn(name = "asset_id", referencedColumnName = "id")
    private AssetDAO assetDao;

    @Column(name="airs")
    String airs;

    public void setAssetDao (AssetDAO assetDao) {
        this.assetDao = assetDao;
    }

    @ManyToMany (mappedBy = "airs")
    Set<OirDAO> oirs;

    public static boolean isNewAirsDao(String id) {
        return id != NEW_ID;
    }
    @Override
    public String toString() {
        return "airs {" +
            "id=" + id +
            ", asset_id=" + (assetDao == null ? "null" : assetDao.getId()) +
            ", airs=" + airs +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AirsDAO)) return false;
        AirsDAO airsDAO = (AirsDAO) o;
        return Objects.equals(id, airsDAO.id) &&
            ((assetDao == null && airsDAO.getAssetDao() == null) ||
                Objects.equals(assetDao.getId(), airsDAO.getAssetDao().getId())) &&
            //Objects.equals(asset_id, airsDAO.asset_id) &&
            Objects.equals(airs, airsDAO.airs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, (assetDao == null ? "null" : assetDao.getId()) , airs);
    }

    public static class AirsDAOBuilder {
        public AirsDAOBuilder id (String id) {
            this.id = id.equals(NEW_ID) ? null : UUID.fromString(id);
            return this;
        }
    }
}
