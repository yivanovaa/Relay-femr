package femr.business.services.system;

import com.google.inject.Inject;
import femr.business.services.core.IKitIdentityService;
import femr.data.daos.core.IKitIdentityRepository;
import femr.data.models.core.IKitIdentity;
import femr.data.models.mysql.KitIdentity;
import org.joda.time.DateTime;
import play.Logger;

import java.util.UUID;

public class KitIdentityService implements IKitIdentityService {

    private static final int SINGLETON_ID = 1;
    private static final String KIT_ID_ENV = "KIT_ID";
    private static final String SOURCE_ENVIRONMENT = "ENVIRONMENT";
    private static final String SOURCE_GENERATED = "GENERATED";

    private final IKitIdentityRepository kitIdentityRepository;
    private volatile IKitIdentity cachedIdentity;

    @Inject
    public KitIdentityService(IKitIdentityRepository kitIdentityRepository) {
        this.kitIdentityRepository = kitIdentityRepository;
    }

    @Override
    public IKitIdentity getIdentity() {
        IKitIdentity identity = cachedIdentity;
        if (identity != null) {
            return identity;
        }

        synchronized (this) {
            if (cachedIdentity != null) {
                return cachedIdentity;
            }

            IKitIdentity persistedIdentity = kitIdentityRepository.retrieve();
            if (persistedIdentity != null) {
                validateUuid(persistedIdentity.getKitId());
                cachedIdentity = persistedIdentity;
                return persistedIdentity;
            }

            String configuredKitId = System.getenv(KIT_ID_ENV);
            String kitId;
            String source;

            if (configuredKitId != null && !configuredKitId.trim().isEmpty()) {
                kitId = validateUuid(configuredKitId.trim());
                source = SOURCE_ENVIRONMENT;
            } else {
                kitId = UUID.randomUUID().toString();
                source = SOURCE_GENERATED;
            }

            DateTime now = DateTime.now();
            KitIdentity newIdentity = new KitIdentity();
            newIdentity.setSingletonId(SINGLETON_ID);
            newIdentity.setKitId(kitId);
            newIdentity.setSource(source);
            newIdentity.setCreatedAt(now);
            newIdentity.setUpdatedAt(now);

            try {
                cachedIdentity = kitIdentityRepository.create(newIdentity);
                Logger.info("KitIdentityService", "Initialized kit identity from " + source);
                return cachedIdentity;
            } catch (RuntimeException ex) {
                // Another application instance may have initialized the singleton first.
                IKitIdentity identityAfterRace = kitIdentityRepository.retrieve();
                if (identityAfterRace != null) {
                    validateUuid(identityAfterRace.getKitId());
                    cachedIdentity = identityAfterRace;
                    return identityAfterRace;
                }
                throw ex;
            }
        }
    }

    @Override
    public String getKitId() {
        return getIdentity().getKitId();
    }

    private String validateUuid(String value) {
        try {
            return UUID.fromString(value).toString();
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("KIT_ID must be a valid UUID", ex);
        }
    }
}
