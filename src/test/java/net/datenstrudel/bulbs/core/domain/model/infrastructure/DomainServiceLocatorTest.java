package net.datenstrudel.bulbs.core.domain.model.infrastructure;

import net.datenstrudel.bulbs.core.IntegrationTest;
import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.config.BulbsCoreConfig;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbsHwService;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 *
 * @author Thomas Wendzinski
 */
@ContextConfiguration(
    initializers = TestConfig.class,
    classes = {
        TestConfig.class,
        BulbsCoreConfig.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Category(value = IntegrationTest.class)
public class DomainServiceLocatorTest {
    
    public DomainServiceLocatorTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testInstance() {
        System.out.println("instance");
        DomainServiceLocator result = DomainServiceLocator.instance();
        assertNotNull(result);
    }

    @Test
    public void testGetBean() {
        System.out.println("getBean");
        Object result = DomainServiceLocator.getBean(BulbsHwService.class);
        assertNotNull(result);
    }

//    @Test
    public void testSetApplicationContext() {
        System.out.println("setApplicationContext");
        ApplicationContext ac = null;
        DomainServiceLocator instance = new DomainServiceLocator();
        instance.setApplicationContext(ac);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
