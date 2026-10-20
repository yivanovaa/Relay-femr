package femr.data.models.mysql;

import femr.data.models.core.IKitIdentity;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "kit_identity")
public class KitIdentity implements IKitIdentity {

    @Id
    @Column(name = "singleton_id", nullable = false)
    private int singletonId;

    @Column(name = "kit_id", unique = true, nullable = false, length = 36)
    private String kitId;

    @Column(name = "legacy_id", length = 255)
    private String legacyId;

    @Column(name = "source", nullable = false, length = 32)
    private String source;

    @Column(name = "created_at", nullable = false)
    private DateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private DateTime updatedAt;

    @Override
    public int getSingletonId() {
        return singletonId;
    }

    @Override
    public void setSingletonId(int singletonId) {
        this.singletonId = singletonId;
    }

    @Override
    public String getKitId() {
        return kitId;
    }

    @Override
    public void setKitId(String kitId) {
        this.kitId = kitId;
    }

    @Override
    public String getLegacyId() {
        return legacyId;
    }

    @Override
    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public DateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public DateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public void setUpdatedAt(DateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
