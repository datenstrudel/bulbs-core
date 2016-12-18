package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

import net.datenstrudel.bulbs.core.AbstractBulbsIT;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroup;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroupId;
import net.datenstrudel.bulbs.core.domain.model.group.BulbGroupRepository;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Thomas Wendzinski
 */
public class BulbGroupRepositoryImplIT extends AbstractBulbsIT {
    
    private static final Logger log = LoggerFactory.getLogger(BulbGroupRepositoryImplIT.class);
    
    @Autowired
    private BulbGroupRepository instance;

    @Autowired
    private MongoTemplate mongo;

    private static boolean initialized = false;
    
    @Before
    public void setUp() {
        if(BulbGroupRepositoryImplIT.initialized) return ;
        BulbGroupRepositoryImplIT.initialized = true;
        mongo.dropCollection(BulbGroup.class);
    }

    @Test
    public void testNextIdentity() {
        System.out.println("nextIdentity");
        BulbsContextUserId creatorId = new BulbsContextUserId("test_userUuid");
        BulbGroupId result = instance.nextIdentity(creatorId);
        assertThat(result.getCreator(), is(creatorId));
    }

    @Test
    public void findById() {
        System.out.println("findOne");
        BulbGroup expResult = newTestBulbGroup();

        instance.save(expResult);
        BulbGroup result = instance.findOne(expResult.getId());

        assertEquals(expResult, result);
        assertEquals(expResult.getBulbs(), result.getBulbs());
        assertEquals(expResult.getName(), result.getName());
    }

    @Test
    public void findByNameAndGroupId_Creator() {
        System.out.println("findByNameAndId_Creator");
        BulbGroup expResult = newTestBulbGroup();

        instance.save(expResult);
        BulbGroup result = instance.findByNameAndId_Creator(
                expResult.getName(), expResult.getId().getCreator());

        assertEquals(expResult, result);
    }

    @Test
    public void findByGroupId_Creator() {
        System.out.println("findByOwner");
        LinkedHashSet<BulbGroup> expResult = new LinkedHashSet<>();
        BulbGroup g_0 = newTestBulbGroup();
        BulbGroup g_1 = newTestBulbGroup();
        ReflectionTestUtils.setField(g_1.getId(), "creator", g_0.getId().getCreator());
        expResult.add(g_0);
        expResult.add(g_1);

        Iterator<BulbGroup> it = (Iterator) expResult.iterator();
        instance.save(it.next());
        instance.save(it.next());
        Set<BulbGroup> result = instance.findById_Creator(expResult.iterator().next().userId());
        assertTrue(Objects.equals(expResult, result));
    }

    @Test
    public void delete() {
        System.out.println("delete");
        BulbGroup group = newTestBulbGroup();
        BulbGroupId groupId = group.getId();
        BulbGroup res;

        instance.save(group);
        res = instance.findOne(groupId);
        assertEquals(res, group);
        instance.delete(groupId); groupId.equals(res.getId());
        res = instance.findOne(groupId);
        assertNull(res);

    }

    private BulbGroup newTestBulbGroup(){
        BulbGroupId groupId = instance.nextIdentity(new BulbsContextUserId(UUID.randomUUID().toString()));
        BulbGroup expResult = new BulbGroup(groupId, "TestLoadById__GroupName");
        expResult.addBulb(new BulbId(new BulbBridgeId("TestLoadById__BulbIdBridge"), "0"));
        expResult.addBulb(new BulbId(new BulbBridgeId("TestLoadById__BulbIdBridge"), "1"));
        return expResult;
    }
}
