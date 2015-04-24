package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.identity.*;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.inOrder;

@RunWith(MockitoJUnitRunner.class)
public class BulbBridgeDomainServiceImplTest {

    @InjectMocks
    BulbBridgeDomainServiceImpl instance;

    @Mock
    BulbsHwService bulbsHwService_mk;

    @Test
    public void removeBulbsPrincipals_success() throws Exception {
        BulbBridgeAddress address = new BulbBridgeAddress("testHost", 80);
        BulbsPrincipal principal1 = new BulbsPrincipal("test_userName1", AppIdCore.instance(), "test_bbId1", BulbsPrincipalState.REGISTERED);
        BulbsPrincipal principal2 = new BulbsPrincipal("test_userName2", AppIdCore.instance(), "test_bbId1", BulbsPrincipalState.REGISTERED);
        BulbsPrincipal principal3 = new BulbsPrincipal("test_userName3", AppIdCore.instance(), "test_bbId1", BulbsPrincipalState.REGISTERED);
        BulbsContextUser user = new BulbsContextUser(new BulbsContextUserId("test_useruuid"), "test_email", "", "test_nick", "test_ApiKey");
        DomainEventPublisher.instance().reset();
        instance.removeBulbsPrincipalsAfterDeletion(
                principal1,
                new BulbsPrincipal[]{principal1, principal2, principal3},
                address,
                BulbsPlatform._EMULATED,
                user
        );
        InOrder order = inOrder(bulbsHwService_mk);
        order.verify(bulbsHwService_mk).removeBulbsPrincipal(address, principal1, principal2, BulbsPlatform._EMULATED);
        order.verify(bulbsHwService_mk).removeBulbsPrincipal(address, principal1, principal3, BulbsPlatform._EMULATED);
        order.verify(bulbsHwService_mk).removeBulbsPrincipal(address, principal1, principal1, BulbsPlatform._EMULATED);
    }
}