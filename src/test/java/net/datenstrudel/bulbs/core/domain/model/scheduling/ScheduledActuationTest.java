/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.datenstrudel.bulbs.core.domain.model.scheduling;

import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.infrastructure.services.scheduling.ScheduledJobExecutor;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.PointInTimeTrigger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quartz.CronExpression;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;


/**
 *
 * @author Thomas Wendzinski
 */
public class ScheduledActuationTest {
    
    JobCoordinator mk_jobCoordinator;
            
    public ScheduledActuationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    @Before
    public void setUp() {
        mk_jobCoordinator = createNiceMock(JobCoordinator.class);
    }

    @Test
    public void testActivate() {
        System.out.println("activate");
        final ScheduledActuation instance = newScheduledActuation();
        Set<AbstractActuatorCmd> states = new HashSet<AbstractActuatorCmd>(){{
            add(new BulbActuatorCommand(null, null, DISABLE_CLASS_MOCKING, null, null));
        }};
        instance.setNewStates(states, mk_jobCoordinator);
        
        resetToNice(mk_jobCoordinator);
        // Am I already activated?
        expect(mk_jobCoordinator.isScheduled(instance.getScheduleId().getUuid()))
                .andReturn(Boolean.TRUE);
        mk_jobCoordinator.unSchedule(instance.getScheduleId().getUuid());
        expectLastCall().once();
        
        mk_jobCoordinator.schedule(
                isA(String.class), isA(CronExpression.class), isA(ScheduledJobExecutor.class));
        expectLastCall().andStubDelegateTo(new JobCoordinator() {
            @Override
            public void schedule(String jobId, CronExpression cronTrigger, ScheduledJobExecutor jobExec) {
                assertEquals(instance.getScheduleId().getUuid(), jobId);
                assertEquals(instance.getTrigger().toCronExpression().getCronExpression(), cronTrigger.getCronExpression());
            }
            @Override
            public void unSchedule(String jobId) {
                throw new UnsupportedOperationException("Not supported.");
            }
            @Override
            public boolean isScheduled(String jobId) {
                throw new UnsupportedOperationException("Not supported.");
            }
        });
        
        replay(mk_jobCoordinator);
        instance.activate(mk_jobCoordinator);
        verify(mk_jobCoordinator);
    }
    @Test(expected = IllegalStateException.class)
    public void testActivate_NoStates() {
        System.out.println("activate_BoStates");
        final ScheduledActuation instance = newScheduledActuation();
        instance.activate(mk_jobCoordinator);
    }
    @Test
    public void testDeActivate() {
        System.out.println("deActivate");
        final ScheduledActuation instance = newScheduledActuation();
        Set<AbstractActuatorCmd> states = new HashSet<AbstractActuatorCmd>(){{
            add(new BulbActuatorCommand(null, null, DISABLE_CLASS_MOCKING, null, null));
        }};
        instance.setNewStates(states, mk_jobCoordinator);
        reset(mk_jobCoordinator);

        mk_jobCoordinator.unSchedule(instance.getScheduleId().getUuid());
        expectLastCall().once();
        replay(mk_jobCoordinator);

        instance.deActivate(mk_jobCoordinator);
        verify(mk_jobCoordinator);
    }
    
    private ScheduledActuation newScheduledActuation(){
        return new ScheduledActuation(
                new ScheduledActuationId(
                        "testScheduledActuation__UUID", 
                        new BulbsContextUserId("testScheduledActuation__userID")), "testScheduledACtuation__NAME", 
                new PointInTimeTrigger(new Date(new Date().getTime() + 10000l), "UTC"), false);
    }

    
}
