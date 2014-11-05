package net.datenstrudel.bulbs.core.infrastructure;

import net.datenstrudel.bulbs.core.IntegrationTest;
import net.datenstrudel.bulbs.core.TestConfig;
import net.datenstrudel.bulbs.core.config.BulbsCoreConfig;
import net.datenstrudel.bulbs.core.domain.model.messaging.DomainEventPublisher;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        initializers = TestConfig.class,
        classes = {
                TestConfig.class,
                BulbsCoreConfig.class,
                PersistenceConfig.class
        }
)
@Category(value = IntegrationTest.class)
public class Runnable_EventPublishingAwareTest {

    @Autowired
    @Qualifier("taskExecutor")
    private AsyncTaskExecutor asyncExecutor;


    private volatile boolean testListenToDomainEvents_withinAsyncContext_passed = false;

    @Test
    public void listenToDomainEvents_withinAsyncContext() throws Exception {
        System.out.println("listenToDomainEvents_withinAsyncContext");

        asyncExecutor.execute(new Runnable_EventPublishingAware(){
            @Override
            public void execute() {
                System.out.println("Async Runnable executed.");
                DomainEventPublisher publisher = DomainEventPublisher.instance();

                ThreadLocal<Map> expSubscr = (ThreadLocal<Map>) ReflectionTestUtils.getField(publisher, "subscribers");
                testListenToDomainEvents_withinAsyncContext_passed = !expSubscr.get().isEmpty();
            }
        });
        Thread.sleep(200l);
        assertTrue(testListenToDomainEvents_withinAsyncContext_passed);
    }

}