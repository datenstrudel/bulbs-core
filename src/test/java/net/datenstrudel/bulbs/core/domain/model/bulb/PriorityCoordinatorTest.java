package net.datenstrudel.bulbs.core.domain.model.bulb;

import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Thomas Wendzinski
 */
public class PriorityCoordinatorTest {
    
    public PriorityCoordinatorTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testExecutionAllowedFor() throws InterruptedException {
        System.out.println("executionAllowedFor");
        BulbActuatorCommand cmd_0 = new BulbActuatorCommand(
                null, 
                new AppId("testApp_0"), 
                "testUserApiKey", 
                CommandPriority.standard(), 
                null);
        PriorityCoordinator instance = new PriorityCoordinator();
        boolean result = instance.executionAllowedFor(cmd_0);
        assertEquals(true, result);
        
        BulbActuatorCommand cmd_1 = new BulbActuatorCommand(
                null, 
                new AppId("testApp_1"), 
                "testUserApiKey", 
                CommandPriority.override(), 
                null);
        BulbActuatorCommand cmd_2 = new BulbActuatorCommand(
                null, 
                new AppId("testApp_1"), 
                "testUserApiKey", 
                CommandPriority.standard(), 
                null);
        // Override
        result = instance.executionAllowedFor(cmd_1);
        assertEquals(true, result);
        // Not in charge
        result = instance.executionAllowedFor(cmd_0);
        assertEquals(false, result);
        // Still in charge
        result = instance.executionAllowedFor(cmd_2);
        assertEquals(true, result);
        
        // Override Temporary
        BulbActuatorCommand cmd_3 = new BulbActuatorCommand(
                null, 
                new AppId("testApp_2"), 
                "testUserApiKey", 
                CommandPriority.overrideTemporary(3000), 
                null);
        result = instance.executionAllowedFor(cmd_3);
        assertEquals(true, result);
        
        // Suspended
        result = instance.executionAllowedFor(cmd_2);
        assertEquals(false, result);
        
        Thread.sleep(3001);
        // In charge again
        result = instance.executionAllowedFor(cmd_2);
        assertEquals(true, result);
        
    }

}