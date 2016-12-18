package net.datenstrudel.bulbs.core.domain.model.infrastructure;

import net.datenstrudel.bulbs.core.AbstractBulbsIT;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbsHwService;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * @author Thomas Wendzinski
 */
public class DomainServiceLocatorIT extends AbstractBulbsIT {

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
