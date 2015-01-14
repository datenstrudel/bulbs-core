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
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.BulbCmdTranslator_HTTP;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbBridgeAddress;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbsPlatform;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedList;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 * @author Thomas Wendzinski
 */
public class CmdHwExecutorTest {
    
    BulbBridgeHardwareAdapter mk_hwAdapter;
    DomainEventStore mk_eventStore;
    BulbBridgeAddress T_BRIDGE_ADDRESS = new BulbBridgeAddress("localhost", 0);
    BulbsPlatform mk_BulbsPlatform = BulbsPlatform._EMULATED;
    DomainServiceLocator mk_domainServiceLocator;
    DomainServiceLocator serviceLocator;
    
    public CmdHwExecutorTest() {
    }
    
    @Before
    public void setUp() {
        mk_hwAdapter = createMock(BulbBridgeHardwareAdapter.class);
        mk_eventStore = createMock(DomainEventStore.class);
        ReflectionTestUtils.setField(new BulbsCoreEventProcessor(), "eventStore", 
                mk_eventStore);
        mk_domainServiceLocator = createMock(DomainServiceLocator.class);

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

        instance = new CmdHwExecutor(T_BRIDGE_ADDRESS, principal, command, mk_hwAdapter, mk_BulbsPlatform);

        expect(mk_domainServiceLocator.getBeanInternal(DomainEventStore.class)).andReturn(mk_eventStore).anyTimes();
        expect(mk_domainServiceLocator.getBeanInternal(DomainEventPublisherDeferrer.class)).andReturn(null).anyTimes();
        expect(mk_eventStore.store(isA(DomainEvent.class))).andReturn(-1l).anyTimes();
        expect(mk_hwAdapter.applyBulbState(
                isA(BulbId.class), eq(T_BRIDGE_ADDRESS),
                eq(principal),
                isA(BulbState.class),
                isA(BulbsPlatform.class))).andReturn(
                new InvocationResponse("OK", false))
                .times(COUNT_STATES);
        replay(mk_hwAdapter, mk_eventStore, mk_domainServiceLocator);
        
        instance.run();
        verify(mk_hwAdapter);
    }
    public void testRun_WithLoop() throws Exception{
        //TODO: Implement Me!
    }
    
}
