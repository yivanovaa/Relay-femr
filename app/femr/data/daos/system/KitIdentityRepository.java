package femr.data.daos.system;

import com.google.inject.Inject;
import femr.data.daos.core.IKitIdentityRepository;
import femr.data.models.core.IKitIdentity;
import femr.data.models.mysql.KitIdentity;
import io.ebean.Ebean;
import play.Logger;

public class KitIdentityRepository implements IKitIdentityRepository {

    @Inject
    public KitIdentityRepository() {
    }

    @Override
    public IKitIdentity retrieve() {
        try {
            return Ebean.find(KitIdentity.class)
                    .where()
                    .eq("singletonId", 1)
                    .findOne();
        } catch (Exception ex) {
            Logger.error("KitIdentityRepository-retrieve", ex);
            throw ex;
        }
    }

    @Override
    public IKitIdentity create(IKitIdentity kitIdentity) {
        try {
            Ebean.save(kitIdentity);
            return kitIdentity;
        } catch (Exception ex) {
            Logger.error("KitIdentityRepository-create", ex);
            throw ex;
        }
    }
}
