package femr.business.services.core;

import femr.data.models.core.IKitIdentity;

public interface IKitIdentityService {

    IKitIdentity getIdentity();

    String getKitId();
}
