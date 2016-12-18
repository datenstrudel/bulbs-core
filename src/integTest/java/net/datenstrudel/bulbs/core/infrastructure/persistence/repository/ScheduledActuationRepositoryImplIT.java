package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

import net.datenstrudel.bulbs.core.AbstractBulbsIT;
import net.datenstrudel.bulbs.core.domain.model.bulb.AbstractActuatorCmd;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbBridgeId;
import net.datenstrudel.bulbs.core.domain.model.bulb.BulbId;
import net.datenstrudel.bulbs.core.domain.model.identity.AppIdCore;
import net.datenstrudel.bulbs.core.domain.model.identity.BulbsContextUserId;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetActuatorCommand;
import net.datenstrudel.bulbs.core.domain.model.preset.PresetId;
import net.datenstrudel.bulbs.core.domain.model.scheduling.JobCoordinator;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuation;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationId;
import net.datenstrudel.bulbs.core.domain.model.scheduling.ScheduledActuationRepository;
import net.datenstrudel.bulbs.shared.domain.model.bulb.CommandPriority;
import net.datenstrudel.bulbs.shared.domain.model.scheduling.PointInTimeTrigger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.mock;

/**
 * @author Thomas Wendzinski
 */
public class ScheduledActuationRepositoryImplIT extends AbstractBulbsIT{

    @Autowired
    private ScheduledActuationRepository instance;
    @Autowired
    private MongoTemplate mongo;

    private JobCoordinator mk_jobCoordinator;

    @Before
    public void setUp() {
        mongo.dropCollection(ScheduledActuation.class);
        mk_jobCoordinator = mock(JobCoordinator.class);
    }

    @Test
    public void nextIdentity() {
        System.out.println("nextIdentity");
        BulbsContextUserId creator = new BulbsContextUserId("testNextId__userId");
        ScheduledActuationId result = instance.nextIdentity(creator);
        assertThat(result.getCreator(), is(creator));
        assertThat("Identity uuid exists", !StringUtils.isEmpty(result.getUuid()));
    }

    @Test
    public void findOne() {
        System.out.println("findOne");
        BulbsContextUserId creator = new BulbsContextUserId("findById__userId");
        ScheduledActuationId id = instance.nextIdentity(creator);
        ScheduledActuation expResult = newTestInstance(id, true, true);

        instance.save(expResult);
        ScheduledActuation result = instance.findOne(id);

        assertThat(result, is(expResult));
        assertThat(result.getTrigger(), is(expResult.getTrigger()));
        assertThat(result.getStates(), is(expResult.getStates()));
        assertThat(result.getName(), is(expResult.getName()));
        assertThat(result.getCreated(), is(expResult.getCreated()));
        assertThat(result.isDeleteAfterExecution(), is(expResult.isDeleteAfterExecution()));

    }

    @Test
    public void findByNameAndScheduleId_Creator() {
        System.out.println("findByName");
        BulbsContextUserId creator = new BulbsContextUserId("testLoadById__userId");
        ScheduledActuationId id = instance.nextIdentity(creator);
        ScheduledActuation expResult = newTestInstance(id, true, false);

        instance.save(expResult);
        ScheduledActuation result = instance.findByNameAndId_Creator(expResult.getName(), creator);
        assertThat(result, is(expResult));
        assertThat(result.getTrigger(), is(expResult.getTrigger()));
    }
    @Test
    public void findByScheduleId_Owner() {
        System.out.println("findByScheduleId_Owner");
        BulbsContextUserId userXp = new BulbsContextUserId("test_UserUuid_exp");
        BulbsContextUserId userUXp = new BulbsContextUserId("test_UserUuid_unExp");
        ScheduledActuation xp1 = newTestInstance(instance.nextIdentity(userXp), false, false);
        ScheduledActuation xp2 = newTestInstance(instance.nextIdentity(userXp), true, false);
        ScheduledActuation uxp1 = newTestInstance(instance.nextIdentity(userUXp), false, false);
        Set<ScheduledActuation> expResult = new HashSet<>();
        expResult.add(xp1);
        expResult.add(xp2);

        instance.save(Arrays.asList(uxp1, xp1, xp2));
        Set<ScheduledActuation> result = instance.findById_Creator(userXp);
        assertThat(result, is(expResult));
    }

    public void testStore() {
        // -> See testLoadById()
    }

    @Test
    public void delete() {
        System.out.println("delete");
        BulbsContextUserId creator = new BulbsContextUserId("testLoadById__userId");
        ScheduledActuationId id = instance.nextIdentity(creator);
        ScheduledActuation expResult = newTestInstance(id, true, true);

        instance.save(expResult);
        ScheduledActuation res = instance.findOne(id);
        assertThat(res, is(notNullValue()));
        instance.delete(id);
        res = instance.findOne(id);
        assertThat(res, is(nullValue()));
    }

    private ScheduledActuation newTestInstance(ScheduledActuationId id, boolean withTrigger, boolean withStates) {
        final ScheduledActuation res;
        if (withTrigger) {
            PointInTimeTrigger trigger = new PointInTimeTrigger(new Date(), "UTC");
            trigger.toCronExpression(); // create cron exp internally
            res = new ScheduledActuation(
                    id,
                    "testLoadById_actName",
                    trigger, true);
        } else {
            res = new ScheduledActuation(id, "test_name", false);
        }

        if (withStates) {
            final List<AbstractActuatorCmd> states = new ArrayList<>();
            final BulbBridgeId bridgeId = new BulbBridgeId("testSchedActRep_bridgeId_0");
            states.add(new BulbActuatorCommand(new BulbId(bridgeId, "1"), AppIdCore.instance(), "testUserApiKey",
                    new CommandPriority(0), new LinkedList<>()));
            states.add(new BulbActuatorCommand(new BulbId(bridgeId, "2"), AppIdCore.instance(), "testUserApiKey",
                    new CommandPriority(0), new LinkedList<>()));
            states.add(new BulbActuatorCommand(new BulbId(bridgeId, "3"), AppIdCore.instance(), "testUserApiKey",
                    new CommandPriority(0), new LinkedList<>()));
            states.add(new PresetActuatorCommand(AppIdCore.instance(), "testUserApiKe", new CommandPriority(0),
                    new PresetId("testPresetUuid-generic", id.getCreator()), false));
            res.setNewStates(states, mk_jobCoordinator);
        }

        return res;
    }
}
