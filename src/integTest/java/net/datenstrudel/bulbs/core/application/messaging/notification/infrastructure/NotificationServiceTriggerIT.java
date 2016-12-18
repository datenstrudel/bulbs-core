package net.datenstrudel.bulbs.core.application.messaging.notification.infrastructure;

import net.datenstrudel.bulbs.core.AbstractBulbsWebIT;
import net.datenstrudel.bulbs.core.application.messaging.notification.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * @author Thomas Wendzinski
 */
public class NotificationServiceTriggerIT extends AbstractBulbsWebIT {

    @Autowired
    private NotificationServiceTrigger instance;

    private NotificationService mk_notificationService;

    @Before
    public void setUp() {
        mk_notificationService = createMock(NotificationService.class);
        mk_notificationService.publishNotifications();
        expectLastCall().anyTimes();
        replay(mk_notificationService);

        ReflectionTestUtils.invokeSetterMethod(instance, "setNfService_coreInternal", mk_notificationService);
    }

    @Test
    public void testTrigger_coreInternal() {
        try {
            Thread.sleep(2000l);
        } catch (InterruptedException ex) {
        }
        verify(mk_notificationService);
    }
}