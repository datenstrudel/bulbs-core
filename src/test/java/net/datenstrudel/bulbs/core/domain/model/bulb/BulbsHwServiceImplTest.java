package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.application.messaging.BulbsCoreEventProcessor;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.DomainEventStore;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipalState;
import net.datenstrudel.bulbs.core.domain.model.infrastructure.DomainServiceLocator;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisherDeferrer;
import net.datenstrudel.bulbs.core.infrastructure.services.hardwareadapter.bulb.BulbBridgeHardwareAdapter;
import net.datenstrudel.bulbs.shared.domain.model.bulb.*;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 *
 * @author Thomas Wendzinski
 */
@RunWith(MockitoJUnitRunner.class)
public class BulbsHwServiceImplTest {
    
    //~ Member(s) //////////////////////////////////////////////////////////////
    boolean initialized = false;
    BulbsHwServiceImpl instance2Test = new BulbsHwServiceImpl();

    @Mock
    BulbBridgeHardwareAdapter mk_hwAdapter;

    @Mock
    DomainEventStore mk_eventStore;

    BulbBridgeAddress T_BRIDGE_ADDRESS = new BulbBridgeAddress("localhost", 0);

    DomainServiceLocator serviceLocator;
    @Mock
    DomainServiceLocator mk_domainServiceLocator;
    
    public BulbsHwServiceImplTest() {
    }

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(
                instance2Test, 
                "hwAdapter_Rest", 
                mk_hwAdapter); 
        
        //~ INIT ONCE ~~~~~~~~~~~~~~
        if(initialized)return ;
        initialized = true;
        ReflectionTestUtils.setField(
                instance2Test, 
                "asyncExecutor", 
                new SimpleAsyncTaskExecutor("testBulbsHwService_")); 

        ReflectionTestUtils.setField(new BulbsCoreEventProcessor(), "eventStore", 
                mk_eventStore);
        serviceLocator = new DomainServiceLocator();
        ReflectionTestUtils.setField(serviceLocator, "instance", mk_domainServiceLocator);
    }
    @After
    public void tearDown() {
        ReflectionTestUtils.setField(serviceLocator, "instance", null);
    }

    //@Test
    public void testBridgeFromHwInterface() throws Exception {
        System.out.println("bridgeFromHwInterface");
        BulbBridgeAddress address = null;
        BulbsPrincipal principal = null;
        BulbsContextUserId contextUserId = null;
        BulbsPlatform platform = null;
        BulbsHwServiceImpl instance = new BulbsHwServiceImpl();
        BulbBridge expResult = null;
        BulbBridge result = instance.bridgeFromHwInterface(address, 
                new BulbBridgeId(UUID.randomUUID().toString()), principal, contextUserId, platform);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    //@Test
    public void testCreateBulbsPrincipal() throws Exception {
        System.out.println("createBulbsPrincipal");
        BulbBridgeAddress address = null;
        BulbsPrincipal principal = null;
        BulbsPlatform platform = null;
        BulbsHwServiceImpl instance = new BulbsHwServiceImpl();
        HwResponse expResult = null;
        InvocationResponse result = instance.createBulbsPrincipal(address, principal, platform);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    //@Test
    public void testRemoveBulbsPrincipal() throws Exception {
        System.out.println("removeBulbsPrincipal");
        BulbBridgeAddress address = null;
        BulbsPrincipal principal = null;
        BulbsPrincipal principal2Remove = null;
        BulbsPlatform platform = null;
        BulbsHwServiceImpl instance = new BulbsHwServiceImpl();
        HwResponse expResult = null;
        HwResponse result = instance.removeBulbsPrincipal(address, principal, principal2Remove, platform);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    //@Test
    public void testDiscoverNewBulbs() throws Exception {
        System.out.println("discoverNewBulbs");
        BulbBridgeId bridgeId = null;
        BulbBridgeAddress address = null;
        BulbsPrincipal principal = null;
        BulbsPlatform platform = null;
        BulbsHwServiceImpl instance = new BulbsHwServiceImpl();
        HwResponse expResult = null;
        instance.discoverNewBulbs(bridgeId, address, principal, platform);
//        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    //@Test
    public void testModifyBridgeAttributes() throws Exception {
        System.out.println("modifyBridgeAttributes");
        BulbBridgeAddress address = null;
        BulbsPrincipal principal = null;
        Map<String, Object> attributes = null;
        BulbsPlatform platform = null;
        BulbsHwServiceImpl instance = new BulbsHwServiceImpl();
        HwResponse expResult = null;
        HwResponse result = instance.modifyBridgeAttributes(address, principal, attributes, platform);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    //@Test
    public void testBulbsFromHwInterface() throws Exception {
        System.out.println("bulbsFromHwInterface");
        BulbBridge parentBridge = null;
        BulbsPrincipal principal = null;
        BulbsPlatform platform = null;
        BulbsHwServiceImpl instance = new BulbsHwServiceImpl();
        Bulb[] expResult = null;
        Bulb[] result = instance.bulbsFromHwInterface(parentBridge, principal, platform);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    //@Test
    public void testBulbFromHwInterface() throws Exception {
        System.out.println("bulbFromHwInterface");
        BulbId bulbId = null;
        BulbBridge parentBridge = null;
        BulbsPrincipal principal = null;
        BulbsPlatform platform = null;
        BulbsHwServiceImpl instance = new BulbsHwServiceImpl();
        Bulb expResult = null;
        Bulb result = instance.bulbFromHwInterface(bulbId, parentBridge, principal, platform);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    //@Test
    public void testModifyBulbAttributes() throws Exception {
        System.out.println("modifyBulbAttributes");
        BulbId bulbId = null;
        BulbBridgeAddress address = null;
        BulbsPrincipal principal = null;
        Map<String, Object> attributes = null;
        BulbsPlatform platform = null;
        BulbsHwServiceImpl instance = new BulbsHwServiceImpl();
        HwResponse expResult = null;
        InvocationResponse result = instance.modifyBulbAttributes(bulbId, address, principal, attributes, platform);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testCancelActuation() throws BulbBridgeHwException {
        System.out.println("testCancelActuation");
        final int COUNT_STATES = 50;
        BulbId bId = new BulbId(new BulbBridgeId("brId"), "1");
        BulbActuatorCommand command = new BulbActuatorCommand(
                bId,
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
        BulbsPlatform platform = BulbsPlatform.PHILIPS_HUE;
        when(mk_domainServiceLocator.getBeanInternal(DomainEventStore.class)).thenReturn(mk_eventStore);
        when(mk_domainServiceLocator.getBeanInternal(DomainEventPublisherDeferrer.class)).thenReturn(null);
        when(mk_eventStore.store(any(DomainEvent.class))).thenReturn(-1l);

        instance2Test.executeBulbActuation(T_BRIDGE_ADDRESS, principal, command, null, platform);

        try{
            Thread.sleep(80);
        }catch(InterruptedException iex){}

        instance2Test.cancelActuation(bId);
        try{
            Thread.sleep(1000);
        }catch(InterruptedException iex){}
        verify(mk_hwAdapter, VerificationModeFactory.atLeast(1)).applyBulbState(
                any(BulbId.class), eq(T_BRIDGE_ADDRESS),
                eq(principal),
                any(BulbState.class),
                any(BulbsPlatform.class),
                any(BulbState.class)
        );
        Map<Object, CmdHwExecutor> execs = (Map) ReflectionTestUtils.getField(instance2Test, "runningExecutions");
        assertTrue(execs.get(bId).isExecutionFinished());
        
    }
    @Test
    public void testExecuteBulbActuation() throws Exception {
        System.out.println("executeBulbActuation");
        final int COUNT_STATES = 50;
         BulbId bId = new BulbId(new BulbBridgeId("brId"), "1");
        BulbActuatorCommand command = new BulbActuatorCommand(
                bId,
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
        BulbsPlatform platform = BulbsPlatform.PHILIPS_HUE;
        when(mk_domainServiceLocator.getBeanInternal(DomainEventStore.class)).thenReturn(mk_eventStore);
        when(mk_domainServiceLocator.getBeanInternal(DomainEventPublisherDeferrer.class)).thenReturn(null);
        when(mk_eventStore.store(any(DomainEvent.class))).thenReturn(-1l);
        when(mk_eventStore.store(any(DomainEvent.class))).thenReturn(1l);

        instance2Test.executeBulbActuation(T_BRIDGE_ADDRESS, principal, command, null, platform);

        Map<Object, CmdHwExecutor> execs = (Map) ReflectionTestUtils.getField(instance2Test, "runningExecutions");
        assertTrue(!execs.get(bId).isExecutionFinished());

        try{
            Thread.sleep(1000);
        }catch(InterruptedException iex){}
        verify(mk_hwAdapter, atLeast(1)).applyBulbState(
                any(BulbId.class), eq(T_BRIDGE_ADDRESS),
                eq(principal),
                any(BulbState.class),
                any(BulbsPlatform.class),
                any(BulbState.class)
        );
    }
}