package net.datenstrudel.bulbs.core.domain.model.infrastructure;

import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.config.BulbsCoreConfig;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbsHwService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

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
public class DomainServiceLocatorIT {
    
    public DomainServiceLocatorIT() {
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

}
