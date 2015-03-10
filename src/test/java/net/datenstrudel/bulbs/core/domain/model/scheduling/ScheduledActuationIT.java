package net.datenstrudel.bulbs.core.domain.model.scheduling;

import junit.framework.Assert;
import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.application.services.ScheduledActuationService;
import net.datenstrudel.bulbs.core.config.BulbsCoreConfig;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetActuatorCommand;
import net.datenstrudel.bulbs.core.infrastructure.services.InfrastructureServicesConfig;
import net.datenstrudel.bulbs.core.testConfigs.ScheduledActuationIntegrationTestConfig;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.PointInTimeTrigger;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.Trigger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Set;

import static org.easymock.EasyMock.*;

/**
 *
 * @author Thomas Wendzinski
 */
@ContextConfiguration(
classes = {
        TestConfig.class,
        BulbsCoreConfig.class,
        InfrastructureServicesConfig.class,
        ScheduledActuationIntegrationTestConfig.class
      }
)
@RunWith(SpringJUnit4ClassRunner.class)
public class ScheduledActuationIT {

    @Autowired
    ScheduledActuationService mk_actService;
    @Autowired 
    JobCoordinator jobCoordinator;
    
    public ScheduledActuationIT() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @Before
    public void setUp() {
        reset(mk_actService);
    }

    @Test
    public void testActivate() throws Exception{
        System.out.println("activate");
        ZoneId tzId = ZoneId.of("Europe/Berlin");
        ScheduledActuation instance = new ScheduledActuation(
                new ScheduledActuationId("testActivate_schedActId", 
                        new BulbsContextUserId("testActivate_useruuid")), 
                "testActivate_schedActName", true);
        instance.setNewStates(new LinkedList<AbstractActuatorCmd>(){{
            add(new PresetActuatorCommand(null, DISABLE_CLASS_MOCKING, null, null, true));
        }}, 
                jobCoordinator);
        Date trigger_ = new Date(new Date().getTime() + 1000l);
        Trigger trigger = new PointInTimeTrigger(trigger_, tzId.toString());
//        Date trigger = Date.from(trigger_.toInstant().atZone(ZoneId.of(instance.getTimezoneId())).toLocalDateTime().toInstant(ZoneOffset.UTC));
        System.out.println("Trigger: " + trigger_);
        instance.defineTrigger(trigger, jobCoordinator);
        mk_actService.execute(instance.getId().getCreator(), instance.getId());
        expectLastCall().once();
        replay(mk_actService);
        instance.activate(jobCoordinator);
        Thread.sleep(1100l);
        verify(mk_actService);
    }
    @Test
    public void testDeActivate() throws Exception{
        System.out.println("activate");
        ZoneId tzId = ZoneId.of("Europe/Berlin");
        ScheduledActuation instance = new ScheduledActuation(
                new ScheduledActuationId("testDeActivate_schedActId", 
                        new BulbsContextUserId("testDeActivate_useruuid")), 
                "testDeActivate_schedActName", true);
        instance.setNewStates(new LinkedList<AbstractActuatorCmd>(){{
            add(new PresetActuatorCommand(null, DISABLE_CLASS_MOCKING, null, null, true));
        }}, 
                jobCoordinator);
        Date trigger_ = new Date(new Date().getTime() + 1000l);
        Trigger trigger = new PointInTimeTrigger(trigger_, tzId.toString());
//        Date trigger = Date.from(trigger_.toInstant().atZone(ZoneId.of(instance.getTimezoneId())).toLocalDateTime().toInstant(ZoneOffset.UTC));
        System.out.println("Trigger: " + trigger_);
        instance.defineTrigger(trigger, jobCoordinator);
        mk_actService.execute(isA(BulbsContextUserId.class), isA(ScheduledActuationId.class));
        
        // TODO: make sure test fails when next call is detected!!
        final  boolean[] testDeActivate_failed = {false};
        
        expectLastCall().andStubDelegateTo(new ScheduledActuationService() {
            @Override
            public void execute(BulbsContextUserId userId, ScheduledActuationId actId) {
                testDeActivate_failed[0] = true;
            }

            
            @Override
            public Set<ScheduledActuation> loadAllByUser(BulbsContextUserId userId) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public void remove(BulbsContextUserId userId, ScheduledActuationId actId) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public void activate(BulbsContextUserId userId, ScheduledActuationId actId) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public void deactivate(BulbsContextUserId userId, ScheduledActuationId actId) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public void modifyName(BulbsContextUserId userId, ScheduledActuationId actId, String newName) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public void modifyStates(BulbsContextUserId userId, ScheduledActuationId actId, Collection<AbstractActuatorCmd> newStates) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public ScheduledActuation loadById(BulbsContextUserId userId, ScheduledActuationId id) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public ScheduledActuation create(BulbsContextUserId userId, String name, boolean removeAfterExecution) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public ScheduledActuation create(BulbsContextUserId userId, String name, Trigger trigger, boolean removeAfterExecution) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            @Override
            public ScheduledActuation createAndActivate(BulbsContextUserId userId, String name, Trigger trigger, boolean removeAfterExecution, Collection<AbstractActuatorCmd> states) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        replay(mk_actService);
        instance.activate(jobCoordinator);
        instance.deActivate(jobCoordinator);
        Thread.sleep(1100l);
        verify(mk_actService);
        Assert.assertTrue( !testDeActivate_failed[0]);
    }

    
}
