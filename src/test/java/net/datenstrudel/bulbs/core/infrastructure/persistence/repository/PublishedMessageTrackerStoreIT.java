package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.PublishedMessageTracker;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.PublishedMessageTrackerStore;
import net.datenstrudel.bulbs.core.domain.model.preset.Preset;
import net.datenstrudel.bulbs.core.infrastructure.PersistenceConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Thomas Wendzinski.
 */
@ContextConfiguration(
        initializers = TestConfig.class,
        classes = {
                PersistenceConfig.class,
                TestConfig.class
        })
@org.junit.runner.RunWith(SpringJUnit4ClassRunner.class)
public class PublishedMessageTrackerStoreIT {

    @Autowired
    private PublishedMessageTrackerStore instance;
    @Autowired
    private MongoTemplate mongo;
    private static boolean initialized = false;

    @Before
    public void setUp() {
        if(PublishedMessageTrackerStoreIT.initialized) return ;
        PublishedMessageTrackerStoreIT.initialized = true;
        mongo.dropCollection(PublishedMessageTracker.class);
    }

    @Test
    public void findByType(){
        String expType = "test_pubMsgTrackerType_exp";
        String unExpType = "test_pubMsgTrackerType_unExp";
        PublishedMessageTracker expTracker = new PublishedMessageTracker(expType, 10);
        PublishedMessageTracker unExpTracker = new PublishedMessageTracker(unExpType, 10);
        instance.save(Arrays.asList(expTracker, unExpTracker));
        PublishedMessageTracker actual = instance.findByType(expType);
        assertPublishedMessageTracker(expTracker, actual);
    }

    private void assertPublishedMessageTracker(PublishedMessageTracker expected, PublishedMessageTracker actual){
        assertThat(actual.getMostRecentPublishedStoredEventId(), is(expected.getMostRecentPublishedStoredEventId()));
        assertThat(actual.getType(), is(expected.getType()));
        assertThat(actual.getId(), is(expected.getId()));
    }

}
