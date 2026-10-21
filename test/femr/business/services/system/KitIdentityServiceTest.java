package femr.business.services.system;

import femr.data.daos.core.IKitIdentityRepository;
import femr.data.models.core.IKitIdentity;
import femr.data.models.mysql.KitIdentity;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

public class KitIdentityServiceTest {

    IKitIdentityRepository kitIdentityRepository;
    KitIdentityService kitIdentityService;

    @Before
    public void setUp() {
        kitIdentityRepository = mock(IKitIdentityRepository.class);
        kitIdentityService = new KitIdentityService(kitIdentityRepository);
    }

    @Test
    public void generatesAndPersistsUuidWhenNoIdentityExists() {
        Assume.assumeTrue(isKitIdEnvironmentUnset());
        when(kitIdentityRepository.retrieve()).thenReturn(null);
        doAnswer(invocation -> invocation.getArgument(0))
                .when(kitIdentityRepository).create(any(IKitIdentity.class));

        IKitIdentity identity = kitIdentityService.getIdentity();

        Assert.assertNotNull(identity);
        Assert.assertEquals(1, identity.getSingletonId());
        Assert.assertEquals("GENERATED", identity.getSource());
        Assert.assertNotNull(identity.getCreatedAt());
        Assert.assertNotNull(identity.getUpdatedAt());
        assertUuid(identity.getKitId());
        verify(kitIdentityRepository).retrieve();
        verify(kitIdentityRepository).create(identity);
    }

    @Test
    public void returnsPersistedIdentityWithoutCreatingAnotherIdentity() {
        IKitIdentity persistedIdentity = identity("5be2ed42-77f1-4c4f-9e2b-2c9ce8c6e3a1", "GENERATED");
        when(kitIdentityRepository.retrieve()).thenReturn(persistedIdentity);

        IKitIdentity result = kitIdentityService.getIdentity();

        Assert.assertSame(persistedIdentity, result);
        Assert.assertEquals(persistedIdentity.getKitId(), kitIdentityService.getKitId());
        verify(kitIdentityRepository).retrieve();
        verify(kitIdentityRepository, never()).create(any(IKitIdentity.class));
    }

    @Test
    public void cachesIdentityAfterFirstLookup() {
        IKitIdentity persistedIdentity = identity("5be2ed42-77f1-4c4f-9e2b-2c9ce8c6e3a1", "ENVIRONMENT");
        when(kitIdentityRepository.retrieve()).thenReturn(persistedIdentity);

        IKitIdentity firstResult = kitIdentityService.getIdentity();
        IKitIdentity secondResult = kitIdentityService.getIdentity();

        Assert.assertSame(firstResult, secondResult);
        verify(kitIdentityRepository).retrieve();
    }

    @Test
    public void rejectsPersistedIdentityWithInvalidUuid() {
        IKitIdentity persistedIdentity = identity("not-a-uuid", "GENERATED");
        when(kitIdentityRepository.retrieve()).thenReturn(persistedIdentity);

        try {
            kitIdentityService.getIdentity();
            Assert.fail("Expected an invalid persisted UUID to be rejected");
        } catch (IllegalStateException exception) {
            Assert.assertEquals("KIT_ID must be a valid UUID", exception.getMessage());
        }
    }

    @Test
    public void usesIdentityCreatedByAnotherInstanceWhenCreateLosesRace() {
        IKitIdentity persistedAfterRace = identity("5be2ed42-77f1-4c4f-9e2b-2c9ce8c6e3a1", "GENERATED");
        when(kitIdentityRepository.retrieve()).thenReturn(null, persistedAfterRace);
        doThrow(new RuntimeException("duplicate singleton identity"))
                .when(kitIdentityRepository).create(any(IKitIdentity.class));

        IKitIdentity result = kitIdentityService.getIdentity();

        Assert.assertSame(persistedAfterRace, result);
        Assert.assertEquals(persistedAfterRace.getKitId(), kitIdentityService.getKitId());
        verify(kitIdentityRepository).create(any(IKitIdentity.class));
        verify(kitIdentityRepository, org.mockito.Mockito.times(2)).retrieve();
    }

    @Test
    public void usesConfiguredKitIdWhenEnvironmentProvidesValidUuid() {
        String configuredKitId = System.getenv("KIT_ID");
        Assume.assumeTrue(configuredKitId != null && !configuredKitId.trim().isEmpty());
        Assume.assumeTrue(isValidUuid(configuredKitId.trim()));
        when(kitIdentityRepository.retrieve()).thenReturn(null);
        doAnswer(invocation -> invocation.getArgument(0))
                .when(kitIdentityRepository).create(any(IKitIdentity.class));

        IKitIdentity identity = kitIdentityService.getIdentity();

        Assert.assertEquals(configuredKitId.trim().toLowerCase(), identity.getKitId());
        Assert.assertEquals("ENVIRONMENT", identity.getSource());
    }

    private IKitIdentity identity(String kitId, String source) {
        KitIdentity identity = new KitIdentity();
        identity.setSingletonId(1);
        identity.setKitId(kitId);
        identity.setSource(source);
        return identity;
    }

    private void assertUuid(String value) {
        Assert.assertNotNull(value);
        Assert.assertEquals(value, UUID.fromString(value).toString());
    }

    private boolean isValidUuid(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private boolean isKitIdEnvironmentUnset() {
        String configuredKitId = System.getenv("KIT_ID");
        return configuredKitId == null || configuredKitId.trim().isEmpty();
    }
}
