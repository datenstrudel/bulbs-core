package net.datenstrudel.bulbs.core.domain.model.scheduling;

import net.datenstrudel.bulbs.core.AbstractBulbsIT;
import net.datenstrudel.bulbs.core.application.services.ScheduledActuationServiceImpl;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetActuatorCommand;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.PointInTimeTrigger;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.Trigger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;

import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;


/**
 * @author Thomas Wendzinski
 */
@DirtiesContext
public class ScheduledActuationIT extends AbstractBulbsIT {

    public static final ZoneId TZ_ID = ZoneId.of("Europe/Berlin");

    @MockBean
    private ScheduledActuationServiceImpl mk_actService;

    @Autowired
    private JobCoordinator jobCoordinator;

    @Before
    public void setUp() {
        reset(mk_actService);
    }

    @Test
    public void activate_ShouldScheduleAndTrigger() throws Exception {
        ScheduledActuation instance = givenScheduledActuationWithTrigger();
        instance.activate(jobCoordinator);
        Thread.sleep(1100l);
        verify(mk_actService).execute(instance.getId().getCreator(), instance.getId());
    }

    @Test
    public void testDeActivate() throws Exception {
        ScheduledActuation instance = givenScheduledActuationWithTrigger();

        instance.activate(jobCoordinator);
        instance.deActivate(jobCoordinator);

        Thread.sleep(1100l);
        verify(mk_actService, never()).execute(any(BulbsContextUserId.class), any(ScheduledActuationId.class));
    }

    private ScheduledActuation givenScheduledActuationWithTrigger() {
        ScheduledActuation res = new ScheduledActuation(
                new ScheduledActuationId("testActivate_schedActId",
                        new BulbsContextUserId("testActivate_useruuid")),
                "testActivate_schedActName", true);

        res.setNewStates(Collections.singletonList(new PresetActuatorCommand(null, "test_apiKex", null, null, true)),
                jobCoordinator);

        Date triggerTime = new Date(new Date().getTime() + 1000l);
        Trigger trigger = new PointInTimeTrigger(triggerTime, TZ_ID.toString());

        res.defineTrigger(trigger, jobCoordinator);
        return res;
    }

}
