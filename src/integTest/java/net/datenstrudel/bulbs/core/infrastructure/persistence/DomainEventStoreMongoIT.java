package net.datenstrudel.bulbs.core.infrastructure.persistence;

import jdk.nashorn.internal.ir.annotations.Ignore;
import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.DomainEventStore;
import net.datenstrudel.bulbs.core.application.messaging.eventStore.StoredEvent;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEvent;
import net.datenstrudel.bulbs.core.infrastructure.PersistenceConfig;
import net.datenstrudel.bulbs.shared.domain.model.bulb.BulbState;
import net.datenstrudel.bulbs.shared.domain.model.color.ColorRGB;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Thomas Wendzinski
 */
@ContextConfiguration(
initializers = TestConfig.class,
classes = {
        PersistenceConfig.class,
        TestConfig.class
})
@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class DomainEventStoreMongoIT {
    
    private static final Logger log = LoggerFactory.getLogger(DomainEventStoreMongoIT.class);
    
    @Autowired
    DomainEventStore instance;
    @Autowired
    private MongoTemplate mongo;
    
    public DomainEventStoreMongoIT() {
    }
    
    @After
    public void destroy() {
        mongo.dropCollection(StoredEvent.class);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {}
    }

//    @Test
    public void testLoadById() {
        // See testStore()
    }
    @Test
    public void testNextIdentity(){
        System.out.println("nextIdentity");
        Long res_0 = instance.nextIdentity();
        log.info("StoredEvent ID generated: " + res_0);
        Long res_1 = instance.nextIdentity();
        log.info("StoredEvent ID generated: " + res_1);
        Long res_2 = instance.nextIdentity();
        log.info("StoredEvent ID generated: " + res_2);
        assertTrue( res_0 < res_1  && res_1 < res_2);
        
    }
    @Test
    public void testStore() {
        System.out.println("save");
        DomainEvent event = new TestEvent(
                new BulbState(new ColorRGB(1, 2, 3), true), 
                "testDId", 
                new Date(0l));
        
        Long storedEventId = instance.store(event);
        
        StoredEvent res = instance.loadById(storedEventId);
        assertEquals(event, instance.fromStoredEvent(res));
    }
    @Test
    public void testLoadAllStoredEventsSince(){
        System.out.println("testLoadAllStoredEventsSince");
        DomainEvent event_0 = new TestEvent(
                new BulbState(new ColorRGB(1, 2, 3), true), 
                "testDId_0", 
                new Date(0l));
        DomainEvent event_1 = new TestEvent(
                new BulbState(new ColorRGB(1, 2, 3), true), 
                "testDId_1", 
                new Date(1000l));
        
        Long storedEventId_0 = instance.store(event_0);
        Long storedEventId_1 = instance.store(event_1);
        
        List<StoredEvent> res = instance.loadAllStoredEventsSince(storedEventId_0-1);
        assertEquals(2, res.size());
        res = instance.loadAllStoredEventsSince(storedEventId_0);
        assertEquals(event_1, instance.fromStoredEvent(res.iterator().next()) );
        res = instance.loadAllStoredEventsSince(storedEventId_1);
        assertTrue(res.isEmpty());
    }
    
    private static final class TestEvent implements DomainEvent{

        private BulbState state;
        private String domainId;
        private Date occuredOn = new Date();

        public TestEvent(BulbState state, String domainId, Date occuredOn) {
            this.state = state;
            this.domainId = domainId;
            this.occuredOn = occuredOn;
        }

        public BulbState getState() {
            return state;
        }
        public void setState(BulbState state) {
            this.state = state;
        }

        public void setOccuredOn(Date occuredOn) {
            this.occuredOn = occuredOn;
        }

        @Override
        public String getDomainId() {
            return domainId;
        }
        @Override
        public Date getOccurredOn() {
            return occuredOn;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + Objects.hashCode(this.state);
            hash = 71 * hash + Objects.hashCode(this.domainId);
            hash = 71 * hash + Objects.hashCode(this.occuredOn);
            return hash;
        }
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TestEvent other = (TestEvent) obj;
            if (!Objects.equals(this.state, other.state)) {
                return false;
            }
            if (!Objects.equals(this.domainId, other.domainId)) {
                return false;
            }
            if (!Objects.equals(this.occuredOn, other.occuredOn)) {
                return false;
            }
            return true;
        }
    }
}