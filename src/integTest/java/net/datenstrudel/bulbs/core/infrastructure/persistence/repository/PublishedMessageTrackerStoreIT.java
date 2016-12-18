package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

import net.datenstrudel.bulbs.core.AbstractBulbsIT;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.PublishedMessageTracker;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.PublishedMessageTrackerStore;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by Thomas Wendzinski.
 */
public class PublishedMessageTrackerStoreIT extends AbstractBulbsIT{

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
