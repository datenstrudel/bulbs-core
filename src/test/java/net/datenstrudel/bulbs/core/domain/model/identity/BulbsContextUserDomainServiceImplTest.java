/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.datenstrudel.bulbs.core.domain.model.identity;

import net.datenstrudel.bulbs.shared.domain.model.identity.AppId;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author Thomas Wendzinski
 */
public class BulbsContextUserDomainServiceImplTest {
    
    private static final Logger log = LoggerFactory.getLogger(BulbsContextUserDomainServiceImplTest.class);
    
    public BulbsContextUserDomainServiceImplTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testBulbsPrincipalInstanceForNewBridge() {
        System.out.println("bulbsPrincipalInstanceForNewBridge");
        AppId appType = new AppId("testApp");
        BulbsContextUserDomainServiceImpl instance = new BulbsContextUserDomainServiceImpl();
        BulbsPrincipal result = instance.bulbsPrincipalInstanceForNewBridge(appType);
        
        log.info(result.toString());
        assertNotNull(result.getUsername());
        assertEquals(BulbsPrincipalState.REQUESTED, result.getState());
        assertEquals(appType, result.getAppId());
    }
}