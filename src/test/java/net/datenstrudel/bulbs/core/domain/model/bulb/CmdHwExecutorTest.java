/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.application.messaging.BulbsCoreEventProcessor;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.DomainEventStore;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipalState;
import net.datenstrudel.bulbs.core.domain.model.infrastructure.DomainServiceLocator;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisherDeferrer;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.BulbBridgeHardwareAdapter;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 *
 * @author Thomas Wendzinski
 */
@RunWith(MockitoJUnitRunner.class)
public class CmdHwExecutorTest {

    @Mock
    BulbBridgeHardwareAdapter mk_hwAdapter;
    @Mock
    DomainEventStore mk_eventStore;
    BulbBridgeAddress T_BRIDGE_ADDRESS = new BulbBridgeAddress("localhost", 0);
    BulbsPlatform mk_BulbsPlatform = BulbsPlatform._EMULATED;

    @Mock
    DomainServiceLocator mk_domainServiceLocator;

    DomainServiceLocator serviceLocator;
    
    public CmdHwExecutorTest() {
    }
    
    @Before
    public void setUp() {
//        mk_hwAdapter = createMock(BulbBridgeHardwareAdapter.class);
//        mk_eventStore = createMock(DomainEventStore.class);
        ReflectionTestUtils.setField(new BulbsCoreEventProcessor(), "eventStore", 
                mk_eventStore);

        serviceLocator = new DomainServiceLocator();
        ReflectionTestUtils.setField(serviceLocator, "instance", mk_domainServiceLocator);

    }
    @After
    public void tearDown(){
        ReflectionTestUtils.setField(serviceLocator, "instance", null);
    }

//    @Test
    public void testCancelExecution() {
        System.out.println("cancelExecution");
        CmdHwExecutor instance = null;
        instance.cancelExecution();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

//    @Test
    public void testIsExecutionFinished() {
        System.out.println("isExecutionFinished");
        CmdHwExecutor instance = null;
        boolean expResult = false;
        boolean result = instance.isExecutionFinished();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testRun() throws Exception{
        System.out.println("run");
        CmdHwExecutor instance;

        final int COUNT_STATES = 50;
        BulbActuatorCommand command = new BulbActuatorCommand(
                new BulbId(new BulbBridgeId("brId"), 1),
                new AppId("testApp"), "testUserApiKey", CommandPriority.standard(),
                new LinkedList<BulbState>(){{
                    for (int i = 0; i < COUNT_STATES; i++) {
                        add(new BulbState(new ColorRGB( 
                                (255/COUNT_STATES)*i, (255/COUNT_STATES)*i, (255/COUNT_STATES)*i), true));
                    }
                }}, 
                false
        );
        BulbsPrincipal principal = new BulbsPrincipal(
                "testPrincipalUsernam", new AppId("testCore"), "brId", 
                BulbsPrincipalState.REGISTERED);

        BulbState prevState = new BulbState(false);
        instance = new CmdHwExecutor(T_BRIDGE_ADDRESS, principal, command, prevState, mk_hwAdapter, mk_BulbsPlatform);

        when(mk_domainServiceLocator.getBeanInternal(DomainEventStore.class)).thenReturn(mk_eventStore);
        when(mk_domainServiceLocator.getBeanInternal(DomainEventPublisherDeferrer.class)).thenReturn(null);
        when(mk_eventStore.store(any(DomainEvent.class))).thenReturn(-1l);


//        replay(mk_hwAdapter, mk_eventStore, mk_domainServiceLocator);

        instance.run();
        verify(mk_hwAdapter, atMost(1)).applyBulbState(
                any(BulbId.class), eq(T_BRIDGE_ADDRESS),
                eq(principal),
                any(BulbState.class),
                any(BulbsPlatform.class),
                eq(prevState)
        );
        verify(mk_hwAdapter, atLeast(COUNT_STATES)).applyBulbState(
                any(BulbId.class), eq(T_BRIDGE_ADDRESS),
                eq(principal),
                any(BulbState.class),
                any(BulbsPlatform.class),
                any(BulbState.class)
        );
    }
    public void testRun_WithLoop() throws Exception{
        //TODO: Implement Me!
    }
    
}
