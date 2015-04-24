package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.core.domain.model.group.BulbGroupRepository;
import net.datenstrudel.bulbs.core.domain.model.group.GroupActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.identity.AppIdCore;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserDomainService;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipal;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsPrincipalState;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetRepository;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 *
 * @author Thomas Wendzinski
 */
public class ActuatorDomainServiceImplTest {
    
    private ActuatorDomainServiceImpl instance = new ActuatorDomainServiceImpl();
    private BulbBridgeRepository mk_bridgeRepository;
    private BulbsContextUserDomainService mk_userService;
    private BulbGroupRepository mk_groupRepository;
    private PresetRepository mk_presetRepository;
    
    public ActuatorDomainServiceImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @Before
    public void setUp() {
        mk_bridgeRepository = mock(BulbBridgeRepository.class);
        ReflectionTestUtils.setField(instance, "bridgeRepository", mk_bridgeRepository);
        mk_userService = mock(BulbsContextUserDomainService.class);
        ReflectionTestUtils.setField(instance, "userService", mk_userService);
        
    }

//    @Test
    public void testExecuteDeferred() {
        System.out.println("executeDeferred");
        AbstractActuatorCmd command = null;
        ActuatorDomainServiceImpl instance = new ActuatorDomainServiceImpl();
        instance.executeDeferred(command);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
//    @Test
    public void testExecute_AbstractActuatorCmd() throws Exception {
        System.out.println("execute");
        AbstractActuatorCmd command = null;
        ActuatorDomainServiceImpl instance = new ActuatorDomainServiceImpl();
        instance.execute(command);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
//    @Test
    public void testExecute_Collection() throws Exception {
        System.out.println("execute");
        Collection<? extends AbstractActuatorCmd> commands = null;
        ActuatorDomainServiceImpl instance = new ActuatorDomainServiceImpl();
        instance.execute(commands);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
//    @Test
    public void testExecute_BulbActuatorCommand() throws Exception {
        System.out.println("execute");
        BulbActuatorCommand bulbCommand = null;
        ActuatorDomainServiceImpl instance = new ActuatorDomainServiceImpl();
        instance.execute(bulbCommand);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
//    @Test
    public void testExecute_GroupActuatorCommand() throws Exception {
        System.out.println("execute");
        GroupActuatorCommand groupCmd = null;
        ActuatorDomainServiceImpl instance = new ActuatorDomainServiceImpl();
        instance.execute(groupCmd);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
//    @Test
    public void testExecute_PresetActuatorCommand() throws Exception {
        System.out.println("execute");
        PresetActuatorCommand presetCommand = null;
        ActuatorDomainServiceImpl instance = new ActuatorDomainServiceImpl();
        instance.execute(presetCommand);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    @Test
    public void testExecute_ActuationCancelCommand() throws Exception {
        BulbBridgeId[] bridges = {
                new BulbBridgeId("testCancel_brUuid_1"),
                new BulbBridgeId("testCancel_brUuid_2"),
                new BulbBridgeId("testCancel_brUuid_3")};
        ActuationCancelCommand cancelCommand = newCancelCmd(bridges);

        when(mk_userService.loadPrincipalByUserApiKey(any(String.class), any(AppId.class), any(BulbBridgeId.class)))
                .thenReturn(new BulbsPrincipal("testUsername", AppIdCore.instance(), "testBulbBridge", BulbsPrincipalState.REGISTERED));
        
        ArgumentCaptor<ActuationCancelCommand> captCmd = ArgumentCaptor.forClass(ActuationCancelCommand.class);
        BulbBridge mk_br1, mk_br2, mk_br3;
        mk_br1 = mock(BulbBridge.class);
        mk_br2 = mock(BulbBridge.class);
        mk_br3 = mock(BulbBridge.class);

        when(mk_bridgeRepository.findOne(bridges[0])).thenReturn(mk_br1);
        when(mk_bridgeRepository.findOne(bridges[1])).thenReturn(mk_br2);
        when(mk_bridgeRepository.findOne(bridges[2])).thenReturn(null);

        instance.execute(cancelCommand);

        verify(mk_br1, times(1)).cancelActuation(captCmd.capture(), any(BulbsPrincipal.class));
        verify(mk_br2, times(1)).cancelActuation(captCmd.capture(), any(BulbsPrincipal.class));
        verify(mk_br3, times(0)).cancelActuation(captCmd.capture(), any(BulbsPrincipal.class));

        List<ActuationCancelCommand> captCmds = captCmd.getAllValues();
        assertEquals(2, captCmds.size());
        assertEquals(2, captCmds.get(0).getEntityIds().size() );
        assertEquals(2, captCmds.get(1).getEntityIds().size() );
        assertTrue(!captCmds.get(0).getEntityIds().equals(captCmds.get(1).getEntityIds()) );
    }
//    @Test
    public void testPublishDeferred() throws Exception {
        System.out.println("publishDeferred");
        ActuatorDomainServiceImpl instance = new ActuatorDomainServiceImpl();
        instance.publishDeferred();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    private ActuationCancelCommand newCancelCmd(BulbBridgeId... bridgeIds){
        Set<BulbId> entityIds = new HashSet<>();
        for (BulbBridgeId brId : bridgeIds) {
            for (int i = 0; i < 2; i++) {
                entityIds.add(new BulbId(brId, ""+i));
            }
        }
        ActuationCancelCommand res = new ActuationCancelCommand(
                entityIds, AppIdCore.instance(), "testUserApiKey", CommandPriority.override());
        return res;
    }
}
