package femr.data.daos.core;

import femr.data.models.core.IKitIdentity;

public interface IKitIdentityRepository {

    IKitIdentity retrieve();

    IKitIdentity create(IKitIdentity kitIdentity);
}
