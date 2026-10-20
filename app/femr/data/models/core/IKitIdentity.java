package femr.data.models.core;

import org.joda.time.DateTime;

/**
 * The persisted identity of this FEMR installation.
 */
public interface IKitIdentity {

    int getSingletonId();

    void setSingletonId(int singletonId);

    String getKitId();

    void setKitId(String kitId);

    String getLegacyId();

    void setLegacyId(String legacyId);

    String getSource();

    void setSource(String source);

    DateTime getCreatedAt();

    void setCreatedAt(DateTime createdAt);

    DateTime getUpdatedAt();

    void setUpdatedAt(DateTime updatedAt);
}
